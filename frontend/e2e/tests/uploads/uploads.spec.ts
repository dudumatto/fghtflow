import { expect, test } from "@playwright/test";
import { backendE2ESkipMessage, registerUser, shouldRunBackendE2E } from "../../helpers/auth.helper";

test.skip(!shouldRunBackendE2E(), backendE2ESkipMessage);

test("upload PDF valido e preview PDF", async () => {
  const atleta = await registerUser("ATLETA");
  const pdf = Buffer.from("%PDF-1.4\n1 0 obj\n<<>>\nendobj\ntrailer\n<<>>\n%%EOF\n");
  const upload = await atleta.ctx.post("/documentos/upload", {
    multipart: { file: { name: "contrato.pdf", mimeType: "application/pdf", buffer: pdf } }
  });
  expect(upload.ok(), await upload.text()).toBeTruthy();
  const doc = (await upload.json()).data;

  const preview = await atleta.ctx.get(`/documentos/${doc.id}/preview`);
  expect(preview.ok(), await preview.text()).toBeTruthy();
  expect(preview.headers()["content-type"]).toContain("application/pdf");
});

test("bloqueia arquivo invalido e arquivo maior que 10MB", async () => {
  const atleta = await registerUser("ATLETA");
  const invalid = await atleta.ctx.post("/documentos/upload", {
    multipart: { file: { name: "malware.exe", mimeType: "application/octet-stream", buffer: Buffer.from("x") } }
  });
  expect(invalid.status()).toBe(415);

  const large = await atleta.ctx.post("/documentos/upload", {
    multipart: { file: { name: "grande.pdf", mimeType: "application/pdf", buffer: Buffer.alloc(10 * 1024 * 1024 + 1, 1) } }
  });
  expect(large.status()).toBe(413);
});
