import React, { useEffect, useState } from 'react'

const API_BASE = '' // via Ingress: /api/... sur le même host

export default function App(){
    const [today,setToday]=useState(null)
    const [subs,setSubs]=useState([])
    const [content,setContent]=useState('')
    const challengeId = 1

    useEffect(()=>{
        fetch(`/api/challenges/today`).then(r=>r.json()).then(setToday)
        fetch(`/api/submissions?challengeId=${challengeId}`).then(r=>r.json()).then(setSubs)
    },[])

    const submit = async ()=>{
        if(!content.trim()) return
        await fetch(`/api/submissions`,{
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body: JSON.stringify({challengeId, userHandle:"guest", contentType:"text", content})
        })
        setContent('')
        const refreshed = await fetch(`/api/submissions?challengeId=${challengeId}`).then(r=>r.json())
        setSubs(refreshed)
    }

    return (
        <div style={{maxWidth:720, margin:'0 auto', padding:16, fontFamily:'system-ui'}}>
            <h1>Challengr</h1>
            {today ? (
                <div style={{padding:12, border:'1px solid #ddd', borderRadius:12, marginBottom:16}}>
                    <h2>Today: {today.title}</h2>
                    <p>{today.prompt}</p>
                </div>
            ): <p>Loading...</p>}

            <div style={{display:'flex', gap:8, marginBottom:16}}>
                <input value={content} onChange={e=>setContent(e.target.value)} placeholder="Your idea..." style={{flex:1, padding:8}}/>
                <button onClick={submit}>Submit</button>
            </div>

            <h3>Latest submissions</h3>
            <ul style={{listStyle:'none', padding:0, display:'grid', gap:12}}>
                {subs.map(s=>(
                    <li key={s.id} style={{border:'1px solid #eee', borderRadius:12, padding:12}}>
                        <div style={{fontSize:12, opacity:.7}}>@{s.userHandle}</div>
                        <div>{s.content}</div>
                    </li>
                ))}
            </ul>
        </div>
    )
}
