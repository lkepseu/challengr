import React, { useEffect, useRef, useState } from 'react'
import imageCompression from 'browser-image-compression'


const API = import.meta.env.VITE_API_URL || ''

function Auth({onDone}){
    const [email,setEmail]=useState(''); const [password,setPassword]=useState(''); const [handle,setHandle]=useState('');
    const saveToken = (res)=>{ localStorage.setItem('token', res.token); localStorage.setItem('handle', res.handle); onDone(); };
    const doRegister = async ()=>{
        const r=await fetch('${API}/api/auth/register',{method:'POST',headers:{'Content-Type':'application/json'},
            body:JSON.stringify({email,password,handle})}); if(r.ok) saveToken(await r.json()); onDone();
    };
    const doLogin = async ()=>{
        const r=await fetch('${API}/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},
            body:JSON.stringify({email,password})}); if(r.ok) saveToken(await r.json());
    };
    return <div style={{maxWidth:380,margin:'40px auto',fontFamily:'system-ui'}}>
        <h2>Sign in to Challengr</h2>
        <input placeholder="email" value={email} onChange={e=>setEmail(e.target.value)} style={{width:'100%',margin:'8px 0',padding:8}}/>
        <input placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)} style={{width:'100%',margin:'8px 0',padding:8}}/>
        <input placeholder="handle (optional)" value={handle} onChange={e=>setHandle(e.target.value)} style={{width:'100%',margin:'8px 0',padding:8}}/>
        <div style={{display:'flex',gap:8}}>
            <button onClick={doLogin}>Login</button>
            <button onClick={doRegister}>Register</button>
        </div>
    </div>
}


function useScrollHelpers() {
    const listRef = useRef(null)
    const initialScrollDoneRef = useRef(false)
    const justSubmittedRef = useRef(false)

    const isNearBottom = () => {
        const el = listRef.current
        if (!el) return true
        const delta = el.scrollHeight - el.scrollTop - el.clientHeight
        return delta < 40 // marge
    }

    const scrollToBottom = () => {
        const el = listRef.current
        if (el) el.scrollTop = el.scrollHeight
    }

    return { listRef, initialScrollDoneRef, justSubmittedRef, isNearBottom, scrollToBottom }
}


export default function App(){
    const [auth,setAuth]=useState(!!localStorage.getItem('token'));
    const [today,setToday]=useState(null)
    const [subs,setSubs]=useState([])
    const [content,setContent]=useState('')
    const [file,setFile]=useState(null)           // <-- fichier upload
    const [description,setDescription]=useState('')

    const handle = localStorage.getItem('handle') || 'guest'
    const challengeId = 1

    const {
        listRef, initialScrollDoneRef, justSubmittedRef,
        isNearBottom, scrollToBottom
    } = useScrollHelpers()

    useEffect(()=>{
        if(!auth) return
        fetch(`${API}/api/challenges/today`).then(r=>r.json()).then(setToday)
        refresh()
        // eslint-disable-next-line
    },[auth])

    const refresh = async ()=>{
        const atBottomBefore = isNearBottom()
        const data = await fetch(`${API}/api/submissions?challengeId=${challengeId}`).then(r=>r.json())
        // tri: ancien -> récent (les récents en BAS)
        data.sort((a,b)=> new Date(a.createdAt ?? 0).getTime() - new Date(b.createdAt ?? 0).getTime())
        setSubs(data)

        // placer le scroll tout en bas UNIQUEMENT :
        // - au 1er rendu
        // - si l’utilisateur était déjà en bas
        // - si on vient juste de soumettre
        requestAnimationFrame(()=>{
            if (!initialScrollDoneRef.current) {
                scrollToBottom(); initialScrollDoneRef.current = true; return
            }
            if (justSubmittedRef.current) { scrollToBottom(); justSubmittedRef.current = false; return }
            if (atBottomBefore) scrollToBottom()
        })
    }

    const submit = async ()=>{
        if(!content.trim() && !file) return
        const options = { maxWidthOrHeight: 100, maxSizeMB: 1 }
        const compressedFile = await imageCompression(file, options)

        const token = localStorage.getItem('token')

        const form = new FormData()
        form.append('challengeId', challengeId)
        form.append('userHandle', handle)
        if (content.trim()) form.append('content', content)
        if (description.trim()) form.append('description', description)
        if (file) form.append('file', file)

        await fetch(`${API}/api/submissions`,{
            method:'POST',
            headers:{ ...(token?{'Authorization':`Bearer ${token}`}:{}) },
            body: form
        })

        setContent(''); setDescription(''); setFile(null)
        justSubmittedRef.current = true
        await refresh()
    }

    const like = async (id)=>{
        const token = localStorage.getItem('token')
        await fetch(`${API}/api/submissions/${id}/like`,{
            method:'PATCH',
            headers:{ ...(token?{'Authorization':`Bearer ${token}`}:{}) }
        })
        await refresh()
    }

    if(!auth) return <Auth onDone={()=>setAuth(true)}/>

    return (
        <div style={{maxWidth:720, margin:'0 auto', padding:16, fontFamily:'system-ui'}}>
            <h1>Challengr</h1>
            {today && <div style={{padding:6, border:'1px solid #ddd', borderRadius:12, marginBottom:12, fontSize:18}}>
                <span style={{fontWeight:'bold'}}>Today:</span> {today.title} {today.prompt}</div>}

            {/* Formulaire de soumission */}
            <div style={{display:'grid', gap:8, marginBottom:4}}>
                <input type="file" accept="image/*" onChange={e => setFile(e.target.files?.[0] || null)}/>
                <input value={description} onChange={e=>setDescription(e.target.value)} placeholder="Short description (optional)" />
                <div>
                    <button onClick={submit}>Submit</button>
                </div>
            </div>

            {/* Liste scrollable MANUELLEMENT */}
            <h3>Latest submissions</h3>
            <div
                ref={listRef}
                style={{
                    height: 470,           // visible window
                    overflowY: 'auto',     // scroll souris
                    borderRadius: 12,
                    border: '1px solid #eee',
                    padding: 12
                }}
            >
                <ul style={{listStyle:'none', padding:0, margin:0, display:'flex', flexDirection:'column', gap:12}}>
                    {subs.map(s=>(
                        <li key={s.id} style={{border:'1px solid #eee', borderRadius:12, padding:12}}>
                            <div style={{fontSize:12, opacity:.7}}>@{s.userHandle}</div>
                            {s.imageUrl
                                ? <img src={s.imageUrl} alt="" style={{width:'70%', maxWidth:'50%', maborderRadius:8}}/>
                                : <div style={{whiteSpace:'pre-wrap'}}>{s.content}</div>}
                            {s.description && <div style={{opacity:.8, marginTop:6}}>{s.description}</div>}
                            <div style={{marginTop:8}}>
                                <button onClick={()=>like(s.id)}>❤️ {s.likesCount ?? 0}</button>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>
            <button onClick={()=>{localStorage.clear(); location.reload();}} style={{marginTop:8}}>Logout</button>
        </div>
    )
}

