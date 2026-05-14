import { test, expect } from "../../fixtures/auth.fixture";
import {
  PASSWORD,
  backendE2ESkipMessage,
  configuredE2EUser,
  hasConfiguredE2EUser,
  newApiContext,
  registerUser,
  seedAuth,
  shouldRunBackendE2E
} from "../../helpers/auth.helper";
import { routes } from "../../helpers/routes.helper";

test("rota protegida sem login redireciona para login", async ({ page }) => {
  await page.goto(routes.agenda);
  await expect(page).toHaveURL(/\/login$/);
  await expect(page.getByRole("button", { name: "Entrar" })).toBeVisible();
});

test("tela de login abre", async ({ loginPage }) => {
  await loginPage.goto();
  await loginPage.expectLoaded();
});

test("login invalido mostra erro sem crash", async ({ page, loginPage }) => {
  await page.route("**/auth/login", (route) =>
    route.fulfill({
      status: 401,
      contentType: "application/json",
      body: JSON.stringify({ success: false, data: null, error: "Invalid credentials" })
    })
  );
  await loginPage.goto();
  await loginPage.login("nao-existe@fightflow.test", "password123");
  await expect(page.getByText(/401: Invalid credentials|403|Forbidden|Bad credentials|Unauthorized/i)).toBeVisible();
});

test("login real com credenciais E2E carrega dashboard quando env existir", async ({ page, loginPage, dashboardPage }) => {
  test.skip(!hasConfiguredE2EUser(), "Defina E2E_EMAIL e E2E_PASSWORD para validar login real.");
  const user = configuredE2EUser();
  await loginPage.goto();
  await loginPage.login(user.email, user.password);
  await dashboardPage.expectAnyDashboard();
});

test.describe("auth integrado com backend", () => {
test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);

test("login valido, persistencia de sessao e logout", async ({ page }) => {
  const user = await registerUser("ATLETA");
  await page.goto(routes.login);
  await page.getByLabel("Email").fill(user.email);
  await page.getByLabel("Password").fill(PASSWORD);
  await page.getByRole("button", { name: "Entrar" }).click();
  await expect(page).toHaveURL(/\/dashboard$/);
  await expect(page.getByText("Resumo de performance")).toBeVisible();

  await page.reload();
  await expect(page.getByText("Resumo de performance")).toBeVisible();

  await page.getByRole("button", { name: "Sair" }).click();
  await expect(page).toHaveURL(/\/login$/);
});

test("refresh token retorna novo access token via cookie HttpOnly", async () => {
  const ctx = await newApiContext();
  const email = `refresh-${Date.now()}@fightflow.test`;
  const register = await ctx.post("/auth/register", {
    data: { email, password: PASSWORD, role: "ATLETA", academiaNome: `Academia Refresh ${Date.now()}` }
  });
  expect(register.ok(), await register.text()).toBeTruthy();

  const refresh = await ctx.post("/auth/refresh");
  expect(refresh.ok(), await refresh.text()).toBeTruthy();
  const body = await refresh.json();
  expect(body.data.token).toEqual(expect.any(String));
});

test("professor autenticado cai no dashboard admin, nao no dashboard de atleta", async ({ page }) => {
  const professor = await registerUser("PROFESSOR");
  await seedAuth(page, professor);
  await page.goto(routes.dashboard);
  await expect(page).toHaveURL(/\/dashboard\/admin$/);
  await expect(page.getByRole("main").getByText("Dashboard admin")).toBeVisible();
});
});
