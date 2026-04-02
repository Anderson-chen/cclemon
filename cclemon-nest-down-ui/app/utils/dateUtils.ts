export function today(): string {
  return new Date().toISOString().split('T')[0]!
}

export function yesterday(): string {
  const d = new Date()
  d.setDate(d.getDate() - 1)
  return d.toISOString().split('T')[0]!
}

export function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-TW', { month: 'long', day: 'numeric' })
}

export function formatDateShort(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-TW', { month: 'numeric', day: 'numeric' })
}

export function formatTime(timeStr: string): string {
  return timeStr.substring(0, 5)
}

export function getLast7Days(): string[] {
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - i)
    return d.toISOString().split('T')[0]!
  }).reverse()
}

export function getLast14Days(): string[] {
  return Array.from({ length: 14 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - i)
    return d.toISOString().split('T')[0]!
  }).reverse()
}

export function getLast30Days(): string[] {
  return Array.from({ length: 30 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - i)
    return d.toISOString().split('T')[0]!
  }).reverse()
}

export function isInTimeWindow(start: string, end: string): boolean {
  const now = new Date()
  const startParts = start.split(':').map(Number)
  const endParts = end.split(':').map(Number)
  const sh = startParts[0] ?? 0
  const sm = startParts[1] ?? 0
  const eh = endParts[0] ?? 0
  const em = endParts[1] ?? 0
  const cur = now.getHours() * 60 + now.getMinutes()
  const s = sh * 60 + sm
  const e = eh * 60 + em
  return cur >= s && cur <= e
}

export function getDayOfWeek(dateStr: string): string {
  const days = ['日', '一', '二', '三', '四', '五', '六']
  return days[new Date(dateStr).getDay()] ?? '日'
}
