import { expect, test } from "@playwright/test";
import { UI_BASE, collectCriticalBrowserIssues, registerUser, seedAuth } from "../support/e2e-api";

test("frontend nao registra console errors criticos nem network 5xx no fluxo principal", async ({ page }) => {
  const issues = collectCriticalBrowserIssues(page);
  const professor = await registerUser("PROFESSOR");
  await seedAuth(page, professor);
  for (const route of ["/dashboard/admin", "/alunos", "/planos", "/mensalidades", "/agenda", "/competicoes", "/treinos"]) {
    await page.goto(`${UI_BASE}${route}`);
    await expect(page.locator("main")).toBeVisible();
  }
  expect(issues).toEqual([]);
});

test("responsividade basica mobile, tablet e desktop", async ({ page }) => {
  const professor = await registerUser("PROFESSOR");
  await seedAuth(page, professor);
  for (const viewport of [
    { width: 390, height: 844 },
    { width: 768, height: 1024 },
    { width: 1440, height: 900 }
  ]) {
    await page.setViewportSize(viewport);
    await page.goto(`${UI_BASE}/agenda`);
    await expect(page.getByRole("main").getByText("Agenda")).toBeVisible();
    const overflow = await page.evaluate(() => document.documentElement.scrollWidth > document.documentElement.clientWidth);
    expect(overflow).toBe(false);
  }
});
