/**
 * 兼容后端图片字段可能为 URL、JSON 字符串或 JSON 数组字符串
 */
export function resolveImage(value, fallback = '') {
  if (!value) return fallback

  if (Array.isArray(value)) {
    return value[0] || fallback
  }

  if (typeof value !== 'string') {
    return fallback
  }

  const raw = value.trim()
  if (!raw) return fallback

  try {
    if (raw.startsWith('[')) {
      const parsed = JSON.parse(raw)
      if (Array.isArray(parsed) && parsed.length > 0) {
        return parsed[0] || fallback
      }
      return fallback
    }

    if (raw.startsWith('"') && raw.endsWith('"')) {
      const parsed = JSON.parse(raw)
      return typeof parsed === 'string' ? parsed : fallback
    }
  } catch {
    // ignore parse errors and fall back to raw value
  }

  return raw
}
