import { expect, test } from "@playwright/test";
import { athleteProfile, createAula, registerAthlete, registerUser } from "../support/e2e-api";

test("ADMIN acessa dashboard administrativo", async () => {
  const admin = await registerUser("ADMIN");
  const res = await admin.ctx.get("/dashboard/admin");
  expect(res.ok(), await res.text()).toBeTruthy();
});

test("PROFESSOR nao altera aula de outro professor", async () => {
  const professorA = await registerUser("PROFESSOR");
  const professorB = await registerUser("PROFESSOR", { academiaId: professorA.academiaId });
  const aulaB = await createAula(professorB);

  const forbidden = await professorA.ctx.put(`/aulas/${aulaB.id}`, { data: { titulo: "Tentativa IDOR" } });
  expect(forbidden.status()).toBe(403);
});

test("ATLETA nao acessa dados de outro atleta por parametro", async () => {
  const professor = await registerUser("PROFESSOR");
  const atletaA = await registerAthlete(professor.academiaId, "idor-a");
  const atletaB = await registerAthlete(professor.academiaId, "idor-b");
  const other = await athleteProfile(atletaB);

  const forbidden = await atletaA.ctx.get(`/lutas?atletaId=${other.atletaId}`);
  expect(forbidden.status()).toBe(403);
});

test("ATLETA nao acessa dashboard admin", async () => {
  const atleta = await registerUser("ATLETA");
  const forbidden = await atleta.ctx.get("/dashboard/admin");
  expect(forbidden.status()).toBe(403);
});
