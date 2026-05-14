import { test } from "../../fixtures/auth.fixture";
import { backendE2ESkipMessage, seedRoleAuth, shouldRunBackendE2E } from "../../helpers/auth.helper";

test("atletas abre para perfil administrativo", async ({ atletasPage, page }) => {
  test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);
  await seedRoleAuth(page, "PROFESSOR");
  await atletasPage.goto();
  await atletasPage.expectLoaded();
});

