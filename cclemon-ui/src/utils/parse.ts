export function objectToQueryParams(obj: Record<string, string>): string {
  return Object.entries(obj)
    .filter(([, value]) => value)
    .map(
      ([key, value]) =>
        `${encodeURIComponent(key)}=${encodeURIComponent(value)}`,
    )
    .join('&');
}

export function objectToFormData(obj: Record<string, string>): FormData {
  const formData = new FormData();
  Object.entries(obj).forEach(([key, value]) => {
    if (value) {
      formData.append(key, value);
    }
  });
  return formData;
}
