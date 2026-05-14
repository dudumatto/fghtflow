import { test, expect } from "../../fixtures/auth.fixture";
import {
  backendE2ESkipMessage,
  configuredE2EUser,
  hasConfiguredE2EUser,
  registerUser,
  seedAuth,
  shouldRunBackendE2E
} from "../../helpers/auth.helper";
import { collectCriticalBrowserIssues } from "../../helpers/assertions.helper";
import { routes } from "../../helpers/routes.helper";

test("dashboard carrega para usuario valido se env existir", async ({ loginPage, dashboardPage }) => {
  test.skip(!hasConfiguredE2EUser(), "Defina E2E_EMAIL e E2E_PASSWORD para validar dashboard real.");
  const user = configuredE2EUser();
  await loginPage.goto();
  await loginPage.login(user.email, user.password);
  await dashboardPage.expectAnyDashboard();
});

test.describe("dashboard integrado com backend", () => {
test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);

test("dashboard admin carrega cards principais sem erro critico", async ({ page }) => {
  const issues = collectCriticalBrowserIssues(page);
  const professor = await registerUser("PROFESSOR");
  await seedAuth(page, professor);
  await page.goto(routes.adminDashboard);
  for (const label of ["Alunos ativos", "Usuarios atletas", "Usuarios professores", "Planos ativos", "Aulas proximas"]) {
    await expect(page.getByText(label)).toBeVisible();
  }
  expect(issues).toEqual([]);
});

test("dashboard atleta carrega metricas de performance", async ({ page }) => {
  const atleta = await registerUser("ATLETA");
  await seedAuth(page, atleta);
  await page.goto(routes.dashboard);
  for (const label of ["Total lutas", "Vitorias", "Derrotas", "Winrate", "Submission rate"]) {
    await expect(page.getByText(label)).toBeVisible();
  }
});
});
