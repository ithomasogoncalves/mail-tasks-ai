import { ReactNode } from "react";
import { Link, useLocation } from "react-router-dom";
import { Mail, LayoutDashboard, ListTodo, Settings, LogOut } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { useAuth } from "@/contexts/AuthContext";

interface AppLayoutProps {
  children: ReactNode;
}

const AppLayout = ({ children }: AppLayoutProps) => {
  const { logout } = useAuth();

  const menuItems = [
    { icon: LayoutDashboard, label: "Dashboard", path: "/dashboard" },
    { icon: ListTodo, label: "Tarefas", path: "/tasks" },
    { icon: Settings, label: "Configurações", path: "/settings" },
  ];

  return (
    <div className="flex min-h-screen bg-background">
      <aside className="w-64 border-r border-border bg-card">
        <div className="flex h-16 items-center gap-2 border-b border-border px-6">
          <Mail className="h-6 w-6 text-primary" />
          <span className="text-lg font-bold">Mail Task's AI</span>
        </div>
        
        <nav className="flex flex-col gap-2 p-4">
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            const Icon = item.icon;
            
            return (
              <Link key={item.path} to={item.path}>
                <Button
                  variant={isActive ? "secondary" : "ghost"}
                  className={`w-full justify-start gap-3 ${
                    isActive ? "bg-secondary font-medium" : ""
                  }`}
                >
                  <Icon className="h-5 w-5" />
                  {item.label}
                </Button>
              </Link>
            );
          })}
        </nav>

        <Separator className="mx-4" />

        <div className="p-4">
      <Button 
        variant="ghost" 
        className="..." 
        onClick={logout}
      >
        <LogOut className="h-5 w-5" />
        Sair
      </Button>
    </div>
      </aside>

      <main className="flex-1 overflow-y-auto">
        <div className="container mx-auto p-8">
          {children}
        </div>
      </main>
    </div>
  );
};

export default AppLayout;
