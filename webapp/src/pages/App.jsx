import React, { useEffect, useState } from 'react'
import Auth from '../components/Auth'
import SubmissionForm from '../components/SubmissionForm'
import SubmissionList from '../components/SubmissionList'
import useScrollHelpers from '../hooks/useScrollHelpers'
import { API, getJson, authHeader } from '../utils/api'

export default function App() {
    const [auth, setAuth] = useState(!!localStorage.getItem('token'))
    const [today, setToday] = useState(null)
    const [subs, setSubs] = useState([])
    const handle = localStorage.getItem('handle') || 'guest'
    const challengeId = 1
    const { listRef, isNearBottom, scrollToBottom, initialScrollDone, justSubmitted } =
        useScrollHelpers()

    const refresh = async () => {
        const data = await getJson(`/api/submissions?challengeId=${challengeId}`)
        data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
        setSubs(data)
    }

    const like = async id => {
        await fetch(`${API}/api/submissions/${id}/like`, {
            method: 'PATCH',
            headers: authHeader()
        })
        refresh()
    }

    useEffect(() => {
        if (!auth) return
        getJson('/api/challenges/today').then(setToday)
        refresh()
    }, [auth])

    if (!auth) return <Auth onDone={() => setAuth(true)} />

    return (
        <div style={{ maxWidth: 720, margin: '0 auto', padding: 16, fontFamily: 'system-ui' }}>
            <h1>Challengr</h1>
            {today && (
                <div style={{ padding: 6, border: '1px solid #ddd', borderRadius: 12, marginBottom: 12 }}>
                    <b>Today:</b> {today.title} {today.prompt}
                </div>
            )}
            <SubmissionForm challengeId={challengeId} handle={handle} onSubmitted={refresh} />
            <div ref={listRef} style={{ height: 470, overflowY: 'auto', borderRadius: 12, border: '1px solid #eee', padding: 12 }}>
                <SubmissionList subs={subs} onLike={like} />
            </div>
            <button onClick={() => { localStorage.clear(); location.reload(); }}>Logout</button>
        </div>
    )
}
