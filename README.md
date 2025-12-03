# 📧 Mail Task's AI

> **Transforme a sua caixa de entrada em produtividade.**
> O Mail Task's AI é uma plataforma SaaS que utiliza Inteligência Artificial para ler e-mails do Outlook, extrair solicitações automaticamente e organizá-las como tarefas priorizadas num dashboard intuitivo.

---

## 📋 Sobre o Projeto

Este sistema resolve o problema da desorganização em caixas de e-mail corporativas. Através de uma integração segura com a **Microsoft Graph API**, o sistema monitoriza novas mensagens, utiliza a **OpenAI (GPT-4o)** para interpretar o conteúdo, definir urgência (Urgente, Mediano, Rotineira) e categorizar a demanda (Financeiro, RH, Dev, etc.).

### ✨ Principais Funcionalidades

* **Automação de Tarefas:** Conversão automática de e-mails em cartões de tarefas.
* **Inteligência Artificial:** Análise de sentimento, resumo técnico e classificação de urgência via OpenAI.
* **Integração OAuth2:** Conexão segura com contas Microsoft Outlook.
* **Dashboard Analytics:** Métricas em tempo real sobre produtividade e volume de tarefas.
* **Gestão de Acesso:** Sistema de login seguro com JWT e criptografia de dados sensíveis.

---

## 🛠️ Tecnologias Utilizadas

### Backend (API)
* **Java 21**
* **Spring Boot 3.5.7** (Web, Security, Data JPA, OAuth2 Client)
* **PostgreSQL** (Base de dados relacional)
* **Lombok** (Produtividade e redução de *boilerplate*)
* **Maven** (Gestão de dependências)

### Frontend (Client)
* **React + Vite**
* **TypeScript**
* **Tailwind CSS** & **Shadcn/ui** (Estilização e Componentes)
* **TanStack Query** (Gestão de estado assíncrono)
* **Axios** (Comunicação HTTP)

### Integrações & Infraestrutura
* **OpenAI API** (Modelo GPT-4o-mini)
* **Microsoft Graph API**
* **Docker** (Opcional, para contentorização)
* **Microsoft Azure** (Plataforma de Deploy)

---

## ⚙️ Configuração do Ambiente

Para rodar o projeto localmente, precisará de configurar as variáveis de ambiente para proteger as suas credenciais.

### 1. Backend (`backend/src/main/resources/application.yml`)
O backend espera as seguintes variáveis de ambiente. Pode configurá-las na sua IDE ou num ficheiro `.env` se usar Docker.

```yaml
DB_URL: jdbc:postgresql://localhost:5432/mailtasksai
DB_USERNAME: postgres
DB_PASSWORD: <SUA_SENHA_POSTGRES>
AZURE_CLIENT_ID: <SEU_CLIENT_ID_AZURE>
AZURE_CLIENT_SECRET: <SEU_CLIENT_SECRET_AZURE>
AZURE_TENANT_ID: common
AZURE_REDIRECT_URI: http://localhost:8080/api/auth/callback
ENCRYPTION_SECRET_KEY: <CHAVE_32_CHARS_ALEATORIA>
OPENAI_API_KEY: <SUA_KEY_OPENAI>
JWT_SECRET: <CHAVE_JWT_SEGURA>
ADMIN_PASSWORD: <SENHA_INICIAL_ADMIN>
```
### 2. Frontend (`.env`)
Crie um ficheiro .env na raiz da pasta do frontend

```yaml
VITE_API_URL=http://localhost:8080/api
```

### 🚀 Como Rodar o Projeto
Pré-requisitos
Java JDK 21+

Node.js 18+

PostgreSQL rodando e com a base de dados mailtasksai criada.

### Passo 1: Executar o Backend
Navegue até à pasta backend:

```yaml
cd backend
```

Execute a aplicação via Maven Wrapper:

```yaml
./mvnw spring-boot:run
```
A API estará a rodar em http://localhost:8080. A documentação Swagger estará em http://localhost:8080/swagger-ui.html.

### Passo 2: Executar o Frontend
Navegue até à raiz do projeto (onde está o package.json)

```yaml
cd .. # se estiver na pasta backend
```

Instale as dependências:

```yaml
npm install
```

Inicie o servidor de desenvolvimento:

```yaml
npm run dev
```
O frontend estará acessível em http://localhost:5173.

📚 Estrutura do Projeto

```yaml
mail-tasks-ai/
├── backend/                # Código fonte da API Java Spring Boot
│   ├── src/main/java       # Controllers, Services, Models
│   └── src/main/resources  # Configurações (application.yml)
├── src/                    # Código fonte do Frontend React
│   ├── components/         # Componentes reutilizáveis (UI)
│   ├── pages/              # Páginas da aplicação (Dashboard, Tasks)
│   ├── services/           # Integração com API (Axios)
│   └── ...
└── ...
```

### 👤 Autor
### Desenvolvido por Thomás Gonçalves.

Nota: Este projeto foi desenvolvido para fins de portfólio/comercial e utiliza chaves de API que não devem ser partilhadas publicamente. Certifique-se de manter os seus ficheiros .env e configurações locais seguros.