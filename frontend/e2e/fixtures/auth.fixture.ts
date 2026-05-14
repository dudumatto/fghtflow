import { test as base, expect } from "@playwright/test";
import { AcademiasPage } from "../pages/AcademiasPage";
import { AgendaPage } from "../pages/AgendaPage";
import { AlunosPage } from "../pages/AlunosPage";
import { AtletasPage } from "../pages/AtletasPage";
import { DashboardPage } from "../pages/DashboardPage";
import { GraduacaoPage } from "../pages/GraduacaoPage";
import { LoginPage } from "../pages/LoginPage";
import { MensalidadesPage } from "../pages/MensalidadesPage";
import { PerfilPage } from "../pages/PerfilPage";

type FightFlowFixtures = {
  loginPage: LoginPage;
  dashboardPage: DashboardPage;
  academiasPage: AcademiasPage;
  alunosPage: AlunosPage;
  atletasPage: AtletasPage;
  mensalidadesPage: MensalidadesPage;
  agendaPage: AgendaPage;
  graduacaoPage: GraduacaoPage;
  perfilPage: PerfilPage;
};

export const test = base.extend<FightFlowFixtures>({
  loginPage: async ({ page }, use) => use(new LoginPage(page)),
  dashboardPage: async ({ page }, use) => use(new DashboardPage(page)),
  academiasPage: async ({ page }, use) => use(new AcademiasPage(page)),
  alunosPage: async ({ page }, use) => use(new AlunosPage(page)),
  atletasPage: async ({ page }, use) => use(new AtletasPage(page)),
  mensalidadesPage: async ({ page }, use) => use(new MensalidadesPage(page)),
  agendaPage: async ({ page }, use) => use(new AgendaPage(page)),
  graduacaoPage: async ({ page }, use) => use(new GraduacaoPage(page)),
  perfilPage: async ({ page }, use) => use(new PerfilPage(page))
});

export { expect };

