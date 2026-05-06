export default function Spinner() {
  return (
    <div className="inline-flex items-center gap-2 text-sm text-muted">
      <span className="h-4 w-4 animate-spin rounded-full border-2 border-border border-t-primary" />
      <span>Loading</span>
    </div>
  );
}

