import { expect, test } from "@playwright/test";
import { backendE2ESkipMessage, createAula, registerUser, seedAuth, shouldRunBackendE2E, unique } from "../../helpers/auth.helper";
import { routes } from "../../helpers/routes.helper";

test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);

test("professor cria e filtra aula pela UI", async ({ page }) => {
  const professor = await registerUser("PROFESSOR");
  await seedAuth(page, professor);
  const title = unique("Aula UI");

  await page.goto(routes.agenda);
  await page.getByLabel("Titulo").fill(title);
  await page.getByLabel("Descricao").fill("Fluxo completo E2E");
  await page.getByRole("button", { name: "Criar aula" }).click();
  await expect(page.getByRole("cell", { name: title })).toBeVisible();

  await page.getByLabel("Busca").fill(title);
  await expect(page.getByRole("cell", { name: title })).toBeVisible();
  await expect(page.getByRole("button", { name: "Prev" })).toBeDisabled();
});

test("editar e excluir aula pela API reflete na listagem", async ({ page }) => {
  const professor = await registerUser("PROFESSOR");
  const aula = await createAula(professor);
  const updated = `${aula.titulo} editada`;

  const put = await professor.ctx.put(`/aulas/${aula.id}`, { data: { titulo: updated } });
  expect(put.ok(), await put.text()).toBeTruthy();
  await seedAuth(page, professor);
  await page.goto(routes.agenda);
  await page.getByLabel("Busca").fill(updated);
  await expect(page.getByRole("cell", { name: updated })).toBeVisible();

  const del = await professor.ctx.delete(`/aulas/${aula.id}`);
  expect(del.ok(), await del.text()).toBeTruthy();
  await page.getByLabel("Ativa").selectOption("false");
  await expect(page.getByRole("cell", { name: updated })).toBeVisible();
});
