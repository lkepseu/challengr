import React, { useState } from 'react'
import { postJson } from '../utils/api'

export default function Auth({ onDone }) {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [handle, setHandle] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const saveSession = ({ token, handle }) => {
        localStorage.setItem('token', token)
        localStorage.setItem('handle', handle)
        onDone()
    }

    const handleAuth = async (endpoint, payload) => {
        try {
            setError('')
            setLoading(true)
            const data = await postJson(endpoint, payload)
            saveSession(data)
        } catch (err) {
            setError(err.message || 'Something went wrong')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-gradient-to-b from-gray-100 to-gray-200 px-4">
            <div className="w-full max-w-sm rounded-2xl bg-white p-8 shadow-lg border border-gray-200">
                <h2 className="text-2xl font-semibold text-gray-800 text-center mb-6">
                    🔥 Sign in to <span className="text-indigo-600">Challengr</span>
                </h2>

                <div className="space-y-4">
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        className="w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                    />

                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        className="w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                    />

                    <input
                        placeholder="Handle (optional)"
                        value={handle}
                        onChange={e => setHandle(e.target.value)}
                        className="w-full rounded-lg border px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                    />
                </div>

                {error && <p className="mt-3 text-sm text-red-500 text-center">{error}</p>}

                <div className="mt-6 flex justify-between gap-3">
                    <button
                        onClick={() => handleAuth('/api/auth/login', { email, password })}
                        disabled={loading}
                        className="flex-1 rounded-lg bg-indigo-600 text-white py-2 font-medium hover:bg-indigo-700 transition disabled:opacity-50"
                    >
                        {loading ? 'Loading...' : 'Login'}
                    </button>

                    <button
                        onClick={() => handleAuth('/api/auth/register', { email, password, handle })}
                        disabled={loading}
                        className="flex-1 rounded-lg bg-gray-100 text-gray-700 py-2 font-medium hover:bg-gray-200 transition disabled:opacity-50"
                    >
                        Register
                    </button>
                </div>
            </div>
        </div>
    )
}
