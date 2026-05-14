import { expect, test } from "@playwright/test";
import {
  athleteProfile,
  backendE2ESkipMessage,
  createAula,
  createPlano,
  registerAthlete,
  registerUser,
  seedAuth,
  shouldRunBackendE2E
} from "../../helpers/auth.helper";

test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);

test("professor registra presenca e bloqueia duplicada", async ({ page }) => {
  const professor = await registerUser("PROFESSOR");
  const atleta = await registerAthlete(professor.academiaId);
  const aluno = await athleteProfile(atleta);
  const aula = await createAula(professor);

  await seedAuth(page, professor);
  await page.goto(`/agenda/${aula.id}/presencas`);
  await page.getByLabel("Aluno ID").fill(String(aluno.atletaId));
  await page.getByRole("button", { name: "Registrar" }).click();
  await expect(page.getByRole("cell", { name: String(aluno.atletaId), exact: true })).toBeVisible();

  const duplicate = await professor.ctx.post(`/aulas/${aula.id}/presencas`, {
    data: { alunoId: aluno.atletaId, status: "PRESENTE" }
  });
  expect(duplicate.status()).toBe(409);
});

test("atleta visualiza apenas a propria presenca pela API", async () => {
  const professor = await registerUser("PROFESSOR");
  const atletaA = await registerAthlete(professor.academiaId, "atleta-a");
  const atletaB = await registerAthlete(professor.academiaId, "atleta-b");
  const alunoA = await athleteProfile(atletaA);
  const alunoB = await athleteProfile(atletaB);
  const aula = await createAula(professor);

  await professor.ctx.post(`/aulas/${aula.id}/presencas`, { data: { alunoId: alunoA.atletaId, status: "PRESENTE" } });
  await professor.ctx.post(`/aulas/${aula.id}/presencas`, { data: { alunoId: alunoB.atletaId, status: "PRESENTE" } });

  const mine = await atletaA.ctx.get(`/aulas/${aula.id}/presencas`);
  expect(mine.ok(), await mine.text()).toBeTruthy();
  const rows = (await mine.json()).data;
  expect(rows).toHaveLength(1);
  expect(rows[0].alunoId).toBe(alunoA.atletaId);
});

test("aluno bloqueado financeiramente nao pode receber presenca PRESENTE", async () => {
  const professor = await registerUser("PROFESSOR");
  const atleta = await registerAthlete(professor.academiaId, "bloqueado");
  const aluno = await athleteProfile(atleta);
  const plano = await createPlano(professor);
  const aula = await createAula(professor);

  const vencida = new Date(Date.now() - 15 * 24 * 60 * 60 * 1000).toISOString();
  await professor.ctx.post("/mensalidades", {
    data: { alunoId: aluno.atletaId, planoId: plano.id, valor: 120, vencimento: vencida, metodoPagamento: "PIX", referencia: null }
  });
  await professor.ctx.post("/financeiro/atualizar-bloqueios");

  const blocked = await professor.ctx.post(`/aulas/${aula.id}/presencas`, {
    data: { alunoId: aluno.atletaId, status: "PRESENTE" }
  });
  expect([400, 403, 409]).toContain(blocked.status());
});
