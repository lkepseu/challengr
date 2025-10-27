const API = import.meta.env.VITE_API_URL || ''

export const postJson = async (path, body) => {
    const res = await fetch(`${API}${path}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    })
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    return res.json()
}

export const getJson = async path => {
    const res = await fetch(`${API}${path}`)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    return res.json()
}

export const authHeader = () => {
    const token = localStorage.getItem('token')
    return token ? { Authorization: `Bearer ${token}` } : {}
}

export { API }
