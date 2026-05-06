import React from "react";

type Props = React.InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  error?: string | null;
};

export default function Input({ label, error, className = "", id, ...props }: Props) {
  const inputId = id ?? props.name ?? label.toLowerCase().replaceAll(" ", "-");
  return (
    <label className="block">
      <span className="block text-sm text-muted">{label}</span>
      <input
        id={inputId}
        className={`mt-1 h-10 w-full rounded-ui border border-border bg-bg px-3 text-sm text-fg placeholder:text-muted focus:border-primary ${className}`}
        {...props}
      />
      {error ? <span className="mt-1 block text-sm text-danger">{error}</span> : null}
    </label>
  );
}

