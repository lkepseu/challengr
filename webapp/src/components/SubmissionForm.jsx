import React, { useState } from 'react'
import imageCompression from 'browser-image-compression'
import { API, authHeader } from '../utils/api'

export default function SubmissionForm({ challengeId, handle, onSubmitted }) {
    const [description, setDescription] = useState('')
    const [file, setFile] = useState(null)

    const submit = async () => {
        if (!file && !description.trim()) return

        const form = new FormData()
        form.append('challengeId', challengeId)
        form.append('userHandle', handle)
        if (description.trim()) form.append('description', description)

        if (file) {
            const compressed = await imageCompression(file, { maxWidthOrHeight: 100, maxSizeMB: 1 })
            form.append('file', compressed)
        }

        await fetch(`${API}/api/submissions`, {
            method: 'POST',
            headers: authHeader(),
            body: form
        })

        setFile(null)
        setDescription('')
        onSubmitted()
    }

    return (
        <div style={{ display: 'grid', gap: 8 }}>
            <input type="file" accept="image/*" onChange={e => setFile(e.target.files?.[0] || null)} />
            <input
                value={description}
                onChange={e => setDescription(e.target.value)}
                placeholder="Short description (optional)"
            />
            <button onClick={submit}>Submit</button>
        </div>
    )
}
