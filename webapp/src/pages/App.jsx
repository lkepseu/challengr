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
    const challengeId = 3
    const { listRef, scrollToBottom } = useScrollHelpers()

    const refresh = async () => {
        const data = await getJson(`/api/submissions?challengeId=${challengeId}`)
        data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
        setSubs(data)
    }

    const like = async (id) => {
        await fetch(`${API}/api/submissions/${id}/like`, {
            method: 'PATCH',
            headers: authHeader(),
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
        <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 font-sans">
            <div className="max-w-2xl mx-auto px-4 py-6">
                {/* Header */}
                <header className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-indigo-600">Challengr ⚡</h1>
                    <button
                        onClick={() => {
                            localStorage.clear()
                            location.reload()
                        }}
                        className="text-sm text-gray-500 hover:text-red-500 transition"
                    >
                        Logout
                    </button>
                </header>

                {/* Today's challenge */}
                {today && (
                    <div className="mb-6 p-4 rounded-xl border border-gray-200 bg-white shadow-sm">
                        <h2 className="text-lg font-semibold text-gray-800 mb-1">🔥 Today’s Challenge</h2>
                        <p className="text-gray-600">
                            <b>{today.title}</b> — {today.prompt}
                        </p>
                    </div>
                )}

                {/* Submission form */}
                <SubmissionForm challengeId={challengeId} handle={handle} onSubmitted={refresh} />

                {/* Submissions */}
                <div
                    ref={listRef}
                    className="mt-6 h-[470px] overflow-y-auto rounded-2xl border border-gray-200 bg-white shadow-sm p-4"
                >
                    <SubmissionList subs={subs} onLike={like} />
                </div>

                {/* Footer */}
                <footer className="mt-8 text-center text-xs text-gray-400">
                    © {new Date().getFullYear()} Challengr — Dare. Create. Grow.
                </footer>
            </div>
        </div>
    )
}
