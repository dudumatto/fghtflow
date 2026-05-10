import { expect, test } from "@playwright/test";
import { PASSWORD, UI_BASE, newApiContext, registerUser, seedAuth } from "../support/e2e-api";

test("rota protegida sem login redireciona para login", async ({ page }) => {
  await page.goto(`${UI_BASE}/agenda`);
  await expect(page).toHaveURL(/\/login$/);
  await expect(page.getByRole("button", { name: "Entrar" })).toBeVisible();
});

test("login invalido mostra erro sem crash", async ({ page }) => {
  await page.goto(`${UI_BASE}/login`);
  await page.getByLabel("Email").fill("nao-existe@fightflow.test");
  await page.getByLabel("Password").fill("password123");
  await page.getByRole("button", { name: "Entrar" }).click();
  await expect(page.getByText(/401: Invalid credentials|403|Forbidden|Bad credentials|Unauthorized/i)).toBeVisible();
});

test("login valido, persistencia de sessao e logout", async ({ page }) => {
  const user = await registerUser("ATLETA");
  await page.goto(`${UI_BASE}/login`);
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
  await page.goto(`${UI_BASE}/dashboard`);
  await expect(page).toHaveURL(/\/dashboard\/admin$/);
  await expect(page.getByRole("main").getByText("Dashboard admin")).toBeVisible();
});
