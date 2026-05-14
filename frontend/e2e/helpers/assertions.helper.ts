import { expect, type Page } from "@playwright/test";
import { API_BASE } from "./routes.helper";

export function collectCriticalBrowserIssues(page: Page) {
  const issues: string[] = [];
  page.on("console", (msg) => {
    const text = msg.text();
    if (msg.type() === "error" && !text.includes("favicon.ico")) issues.push(`console: ${text}`);
  });
  page.on("response", (res) => {
    const url = res.url();
    const status = res.status();
    if (url.startsWith(API_BASE) && status >= 500) issues.push(`network ${status}: ${url}`);
  });
  return issues;
}

export async function expectNoVisibleServerError(page: Page) {
  await expect(page.getByText(/\b500\b|Internal server error|Internal error/i)).toHaveCount(0);
}

