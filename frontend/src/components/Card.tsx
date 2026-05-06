import React from "react";

export default function Card({ children, className = "" }: { children: React.ReactNode; className?: string }) {
  return <div className={`rounded-ui border border-border bg-card p-4 ${className}`}>{children}</div>;
}

