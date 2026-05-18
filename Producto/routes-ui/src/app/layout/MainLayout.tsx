import { Outlet, useLocation } from "react-router-dom";
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/AppSidebar";

const ROUTE_TITLES: Record<string, string> = {
  "/": "Inicio",
  "/mapa": "Mapa",
  "/usuarios": "Usuarios",
  "/reportes": "Reportes",
  "/files": "Subir JSONs",
  "/login": "Iniciar sesión",
};

export function MainLayout() {
  const { pathname } = useLocation();
  const title = ROUTE_TITLES[pathname] ?? "TAG OK";

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="sticky top-0 z-30 flex h-12 shrink-0 items-center gap-3 border-b border-border bg-background/80 px-4 backdrop-blur supports-backdrop-filter:bg-background/60">
          <SidebarTrigger className="-ml-1 text-muted-foreground hover:text-foreground [&_svg]:stroke-[1.5]" />
          <span className="text-sm font-medium text-foreground">{title}</span>
        </header>
        <Outlet />
      </SidebarInset>
    </SidebarProvider>
  );
}
