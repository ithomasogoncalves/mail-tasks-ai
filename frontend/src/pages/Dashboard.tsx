import { useQuery } from "@tanstack/react-query";
import { taskService } from "@/services/taskService";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Mail, CheckCircle2, Clock, AlertCircle, TrendingUp, Users, Loader2 } from "lucide-react";
import AppLayout from "@/components/AppLayout";
import { Task } from "@/types";

const Dashboard = () => {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['dashboard-data'],
    queryFn: taskService.getTasks,
    refetchInterval: 30000,
  });

  if (isLoading) {
    return (
      <AppLayout>
        <div className="flex h-[80vh] items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      </AppLayout>
    );
  }

  if (isError || !data) {
    return (
      <AppLayout>
        <div className="p-8 text-center text-destructive">
          Erro ao carregar dados do dashboard. Verifique a conexão.
        </div>
      </AppLayout>
    );
  }

  const { tasks, stats } = data;

  const totalTasks = (stats?.pending_count || 0) + (stats?.completed_count || 0);
  const completionRate = totalTasks > 0 ? Math.round(((stats?.completed_count || 0) / totalTasks) * 100) : 0;

  const categoryCounts = tasks.reduce((acc, task) => {
    const cat = task.categoriaSugerida || "Outros";
    acc[cat] = (acc[cat] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);

  const categories = Object.entries(categoryCounts).map(([name, count], index) => ({
    name,
    count,
    progress: Math.min((count / tasks.length) * 100 + 20, 100), 
    color: index % 2 === 0 ? "bg-primary" : "bg-accent"
  }));

  const statsCards = [
    {
      title: "Total de Tarefas",
      value: totalTasks.toString(),
      change: "Atualizado",
      trend: "up",
      icon: <Mail className="h-5 w-5" />,
      color: "text-primary",
    },
    {
      title: "Concluídas",
      value: (stats?.completed_count || 0).toString(),
      change: `${completionRate}%`,
      trend: "up",
      icon: <CheckCircle2 className="h-5 w-5" />,
      color: "text-success",
    },
    {
      title: "Em Progresso",
      value: (stats?.pending_count || 0).toString(),
      change: "Ativas",
      trend: "up",
      icon: <Clock className="h-5 w-5" />,
      color: "text-warning",
    },
    {
      title: "Urgentes",
      value: (stats?.urgent_count || 0).toString(),
      change: "Atenção",
      trend: "down",
      icon: <AlertCircle className="h-5 w-5" />,
      color: "text-destructive",
    },
  ];

  const getUrgencyVariant = (urgency: string) => {
    switch (urgency) {
      case "URGENTE": return "destructive";
      case "MEDIANO": return "default";
      default: return "secondary";
    }
  };

  return (
    <AppLayout>
      <div className="space-y-8">
        {/* Header */}
        <div>
          <h1 className="mb-2 font-display text-3xl font-bold tracking-tight md:text-4xl">
            Dashboard
          </h1>
          <p className="text-muted-foreground">
            Visão geral das suas tarefas e métricas de desempenho em tempo real
          </p>
        </div>

        {/* Stats Grid */}
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {statsCards.map((stat, index) => (
            <Card key={index} className="border-border/50 transition-all hover:border-primary/50 hover:shadow-md">
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">
                  {stat.title}
                </CardTitle>
                <div className={`${stat.color}`}>{stat.icon}</div>
              </CardHeader>
              <CardContent>
                <div className="mb-1 font-display text-3xl font-bold">{stat.value}</div>
                <div className="flex items-center gap-1 text-sm">
                  <TrendingUp className={`h-4 w-4 ${stat.color.includes('destructive') ? 'text-destructive' : 'text-success'}`} />
                  <span className={stat.color.includes('destructive') ? 'text-destructive' : 'text-success'}>
                    {stat.change}
                  </span>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Main Content Grid */}
        <div className="grid gap-6 lg:grid-cols-3">
          {/* Categories */}
          <Card className="lg:col-span-2">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Users className="h-5 w-5 text-primary" />
                Tarefas por Categoria
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              {categories.length === 0 ? (
                <p className="text-muted-foreground text-sm">Nenhuma categoria registrada.</p>
              ) : (
                categories.map((category, index) => (
                  <div key={index} className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="font-medium">{category.name}</span>
                      <span className="text-muted-foreground">{category.count} tarefas</span>
                    </div>
                    <Progress value={category.progress} className={`h-2 ${category.color}`} />
                  </div>
                ))
              )}
            </CardContent>
          </Card>

          {/* Quick Stats */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <TrendingUp className="h-5 w-5 text-primary" />
                Métricas Rápidas
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between rounded-lg border border-border/50 p-4">
                <div>
                  <p className="text-sm text-muted-foreground">Taxa de Conclusão</p>
                  <p className="font-display text-2xl font-bold">{completionRate}%</p>
                </div>
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-success/10">
                  <CheckCircle2 className="h-6 w-6 text-success" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Recent Tasks */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-5 w-5 text-primary" />
              Tarefas Recentes
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {tasks.slice(0, 5).map((task) => (
                <div
                  key={task.id}
                  className="flex items-start justify-between gap-4 rounded-lg border border-border/50 p-4 transition-colors hover:border-primary/50 hover:bg-muted/30"
                >
                  <div className="flex-1 space-y-1">
                    <p className="font-medium line-clamp-1">{task.resumoTarefa}</p>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                      <Badge variant="outline" className="text-xs">
                        {task.categoriaSugerida}
                      </Badge>
                      <span>•</span>
                      <span>{new Date(task.receivedAt).toLocaleDateString('pt-BR')}</span>
                    </div>
                  </div>
                  <Badge variant={getUrgencyVariant(task.urgencia)}>
                    {task.urgencia}
                  </Badge>
                </div>
              ))}
              {tasks.length === 0 && (
                 <p className="text-center text-muted-foreground py-4">Nenhuma tarefa recente.</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </AppLayout>
  );
};

export default Dashboard;