import React, { useState } from 'react'
import { postJson } from '../utils/api'

export default function Auth({ onDone }) {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [handle, setHandle] = useState('')

    const saveSession = ({ token, handle }) => {
        localStorage.setItem('token', token)
        localStorage.setItem('handle', handle)
        onDone()
    }

    const doRegister = async () => {
        const data = await postJson('/api/auth/register', { email, password, handle })
        saveSession(data)
    }

    const doLogin = async () => {
        const data = await postJson('/api/auth/login', { email, password })
        saveSession(data)
    }

    return (
        <div style={{ maxWidth: 380, margin: '40px auto', fontFamily: 'system-ui' }}>
            <h2>Sign in to Challengr</h2>
            <input placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
            <input
                placeholder="Password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
            />
            <input
                placeholder="Handle (optional)"
                value={handle}
                onChange={e => setHandle(e.target.value)}
            />
            <div style={{ display: 'flex', gap: 8 }}>
                <button onClick={doLogin}>Login</button>
                <button onClick={doRegister}>Register</button>
            </div>
        </div>
    )
}
