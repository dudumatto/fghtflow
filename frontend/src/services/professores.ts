import { api } from "./api";
import type { SelectOptionResponse } from "./types";

export const professoresService = {
  select: () => api.get<SelectOptionResponse[]>("/professores/select")
};
