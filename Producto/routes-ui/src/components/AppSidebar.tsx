import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  Home as HomeIcon,
  Map as MapIcon,
  Users as UsersIcon,
  Upload,
  LogOut,
  LogIn,
  ChevronsUpDown,
  ShieldCheck,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useAuth } from "@/app/context/AuthContext";
import { supabase } from "@/app/lib/supabase";
import { iniciales } from "@/features/admin/lib/format";

interface NavItem {
  to: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
}

const NAV_GENERAL: NavItem[] = [
  { to: "/", label: "Inicio", icon: HomeIcon },
  { to: "/mapa", label: "Mapa", icon: MapIcon },
];

const NAV_ADMIN: NavItem[] = [
  { to: "/usuarios", label: "Usuarios", icon: UsersIcon },
  { to: "/files", label: "Subir JSONs", icon: Upload },
];

function isItemActive(currentPath: string, to: string): boolean {
  if (to === "/") return currentPath === "/";
  return currentPath === to || currentPath.startsWith(`${to}/`);
}

export function AppSidebar() {
  const { pathname } = useLocation();

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader>
        <Link
          to="/"
          className="flex items-center px-2 py-2 group-data-[collapsible=icon]:justify-center"
        >
          <div className="flex flex-col leading-tight group-data-[collapsible=icon]:hidden">
            <span className="text-sm font-bold tracking-tight text-brand">
              TAG OK
            </span>
            <span className="text-[11px] text-muted-foreground">
              Panel administrador
            </span>
          </div>
          <span className="hidden text-[11px] font-bold tracking-tight text-brand group-data-[collapsible=icon]:inline">
            TG
          </span>
        </Link>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>General</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {NAV_GENERAL.map((item) => (
                <NavItemButton
                  key={item.to}
                  item={item}
                  active={isItemActive(pathname, item.to)}
                />
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
          <SidebarGroupLabel>Administración</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {NAV_ADMIN.map((item) => (
                <NavItemButton
                  key={item.to}
                  item={item}
                  active={isItemActive(pathname, item.to)}
                />
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        <UserMenu />
      </SidebarFooter>
    </Sidebar>
  );
}

function NavItemButton({ item, active }: { item: NavItem; active: boolean }) {
  return (
    <SidebarMenuItem>
      <SidebarMenuButton
        asChild
        isActive={active}
        tooltip={item.label}
        className="data-[active=true]:bg-brand-soft data-[active=true]:text-brand data-[active=true]:hover:bg-brand-soft data-[active=true]:hover:text-brand dark:data-[active=true]:bg-brand-soft/30"
      >
        <Link to={item.to}>
          <item.icon />
          <span>{item.label}</span>
        </Link>
      </SidebarMenuButton>
    </SidebarMenuItem>
  );
}

function UserMenu() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isAdmin = user?.app_metadata?.role === "admin";

  const handleLogout = async () => {
    await supabase.auth.signOut();
    navigate("/login");
  };

  if (!user) {
    return (
      <Button asChild variant="outline" size="sm" className="w-full justify-start">
        <Link to="/login">
          <LogIn className="h-4 w-4" />
          <span className="group-data-[collapsible=icon]:hidden">
            Iniciar sesión
          </span>
        </Link>
      </Button>
    );
  }

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <Avatar className="h-8 w-8 rounded-md border border-border">
                <AvatarFallback className="bg-muted text-xs font-semibold rounded-md">
                  {iniciales(user.email)}
                </AvatarFallback>
              </Avatar>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-medium">{user.email}</span>
                <span className="truncate text-xs text-muted-foreground">
                  {isAdmin ? "Administrador" : "Usuario"}
                </span>
              </div>
              <ChevronsUpDown className="ml-auto size-4 text-muted-foreground" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            side="right"
            align="end"
            className="w-56"
          >
            <DropdownMenuLabel className="flex flex-col gap-1">
              <span className="text-sm font-medium truncate">{user.email}</span>
              {isAdmin && (
                <Badge variant="outline" className="w-fit gap-1">
                  <ShieldCheck className="h-3 w-3" /> Admin
                </Badge>
              )}
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={handleLogout}>
              <LogOut className="h-4 w-4" />
              Cerrar sesión
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  );
}
