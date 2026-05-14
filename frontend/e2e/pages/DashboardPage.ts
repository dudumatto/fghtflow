import { expect, type Page } from "@playwright/test";
import { routes } from "../helpers/routes.helper";

export class DashboardPage {
  constructor(private readonly page: Page) {}

  async goto() {
    await this.page.goto(routes.dashboard);
  }

  async gotoAdmin() {
    await this.page.goto(routes.adminDashboard);
  }

  async expectAnyDashboard() {
    await expect(this.page.getByRole("main")).toBeVisible();
    await expect(this.page.getByText(/Dashboard|Dashboard admin|Resumo de performance|Sem dados ainda/i)).toBeVisible();
  }
}

