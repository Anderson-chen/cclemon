const { VUE_APP_USER_EVENT_BASE_URL } = process.env

type TrackEventType = 'API' | 'ROUTE' | 'CLICK' | 'ERROR'

interface TrackPayload {
    event: TrackEventType
    target: string
    method?: string
    timestamp?: string
    [key: string]: string | undefined
}

export function track(event: TrackEventType, target: string, extras: Record<string, string> = {}) {
    const payload: TrackPayload = {
        event,
        target,
        timestamp: new Date().toISOString(),
        ...extras,
    }

    const data = JSON.stringify(payload)

    try {
        if (typeof navigator.sendBeacon === 'function') {
            const ok = navigator.sendBeacon(`${VUE_APP_USER_EVENT_BASE_URL}/user-events`, data)
            if (!ok) {
                console.warn('sendBeacon failed', payload)
            }
        } else {
            // Fallback to fetch (e.g. for some environments like certain Cordova apps)
            fetch(`${VUE_APP_USER_EVENT_BASE_URL}/user-events`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: data,
                keepalive: true,
            }).catch(err => console.warn('Tracking fallback failed', err))
        }
    } catch (err) {
        console.warn('Track error:', err)
    }
}
