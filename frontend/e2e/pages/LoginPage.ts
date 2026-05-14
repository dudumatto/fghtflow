import { expect, type Page } from "@playwright/test";
import { routes } from "../helpers/routes.helper";

export class LoginPage {
  constructor(private readonly page: Page) {}

  async goto() {
    await this.page.goto(routes.login);
  }

  async expectLoaded() {
    await expect(this.page.getByRole("button", { name: "Entrar" })).toBeVisible();
  }

  async login(email: string, password: string) {
    await this.page.getByLabel("Email").fill(email);
    await this.page.getByLabel("Password").fill(password);
    await this.page.getByRole("button", { name: "Entrar" }).click();
  }
}

