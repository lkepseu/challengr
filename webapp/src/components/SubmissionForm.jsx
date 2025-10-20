import React, { useState } from 'react'
import imageCompression from 'browser-image-compression'
import { API, authHeader } from '../utils/api'

export default function SubmissionForm({ challengeId, handle, onSubmitted }) {
    const [description, setDescription] = useState('')
    const [file, setFile] = useState(null)
    const [loading, setLoading] = useState(false)
    const [message, setMessage] = useState('')

    const submit = async () => {
        if (!description.trim() && !file) {
            setMessage('Please enter a description or select a file.')
            return
        }

        try {
            setLoading(true)
            setMessage('')

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
                headers: {
                    ...authHeader(),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    challengeId,
                    userHandle: handle,
                    description,
                    contentType: 'text',
                    content: description
                })
            })

            setDescription('')
            setFile(null)
            setMessage('✅ Submission sent successfully!')
            onSubmitted()
        } catch (err) {
            setMessage('❌ Error sending submission.')
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="max-w-md mx-auto mt-6 rounded-2xl bg-white p-6 shadow-md border border-gray-200">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">🚀 Submit your entry</h3>

            <div className="space-y-4">
                {/* (Caché pour le moment) Upload d’image */}
                <div className="hidden">
                    <input
                        type="file"
                        accept="image/*"
                        onChange={e => setFile(e.target.files?.[0] || null)}
                        className="w-full text-sm text-gray-600"
                    />
                </div>

                <textarea
                    value={description}
                    onChange={e => setDescription(e.target.value)}
                    placeholder="Describe your challenge submission (optional)"
                    className="w-full rounded-lg border px-4 py-2 text-gray-800 focus:ring-2 focus:ring-indigo-400 focus:outline-none resize-none"
                    rows="3"
                />

                <button
                    onClick={submit}
                    disabled={loading}
                    className="w-full rounded-lg bg-indigo-600 text-white font-medium py-2 hover:bg-indigo-700 transition disabled:opacity-50"
                >
                    {loading ? 'Submitting...' : 'Submit'}
                </button>

                {message && (
                    <p
                        className={`text-center text-sm ${
                            message.startsWith('✅') ? 'text-green-600' : 'text-red-500'
                        }`}
                    >
                        {message}
                    </p>
                )}
            </div>
        </div>
    )
}
