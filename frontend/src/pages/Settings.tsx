import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { Mail, User, LogOut, Loader2, Link as LinkIcon, CheckCircle2 } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { userService } from "@/services/userService";
import { authService } from "@/services/authService";
import AppLayout from "@/components/AppLayout";
import api from "@/services/api";

const Settings = () => {
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const { data: user, isLoading } = useQuery({
    queryKey: ['user-profile'],
    queryFn: userService.getProfile,
    retry: false,
    refetchOnWindowFocus: true, 
  });

  const connectMutation = useMutation({
    mutationFn: authService.getAuthorizationUrl,
    onSuccess: (url) => {
      toast({
        title: "Redirecionando...",
        description: "Você será levado para a página de login da Microsoft.",
      });
      window.location.href = url;
    },
    onError: () => {
      toast({
        title: "Erro de Conexão",
        description: "Não foi possível iniciar a autenticação com a Microsoft.",
        variant: "destructive",
      });
    }
  });

  const disconnectMutation = useMutation({
    mutationFn: async () => {
      await api.post('/user/disconnect-outlook');
    },
    onSuccess: () => {
      toast({ title: "Conexão removida", description: "O Outlook não está mais sincronizado." });
      queryClient.invalidateQueries({ queryKey: ['user-profile'] });
    }
  });

  if (isLoading) {
      return (
        <AppLayout>
            <div className="flex justify-center mt-20"><Loader2 className="animate-spin text-primary h-8 w-8" /></div>
        </AppLayout>
      )
  }

  return (
    <AppLayout>
      <div className="space-y-8">
        <div>
          <h1 className="mb-2 font-display text-3xl font-bold tracking-tight md:text-4xl">Configurações</h1>
          <p className="text-muted-foreground">Gerencie sua conta e integrações</p>
        </div>

        {/* Perfil do Administrador */}
        <Card className="border-border/50 p-6">
          <div className="mb-6">
            <h2 className="flex items-center gap-2.5 font-display text-xl font-semibold">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <User className="h-5 w-5" />
              </div>
              Perfil do Administrador
            </h2>
          </div>
          <div className="grid gap-6 md:grid-cols-2">
              <div className="space-y-2">
                <Label>Nome</Label>
                <Input value={user?.name || ''} disabled className="bg-muted/50" />
              </div>
              <div className="space-y-2">
                <Label>E-mail de Login</Label>
                <Input value={user?.email || ''} disabled className="bg-muted/50" />
              </div>
          </div>
        </Card>

        {/* Área de Integração Outlook */}
        <Card className="border-border/50 p-6">
          <div className="mb-6">
            <h2 className="flex items-center gap-2.5 font-display text-xl font-semibold">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                <Mail className="h-5 w-5" />
              </div>
              Integração Outlook (Leitura de Tarefas)
            </h2>
            <p className="mt-1 text-sm text-muted-foreground">
              Conecte uma conta Outlook para que a IA leia e processe os e-mails desta conta.
            </p>
          </div>

          <div className="space-y-6">
            {/* Estado da Conexão */}
            <div className={`flex items-center justify-between rounded-lg border p-4 ${user?.microsoftConnected ? 'border-green-200 bg-green-50/50' : 'border-border/50 bg-muted/30'}`}>
              <div className="flex items-center gap-4">
                <div className={`flex h-12 w-12 items-center justify-center rounded-full ${user?.microsoftConnected ? 'bg-green-100 text-green-700' : 'bg-muted text-muted-foreground'}`}>
                  {user?.microsoftConnected ? <CheckCircle2 className="h-6 w-6" /> : <Mail className="h-6 w-6" />}
                </div>
                <div>
                  <p className="font-medium text-base">
                    {user?.microsoftConnected ? 'Conectado e Sincronizando' : 'Aguardando Conexão'}
                  </p>
                  <p className="text-sm text-muted-foreground">
                    {user?.microsoftConnected 
                      ? 'A IA está monitorando novos e-mails automaticamente.' 
                      : 'Conecte sua conta para ativar a automação de tarefas.'}
                  </p>
                </div>
              </div>
              {/* Indicador Visual Pulsante */}
              {user?.microsoftConnected && (
                 <div className="flex items-center gap-2">
                    <span className="relative flex h-3 w-3">
                      <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                      <span className="relative inline-flex rounded-full h-3 w-3 bg-green-500"></span>
                    </span>
                    <span className="text-xs font-medium text-green-700 hidden sm:inline">Ativo</span>
                 </div>
              )}
            </div>

            <Separator />

            {/* BOTÕES DE AÇÃO (VOLTARAM!) */}
            <div>
              {user?.microsoftConnected ? (
                <div className="flex flex-col gap-2">
                   <p className="text-sm text-muted-foreground mb-2">
                    Deseja parar a sincronização? Ao desconectar, novos e-mails não serão mais lidos.
                  </p>
                  <Button
                    variant="destructive"
                    onClick={() => disconnectMutation.mutate()}
                    disabled={disconnectMutation.isPending}
                    className="w-full sm:w-auto gap-2"
                  >
                    {disconnectMutation.isPending ? <Loader2 className="animate-spin h-4 w-4" /> : <LogOut className="h-4 w-4" />}
                    Desconectar Conta Outlook
                  </Button>
                </div>
              ) : (
                <div className="flex flex-col gap-2">
                   <p className="text-sm text-muted-foreground mb-2">
                    Você será redirecionado para a Microsoft para autorizar o acesso seguro.
                  </p>
                  <Button
                    onClick={() => connectMutation.mutate()}
                    disabled={connectMutation.isPending}
                    className="w-full sm:w-auto gap-2 bg-[#0078D4] hover:bg-[#006cbd] text-white shadow-sm" // Cor oficial Microsoft Blue
                  >
                    {connectMutation.isPending ? <Loader2 className="animate-spin h-4 w-4" /> : <LinkIcon className="h-4 w-4" />}
                    Conectar com Microsoft Outlook
                  </Button>
                </div>
              )}
            </div>
          </div>
        </Card>
      </div>
    </AppLayout>
  );
};

export default Settings;