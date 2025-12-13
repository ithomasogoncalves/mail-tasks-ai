import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { taskService } from "@/services/taskService";
import api from "@/services/api";
import { Task, Urgency } from "@/types";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger, DialogFooter } from "@/components/ui/dialog";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Plus, Send, AlertCircle, Clock, CheckCircle2, Loader2, Mail, Eye } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import AppLayout from "@/components/AppLayout";
import ReactMarkdown from 'react-markdown';

const formatDate = (dateString: string) => {
  if (!dateString) return "";
  const utcDate = dateString.endsWith('Z') ? dateString : `${dateString}Z`;
  return new Date(utcDate).toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const Tasks = () => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [replyMessage, setReplyMessage] = useState("");
  
  const queryClient = useQueryClient();
  const { toast } = useToast();

  const [taskForm, setTaskForm] = useState({
    title: "",
    category: "",
    urgency: "",
    recipient: "",
  });

  const { data, isLoading, isError } = useQuery({
    queryKey: ['tasks'],
    queryFn: taskService.getTasks,
    refetchInterval: 30000,
  });

  const taskList = data?.tasks || [];

  const createTaskMutation = useMutation({
    mutationFn: taskService.createTask,
    onSuccess: () => {
      toast({
        title: "Sucesso!",
        description: `Tarefa enviada para ${taskForm.recipient}`,
      });
      setIsDialogOpen(false);
      setTaskForm({ title: "", category: "", urgency: "", recipient: "" });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
    },
    onError: (error) => {
      console.error("Erro na criação:", error);
      toast({
        title: "Erro",
        description: "Falha ao enviar a tarefa.",
        variant: "destructive",
      });
    }
  });

  const sendReplyMutation = useMutation({
    mutationFn: ({ id, message }: { id: string, message: string }) => taskService.sendReply(id, message),
    onSuccess: () => {
      toast({ 
        title: "Resposta Enviada", 
        description: "O e-mail de resposta foi enviado ao solicitante.",
        className: "bg-blue-500 text-white border-none" 
      });
      setReplyMessage("");
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
    },
    onError: (error) => {
      console.error("Erro ao enviar resposta:", error);
      toast({
        title: "Erro",
        description: "Falha ao enviar a resposta.",
        variant: "destructive",
      });
    }
  });

  const completeTaskMutation = useMutation({
    mutationFn: taskService.completeTask,
    onSuccess: () => {
      toast({ 
        title: "Tarefa concluída", 
        description: "A tarefa foi marcada como concluída e o solicitante foi notificado.",
        className: "bg-green-500 text-white border-none" 
      });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      if (selectedTask) setSelectedTask(null);
    },
    onError: (error) => {
      console.error("Erro ao concluir tarefa:", error);
      toast({
        title: "Erro",
        description: "Falha ao concluir a tarefa.",
        variant: "destructive",
      });
    }
  });

  const markAsViewedMutation = useMutation({
    mutationFn: taskService.markAsViewed,
    onSuccess: () => {
      toast({ 
        title: "Tarefa marcada como vista", 
        className: "bg-yellow-500 text-white border-none" 
      });
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
    },
    onError: (error) => {
      console.error("Erro ao marcar como visto:", error);
      toast({
        title: "Erro",
        description: "Falha ao marcar a tarefa como vista.",
        variant: "destructive",
      });
    }
  });

  const getUrgencyVariant = (urgency: string) => {
    switch (urgency) {
      case "URGENTE": return "destructive";
      case "MEDIANO": return "secondary";
      default: return "outline";
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    const payload = {
      title: taskForm.title,
      recipient: taskForm.recipient,
      category: taskForm.category, 
      urgencia: taskForm.urgency as Urgency
    };

    createTaskMutation.mutate(payload);
  };

  const handleSendReply = () => {
    if (selectedTask && replyMessage.trim()) {
      sendReplyMutation.mutate({ id: selectedTask.id, message: replyMessage });
    }
  };

  const handleCompleteTask = () => {
    if (selectedTask) {
      completeTaskMutation.mutate(selectedTask.id);
    }
  };

  const handleMarkAsViewed = () => {
    if (selectedTask) {
      markAsViewedMutation.mutate(selectedTask.id);
    }
  };

  const urgentTasks = taskList.filter(task => task.urgencia === "URGENTE" && task.status !== 'COMPLETED');
  const medianTasks = taskList.filter(task => task.urgencia === "MEDIANO" && task.status !== 'COMPLETED');
  const routineTasks = taskList.filter(task => task.urgencia === "ROTINEIRA" && task.status !== 'COMPLETED');

  const TaskSection = ({ title, icon, tasks }: { title: string; icon: React.ReactNode; tasks: Task[] }) => {
    if (tasks.length === 0) return null;

    return (
      <Card className="overflow-hidden border-border/50 mb-6">
        <CardHeader className="border-b border-border/50 bg-muted/20 pb-4">
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center gap-2.5 font-display text-xl">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10 text-primary">
                {icon}
              </div>
              {title}
            </CardTitle>
            <Badge variant="outline" className="border-primary/30 bg-primary/5 text-sm font-semibold">
              {tasks.length}
            </Badge>
          </div>
        </CardHeader>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow className="border-border/50 bg-muted/10 hover:bg-muted/10">
                  <TableHead className="font-semibold">Tarefa</TableHead>
                  <TableHead className="font-semibold">Categoria</TableHead>
                  <TableHead className="font-semibold">De</TableHead>
                  <TableHead className="font-semibold">Recebido</TableHead>
                  <TableHead className="font-semibold text-right">Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
              {tasks.map((task) => (
                <TableRow 
                  key={task.id} 
                  className={`border-border/40 transition-colors hover:bg-muted/30 cursor-pointer ${task.status === 'VIEWED' ? 'bg-yellow-50/50' : ''}`}
                  onClick={() => setSelectedTask(task)} 
                >
                  <TableCell className="font-medium max-w-[300px] truncate" title={task.resumoTarefa}>
                    {task.resumoTarefa}
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline" className="border-muted-foreground/30">
                      {task.categoriaSugerida}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-sm text-muted-foreground max-w-[150px] truncate">
                    {task.fromEmail}
                  </TableCell>
                  <TableCell className="text-sm text-muted-foreground">
                    {formatDate(task.receivedAt)}
                  </TableCell>
                  <TableCell className="text-right">
                    <Badge variant={task.status === 'VIEWED' ? 'secondary' : 'outline'}>
                      {task.status === 'VIEWED' ? 'VISTO' : 'PENDENTE'}
                    </Badge>
                  </TableCell>
                </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    );
  };

  return (
    <AppLayout>
      <div className="space-y-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="mb-2 font-display text-3xl font-bold tracking-tight md:text-4xl">Tarefas</h1>
            <p className="text-muted-foreground">Gerencie e organize todas as suas tarefas</p>
          </div>

          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button className="gap-2">
                <Plus className="h-4 w-4" />
                Nova Tarefa
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[500px]">
              <DialogHeader>
                <DialogTitle>Criar Nova Tarefa</DialogTitle>
                <DialogDescription>
                  Preencha os detalhes e envie para um colaborador.
                </DialogDescription>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="title">Descrição</Label>
                  <Textarea
                    id="title"
                    value={taskForm.title}
                    onChange={(e) => setTaskForm({ ...taskForm, title: e.target.value })}
                    required
                    rows={3}
                  />
                </div>
                
                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <Label>Categoria</Label>
                    <Select
                      value={taskForm.category}
                      onValueChange={(value) => setTaskForm({ ...taskForm, category: value })}
                      required
                    >
                      <SelectTrigger><SelectValue placeholder="Selecione" /></SelectTrigger>
                      <SelectContent>
                        <SelectItem value="FINANCEIRO">Financeiro</SelectItem>
                        <SelectItem value="RH">RH</SelectItem>
                        <SelectItem value="DESENVOLVIMENTO">Desenvolvimento</SelectItem>
                        <SelectItem value="MARKETING">Marketing</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  
                  <div className="space-y-2">
                    <Label>Urgência</Label>
                    <Select
                      value={taskForm.urgency}
                      onValueChange={(value) => setTaskForm({ ...taskForm, urgency: value })}
                      required
                    >
                      <SelectTrigger><SelectValue placeholder="Selecione" /></SelectTrigger>
                      <SelectContent>
                        <SelectItem value="URGENTE">Urgente</SelectItem>
                        <SelectItem value="MEDIANO">Mediano</SelectItem>
                        <SelectItem value="ROTINEIRA">Rotineira</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label>Destinatário</Label>
                  <Input
                    type="email"
                    placeholder="email@empresa.com"
                    value={taskForm.recipient}
                    onChange={(e) => setTaskForm({ ...taskForm, recipient: e.target.value })}
                    required
                  />
                </div>

                <DialogFooter>
                  <Button type="submit" disabled={createTaskMutation.isPending} className="gap-2">
                    {createTaskMutation.isPending ? <Loader2 className="animate-spin h-4 w-4" /> : <Send className="h-4 w-4" />}
                    Enviar
                  </Button>
                </DialogFooter>
              </form>
            </DialogContent>
          </Dialog>
        </div>

        {/* Modal de Detalhes */}
        <Dialog open={!!selectedTask} onOpenChange={(open) => !open && setSelectedTask(null)}>
          <DialogContent className="sm:max-w-[600px] max-h-[85vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle className="flex items-center gap-2">
                Detalhes da Tarefa
                {selectedTask && (
                  <Badge variant={getUrgencyVariant(selectedTask.urgencia)}>{selectedTask.urgencia}</Badge>
                )}
              </DialogTitle>
              <DialogDescription>
                Recebido de: <b>{selectedTask?.fromEmail}</b> em {selectedTask && formatDate(selectedTask.receivedAt)}
              </DialogDescription>
            </DialogHeader>

            {selectedTask && (
              <div className="space-y-6 py-2">
                <div className="space-y-2">
                  <Label className="text-muted-foreground text-xs uppercase font-bold">Resumo da IA</Label>
                  <div className="p-3 bg-muted/50 border rounded-md text-sm font-medium">
                    {/* CORREÇÃO: Exibir o resumo formatado se existir, senão o resumo original */}
                    <ReactMarkdown>
                      {selectedTask.aiSummaryFormatted || selectedTask.resumoTarefa}
                    </ReactMarkdown>
                  </div>
                </div>

                <div className="space-y-2">
                  <Label className="text-muted-foreground text-xs uppercase font-bold">Conteúdo Original do E-mail</Label>
                  <ScrollArea className="h-[200px] w-full rounded-md border p-4">
                    <div className="text-sm text-muted-foreground whitespace-pre-wrap leading-relaxed">
                      {selectedTask.emailBody || "Conteúdo do corpo do e-mail não disponível."}
                    </div>
                  </ScrollArea>
                </div>

                <div className="flex flex-col gap-2 pt-2 border-t">
                  <span className="text-xs text-muted-foreground font-medium mb-1">Ações Rápidas</span>
                  
                  {/* NOVO CAMPO DE MENSAGEM */}
                  <div className="space-y-2">
                    <Label htmlFor="reply-message">Mensagem de Resposta (Opcional)</Label>
                    <Textarea
                      id="reply-message"
                      placeholder="Adicione uma mensagem para o solicitante..."
                      value={replyMessage}
                      onChange={(e) => setReplyMessage(e.target.value)}
                      rows={2}
                    />
                  </div>

                  {/* NOVOS BOTÕES DE AÇÃO */}
                  <div className="flex gap-3">
                    {/* Botão ENVIAR (sem concluir) */}
                    <Button 
                      variant="outline" 
                      className="flex-1 gap-2"
                      onClick={handleSendReply}
                      disabled={sendReplyMutation.isPending || !replyMessage.trim()}
                    >
                      {sendReplyMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Send className="h-4 w-4" />}
                      Enviar
                    </Button>
                    
                    {/* Botão MARCAR COMO VISTO */}
                    <Button 
                      variant="outline" 
                      className="flex-1 gap-2"
                      onClick={handleMarkAsViewed}
                      disabled={markAsViewedMutation.isPending}
                    >
                      {markAsViewedMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Eye className="h-4 w-4" />}
                      Marcar como Visto
                    </Button>
                  </div>

                  {/* Botão CONCLUIR TAREFA (maior e verde) */}
                  <Button 
                    className="w-full mt-2 gap-2 bg-green-600 hover:bg-green-700 text-white"
                    onClick={handleCompleteTask}
                    disabled={completeTaskMutation.isPending}
                  >
                    {completeTaskMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <CheckCircle2 className="h-4 w-4" />}
                    Concluir Tarefa
                  </Button>
                </div>
              </div>
            )}
          </DialogContent>
        </Dialog>

        {isLoading && (
          <div className="flex h-40 items-center justify-center">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
            <span className="ml-3 text-muted-foreground">Carregando tarefas...</span>
          </div>
        )}

        {isError && (
          <div className="rounded-lg border border-destructive/50 bg-destructive/10 p-4 text-destructive flex items-center gap-2">
            <AlertCircle className="h-5 w-5" />
            Erro ao carregar tarefas. Verifique se o Backend está rodando.
          </div>
        )}

        {!isLoading && !isError && (
          <div className="space-y-6">
            {taskList.length === 0 ? (
              <div className="text-center py-12 text-muted-foreground border rounded-lg border-dashed">
                Nenhuma tarefa pendente encontrada. Aguardando novos e-mails...
              </div>
            ) : (
              <>
                <TaskSection 
                  title="Tarefas Urgentes" 
                  icon={<AlertCircle className="h-5 w-5" />}
                  tasks={urgentTasks}
                />
                
                <TaskSection 
                  title="Tarefas Medianas" 
                  icon={<Clock className="h-5 w-5" />}
                  tasks={medianTasks}
                />
                
                <TaskSection 
                  title="Tarefas Rotineiras" 
                  icon={<CheckCircle2 className="h-5 w-5" />}
                  tasks={routineTasks}
                />
              </>
            )}
          </div>
        )}
      </div>
    </AppLayout>
  );
};

export default Tasks;