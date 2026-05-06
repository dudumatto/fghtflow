export default function Alert({ kind = "error", message }: { kind?: "error" | "info"; message: string }) {
  const cls =
    kind === "info"
      ? "border-primary/40 bg-primary/10 text-fg"
      : "border-danger/40 bg-danger/10 text-fg";
  return <div className={`rounded-ui border px-3 py-2 text-sm ${cls}`}>{message}</div>;
}

