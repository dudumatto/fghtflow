export default function Skeleton({ className = "" }: { className?: string }) {
  return <div className={`animate-pulse rounded-ui bg-border/40 ${className}`} />;
}

