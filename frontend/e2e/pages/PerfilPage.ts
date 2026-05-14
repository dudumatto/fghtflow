import { expect, type Page } from "@playwright/test";
import { routes } from "../helpers/routes.helper";

export class PerfilPage {
  constructor(private readonly page: Page) {}

  async goto() {
    await this.page.goto(routes.perfil);
  }

  async expectLoaded() {
    await expect(this.page.getByRole("main")).toBeVisible();
  }
}

