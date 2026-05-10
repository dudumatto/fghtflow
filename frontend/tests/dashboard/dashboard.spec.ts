import { expect, test } from "@playwright/test";
import { UI_BASE, collectCriticalBrowserIssues, registerUser, seedAuth } from "../support/e2e-api";

test("dashboard admin carrega cards principais sem erro critico", async ({ page }) => {
  const issues = collectCriticalBrowserIssues(page);
  const professor = await registerUser("PROFESSOR");
  await seedAuth(page, professor);
  await page.goto(`${UI_BASE}/dashboard/admin`);
  for (const label of ["Alunos ativos", "Usuarios atletas", "Usuarios professores", "Planos ativos", "Aulas proximas"]) {
    await expect(page.getByText(label)).toBeVisible();
  }
  expect(issues).toEqual([]);
});

test("dashboard atleta carrega metricas de performance", async ({ page }) => {
  const atleta = await registerUser("ATLETA");
  await seedAuth(page, atleta);
  await page.goto(`${UI_BASE}/dashboard`);
  for (const label of ["Total lutas", "Vitorias", "Derrotas", "Winrate", "Submission rate"]) {
    await expect(page.getByText(label)).toBeVisible();
  }
});
