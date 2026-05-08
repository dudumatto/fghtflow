export type ApiError = {
  status: number;
  message: string;
};

type Wrapped<T> = { success: boolean; data: T; error: any };

function baseUrl() {
  return (import.meta as any).env?.VITE_API_BASE ?? "http://localhost:8080";
}

function getToken(): string | null {
  return sessionStorage.getItem("ff_token");
}

function setToken(token: string | null) {
  if (token) {
    sessionStorage.setItem("ff_token", token);
  } else {
    sessionStorage.removeItem("ff_token");
  }
}

async function refreshToken(): Promise<string | null> {
  const res = await fetch(`${baseUrl()}/auth/refresh`, { method: "POST", credentials: "include" });
  if (!res.ok) return null;
  const body = (await res.json()) as Wrapped<any>;
  if (!body?.success) return null;
  const token = body.data?.token as string | undefined;
  if (!token) return null;
  setToken(token);
  return token;
}

async function request<T>(path: string, init: RequestInit, allowRefresh = true): Promise<T> {
  const url = `${baseUrl()}${path}`;
  const headers = new Headers(init.headers ?? {});
  headers.set("Accept", "application/json");
  const token = getToken();
  if (token) headers.set("Authorization", `Bearer ${token}`);

  const res = await fetch(url, { ...init, headers, credentials: "include" });
  if (res.ok) {
    if (res.status === 204) return undefined as T;
    const ct = res.headers.get("content-type") ?? "";
    if (ct.includes("application/json")) {
      const body = (await res.json()) as Wrapped<T>;
      if (body && typeof body.success === "boolean") {
        if (body.success) return body.data as T;
        const errMsg =
          typeof (body as any).error === "string"
            ? ((body as any).error as string)
            : ((body as any).error?.message as string | undefined);
        const err: ApiError = { status: res.status, message: errMsg ?? "Request failed" };
        throw err;
      }
      return body as any as T;
    }
    return (await res.text()) as any as T;
  }

  if (res.status === 403 && allowRefresh && getToken()) {
    const newToken = await refreshToken();
    if (newToken) {
      return request<T>(path, init, false);
    }
  }

  let message = res.statusText || "Request failed";
  try {
    const ct = res.headers.get("content-type") ?? "";
    if (ct.includes("application/json")) {
      const body = (await res.json()) as any;
      message = (typeof body?.error === "string" ? body.error : body?.error?.message) ?? body?.message ?? message;
    }
  } catch {}

  const err: ApiError = { status: res.status, message };
  throw err;
}

export const api = {
  get: <T>(path: string) => request<T>(path, { method: "GET" }),
  post: <T>(path: string, body?: unknown) =>
    request<T>(path, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: body === undefined ? undefined : JSON.stringify(body)
    }),
  put: <T>(path: string, body?: unknown) =>
    request<T>(path, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: body === undefined ? undefined : JSON.stringify(body)
    }),
  upload: async <T>(path: string, file: File) => {
    const url = `${baseUrl()}${path}`;
    const token = getToken();
    const fd = new FormData();
    fd.append("file", file);
    const res = await fetch(url, {
      method: "POST",
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      body: fd,
      credentials: "include"
    });
    if (!res.ok) {
      let message = res.statusText || "Upload failed";
      try {
        const body = await res.json();
        message = (typeof body?.error === "string" ? body.error : body?.error?.message) ?? body?.message ?? message;
      } catch {}
      throw { status: res.status, message } satisfies ApiError;
    }
    const body = (await res.json()) as Wrapped<T>;
    if (body && typeof body.success === "boolean") return body.data as T;
    return body as any as T;
  }
};
