import { test } from "../../fixtures/auth.fixture";
import { backendE2ESkipMessage, seedRoleAuth, shouldRunBackendE2E } from "../../helpers/auth.helper";

test("academias abre para perfil administrativo", async ({ academiasPage, page }) => {
  test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);
  await seedRoleAuth(page, "PROFESSOR");
  await academiasPage.goto();
  await academiasPage.expectLoaded();
});

