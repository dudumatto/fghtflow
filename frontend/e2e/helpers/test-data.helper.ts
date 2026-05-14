export const PASSWORD = process.env.E2E_DEFAULT_PASSWORD ?? "password123";

export function unique(prefix: string) {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
}

