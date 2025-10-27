import React from 'react'

export default function SubmissionList({ subs, onLike }) {
    if (!subs?.length)
        return (
            <p className="text-center text-gray-500 mt-6 italic">
                No submissions yet. Be the first to share yours!
            </p>
        )

    return (
        <ul className="space-y-4 mt-6">
            {subs.map((s) => (
                <li
                    key={s.id}
                    className="border border-gray-200 rounded-2xl p-4 shadow-sm bg-white hover:shadow-md transition"
                >
                    {/* User handle */}
                    <div className="text-sm text-gray-500 mb-2">@{s.userHandle}</div>

                    {/* Image or Text content */}
                    {s.imageUrl ? (
                        <img
                            src={s.imageUrl}
                            alt=""
                            className="w-3/4 rounded-lg object-cover mb-3"
                        />
                    ) : (
                        <div className="whitespace-pre-wrap text-gray-800 mb-2">{s.content}</div>
                    )}

                    {/* Optional description */}
                    {s.description && (
                        <div className="text-gray-600 text-sm italic mt-2">{s.description}</div>
                    )}

                    {/* Hidden Like button (for now) */}
                    <div className="hidden">
                        <button
                            onClick={() => onLike(s.id)}
                            className="mt-3 text-red-500 hover:text-red-600 text-sm"
                        >
                            ❤️ {s.likesCount ?? 0}
                        </button>
                    </div>
                </li>
            ))}
        </ul>
    )
}
