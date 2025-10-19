import React from 'react'

export default function SubmissionList({ subs, onLike }) {
    return (
        <ul style={{ listStyle: 'none', padding: 0, display: 'flex', flexDirection: 'column', gap: 12 }}>
            {subs.map(s => (
                <li key={s.id} style={{ border: '1px solid #eee', borderRadius: 12, padding: 12 }}>
                    <div style={{ fontSize: 12, opacity: 0.7 }}>@{s.userHandle}</div>
                    {s.imageUrl ? (
                        <img src={s.imageUrl} alt="" style={{ width: '70%', borderRadius: 8 }} />
                    ) : (
                        <div style={{ whiteSpace: 'pre-wrap' }}>{s.content}</div>
                    )}
                    {s.description && <div style={{ opacity: 0.8, marginTop: 6 }}>{s.description}</div>}
                    <button onClick={() => onLike(s.id)}>❤️ {s.likesCount ?? 0}</button>
                </li>
            ))}
        </ul>
    )
}
