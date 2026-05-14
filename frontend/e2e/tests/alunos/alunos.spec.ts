import { test } from "../../fixtures/auth.fixture";
import { backendE2ESkipMessage, seedRoleAuth, shouldRunBackendE2E } from "../../helpers/auth.helper";

test("alunos abre para perfil administrativo", async ({ alunosPage, page }) => {
  test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);
  await seedRoleAuth(page, "PROFESSOR");
  await alunosPage.goto();
  await alunosPage.expectLoaded();
});

