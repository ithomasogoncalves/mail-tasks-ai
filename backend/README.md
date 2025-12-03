# Mail Task's AI - Backend API

API robusta construída com Java e Spring Boot para processamento de e-mails e gerenciamento de tarefas com IA.

## 🔧 Tecnologias (Backend)

- **Java 21** + **Spring Boot 3.5.7**
- **Spring Security** + **OAuth2 Client** (Integração Microsoft Graph)
- **Spring Data JPA** + **PostgreSQL**
- **OpenAI API** (Processamento de IA)
- **Lombok** (Redução de boilerplate)

## ⚙️ Configuração de Ambiente

Crie as seguintes variáveis de ambiente ou configure no `application.yml`:

- `DB_USERNAME` / `DB_PASSWORD`: Credenciais do PostgreSQL.
- `AZURE_CLIENT_ID` / `AZURE_CLIENT_SECRET`: Credenciais do Azure AD.
- `AZURE_TENANT_ID`: ID do Tenant Azure.
- `ENCRYPTION_SECRET_KEY`: Chave de 32 chars para criptografia de tokens.
- `OPENAI_API_KEY`: Chave da API da OpenAI.

## 🚀 Como rodar

1. Certifique-se de ter o PostgreSQL rodando e o banco `mailtasksai` criado.
2. Execute via Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run