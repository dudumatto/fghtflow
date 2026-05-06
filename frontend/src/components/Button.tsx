import React from "react";

type Props = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "ghost" | "danger";
  size?: "sm" | "md";
};

export default function Button({ variant = "primary", size = "md", className = "", ...props }: Props) {
  const base =
    "inline-flex items-center justify-center gap-2 rounded-ui border border-border px-3 font-medium transition-colors disabled:opacity-50 disabled:cursor-not-allowed";
  const sizes = size === "sm" ? "h-9 text-sm" : "h-10 text-sm";
  const variants =
    variant === "ghost"
      ? "bg-transparent hover:bg-card"
      : variant === "danger"
        ? "bg-danger text-white border-transparent hover:brightness-110"
        : "bg-primary text-white border-transparent hover:brightness-110";

  return <button className={`${base} ${sizes} ${variants} ${className}`} {...props} />;
}

