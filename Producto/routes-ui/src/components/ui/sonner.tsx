import { useEffect, useState } from "react";
import { Toaster as Sonner, type ToasterProps } from "sonner";

/** Sincroniza el tema del toast con la clase `.dark` del documento. */
function useDocumentTheme(): "light" | "dark" {
  const [theme, setTheme] = useState<"light" | "dark">(() =>
    typeof document !== "undefined" &&
    document.documentElement.classList.contains("dark")
      ? "dark"
      : "light",
  );

  useEffect(() => {
    const root = document.documentElement;
    const observer = new MutationObserver(() => {
      setTheme(root.classList.contains("dark") ? "dark" : "light");
    });
    observer.observe(root, { attributes: true, attributeFilter: ["class"] });
    return () => observer.disconnect();
  }, []);

  return theme;
}

export function Toaster(props: ToasterProps) {
  const theme = useDocumentTheme();

  return (
    <Sonner
      theme={theme}
      position="top-right"
      richColors
      closeButton
      toastOptions={{
        classNames: {
          toast:
            "group rounded-lg border border-border bg-popover text-popover-foreground shadow-lg",
          description: "text-muted-foreground",
        },
      }}
      {...props}
    />
  );
}
