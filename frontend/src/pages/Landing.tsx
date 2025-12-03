import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { CheckCircle2, Mail, Zap, BarChart3, Shield, ArrowRight, Send } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

const Landing = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    company: "",
  });
  const features = [
    {
      icon: <Mail className="h-6 w-6" />,
      title: "Integração com Outlook",
      description: "Conecte-se diretamente ao seu e-mail e transforme mensagens em tarefas automaticamente",
    },
    {
      icon: <Zap className="h-6 w-6" />,
      title: "IA Inteligente",
      description: "Classificação automática de urgência e categorização de tarefas usando inteligência artificial",
    },
    {
      icon: <BarChart3 className="h-6 w-6" />,
      title: "Analytics em Tempo Real",
      description: "Visualize métricas e acompanhe o desempenho da sua equipe em um dashboard completo",
    },
    {
      icon: <Shield className="h-6 w-6" />,
      title: "Segurança Garantida",
      description: "Proteção de dados de nível empresarial com criptografia e conformidade LGPD",
    },
  ];

  const benefits = [
    "Reduza até 60% do tempo gasto em gerenciamento de e-mails",
    "Nunca perca um prazo importante novamente",
    "Organize automaticamente sua caixa de entrada",
    "Colabore melhor com sua equipe",
    "Aumente a produtividade em até 40%",
  ];

  const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  try {
    await fetch('http://localhost:8080/api/public/contact', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    });
    toast.success("Formulário enviado! Entraremos em contato.");
    setFormData({ name: "", email: "", phone: "", company: "" });
  } catch (err) {
    toast.error("Erro ao enviar.");
  }
};

  const scrollToContact = () => {
    document.getElementById("contact")?.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <div className="min-h-screen bg-background scroll-smooth">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b border-border/40 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto flex h-16 items-center justify-between px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-hero">
              <Mail className="h-5 w-5 text-white" />
            </div>
            <span className="font-display text-lg sm:text-xl font-bold">Mail Task's AI</span>
          </div>
          <nav className="hidden items-center gap-4 md:flex lg:gap-6">
            <a href="#features" className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground">
              Recursos
            </a>
            <a href="#benefits" className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground">
              Benefícios
            </a>
            <Button variant="default" size="sm" onClick={scrollToContact}>
              Entrar em Contato
            </Button>
          </nav>
          <Button variant="default" size="sm" className="md:hidden" onClick={scrollToContact}>
            Contato
          </Button>
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative overflow-hidden py-16 sm:py-20 md:py-32">
        <div className="absolute inset-0 bg-gradient-hero opacity-5" />
        <div className="container relative mx-auto px-4 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-3xl text-center animate-fade-in">
            <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-primary/20 bg-primary/5 px-3 py-1.5 text-xs sm:text-sm font-medium text-primary">
              <Zap className="h-3 w-3 sm:h-4 sm:w-4" />
              Transforme e-mails em tarefas
            </div>
            <h1 className="mb-6 font-display text-3xl font-bold tracking-tight sm:text-4xl md:text-5xl lg:text-6xl">
              Gerencie Tarefas do
              <span className="bg-gradient-hero bg-clip-text text-transparent"> Outlook </span>
              com IA
            </h1>
            <p className="mb-8 text-base sm:text-lg text-muted-foreground md:text-xl">
              A solução completa para transformar seus e-mails em tarefas organizadas, priorizadas e rastreáveis. 
              Deixe a IA cuidar da organização enquanto você foca no que importa.
            </p>
            <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
              <Button size="lg" className="group h-12 px-6 sm:px-8 w-full sm:w-auto" onClick={scrollToContact}>
                Solicitar Acesso
                <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
              </Button>
              <Button size="lg" variant="outline" className="h-12 px-6 sm:px-8 w-full sm:w-auto" onClick={() => document.getElementById("features")?.scrollIntoView({ behavior: "smooth" })}>
                Ver Recursos
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-16 sm:py-20 md:py-24">
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
          <div className="mb-12 text-center animate-fade-in">
            <h2 className="mb-4 font-display text-2xl sm:text-3xl font-bold md:text-4xl">
              Recursos Poderosos
            </h2>
            <p className="mx-auto max-w-2xl text-base sm:text-lg text-muted-foreground">
              Tudo que você precisa para gerenciar tarefas de forma eficiente e inteligente
            </p>
          </div>
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {features.map((feature, index) => (
              <Card 
                key={index} 
                className="group relative overflow-hidden border-border/50 bg-card/50 backdrop-blur-sm transition-all duration-500 hover:border-primary/50 hover:shadow-2xl hover:shadow-primary/20 hover:-translate-y-2 hover:scale-[1.02] animate-fade-in"
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <div className="absolute inset-0 bg-gradient-to-br from-primary/10 via-primary/5 to-transparent opacity-0 group-hover:opacity-100 transition-all duration-500" />
                <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary to-primary/50 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-500 origin-left" />
                <CardHeader className="relative z-10">
                  <div className="mb-3 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10 text-primary transition-all duration-500 group-hover:bg-primary/20 group-hover:scale-110 group-hover:rotate-3">
                    <div className="transition-transform duration-500 group-hover:scale-110">
                      {feature.icon}
                    </div>
                  </div>
                  <CardTitle className="text-lg sm:text-xl transition-colors duration-300 group-hover:text-primary">{feature.title}</CardTitle>
                </CardHeader>
                <CardContent className="relative z-10">
                  <CardDescription className="text-sm sm:text-base transition-colors duration-300 group-hover:text-foreground/80">{feature.description}</CardDescription>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Benefits Section */}
      <section id="benefits" className="bg-muted/30 py-16 sm:py-20 md:py-24">
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid gap-12 lg:grid-cols-2 lg:items-center lg:gap-16">
            <div className="animate-fade-in">
              <h2 className="mb-6 font-display text-2xl sm:text-3xl font-bold md:text-4xl">
                Por Que Escolher Mail Task's AI?
              </h2>
              <p className="mb-8 text-base sm:text-lg text-muted-foreground">
                Nossa plataforma foi desenvolvida para profissionais que valorizam seu tempo e 
                precisam de uma solução eficiente para gerenciar tarefas vindas de e-mails.
              </p>
              <ul className="space-y-4">
                {benefits.map((benefit, index) => (
                  <li 
                    key={index} 
                    className="flex items-start gap-3 animate-slide-in"
                    style={{ animationDelay: `${index * 100}ms` }}
                  >
                    <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-primary" />
                    <span className="text-sm sm:text-base">{benefit}</span>
                  </li>
                ))}
              </ul>
            </div>
            <Card className="group bg-gradient-to-br from-primary/10 to-primary/5 border-primary/20 hover:border-primary/40 transition-all duration-500 hover:shadow-2xl hover:shadow-primary/30 hover:-translate-y-2 animate-fade-in" style={{ animationDelay: "200ms" }}>
              <CardHeader>
                <CardTitle className="text-xl sm:text-2xl transition-colors duration-300 group-hover:text-primary">Pronto para Começar?</CardTitle>
                <CardDescription className="text-sm sm:text-base transition-colors duration-300 group-hover:text-foreground/80">
                  Solicite acesso à plataforma e transforme a maneira como você gerencia suas tarefas
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button className="w-full transition-transform duration-300 group-hover:scale-105" size="lg" onClick={scrollToContact}>
                  Solicitar Acesso
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Contact Form Section */}
      <section id="contact" className="py-16 sm:py-20 md:py-24">
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
          <div className="mx-auto max-w-2xl">
            <Card className="group border-border/50 shadow-xl hover:shadow-2xl hover:shadow-primary/20 transition-all duration-500 animate-fade-in hover:-translate-y-1" style={{ animationDelay: "200ms" }}>
              <CardHeader>
                <CardTitle className="transition-colors duration-300 group-hover:text-primary">Solicite Acesso</CardTitle>
                <CardDescription className="transition-colors duration-300 group-hover:text-foreground/80">
                  Preencha o formulário abaixo e nossa equipe entrará em contato em breve
                </CardDescription>
              </CardHeader>
              <CardContent>
                <form onSubmit={handleSubmit} className="space-y-6">
                  <div className="space-y-2">
                    <Label htmlFor="name">Nome Completo *</Label>
                    <Input
                      id="name"
                      type="text"
                      placeholder="Seu nome completo"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      required
                      className="h-12 transition-all duration-300 focus:scale-[1.01]"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="email">E-mail *</Label>
                    <Input
                      id="email"
                      type="email"
                      placeholder="seu.email@empresa.com"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      required
                      className="h-12 transition-all duration-300 focus:scale-[1.01]"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="phone">Telefone *</Label>
                    <Input
                      id="phone"
                      type="tel"
                      placeholder="(00) 00000-0000"
                      value={formData.phone}
                      onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                      required
                      className="h-12 transition-all duration-300 focus:scale-[1.01]"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="company">Empresa *</Label>
                    <Input
                      id="company"
                      type="text"
                      placeholder="Nome da sua empresa"
                      value={formData.company}
                      onChange={(e) => setFormData({ ...formData, company: e.target.value })}
                      required
                      className="h-12 transition-all duration-300 focus:scale-[1.01]"
                    />
                  </div>
                  <Button type="submit" size="lg" className="w-full h-12 group">
                    Enviar Solicitação
                    <Send className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                  </Button>
                </form>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t border-border/40 py-12">
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
            <div className="flex items-center gap-2">
              <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-hero">
                <Mail className="h-5 w-5 text-white" />
              </div>
              <span className="font-display text-base sm:text-lg font-bold">Mail Task's AI</span>
            </div>
            <p className="text-xs sm:text-sm text-muted-foreground text-center">
              © 2025 Thomás Gonçalves. Todos os direitos reservados.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Landing;
