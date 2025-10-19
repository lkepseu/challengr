import { useRef } from 'react'

export default function useScrollHelpers() {
    const listRef = useRef(null)
    const initialScrollDone = useRef(false)
    const justSubmitted = useRef(false)

    const isNearBottom = () => {
        const el = listRef.current
        if (!el) return true
        return el.scrollHeight - el.scrollTop - el.clientHeight < 40
    }

    const scrollToBottom = () => {
        const el = listRef.current
        if (el) el.scrollTop = el.scrollHeight
    }

    return { listRef, initialScrollDone, justSubmitted, isNearBottom, scrollToBottom }
}
