# 🦷 Cadent

> **Sistema de Gerenciamento de Consultório Odontológico**

API REST robusta e bem documentada para gerenciar pacientes, dentistas, agendamentos, procedimentos e pagamentos em consultórios odontológicos.

[![Java](https://img.shields.io/badge/Java-11%2B-blue?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-green?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![H2 Database](https://img.shields.io/badge/Database-H2-yellow?style=flat-square&logo=h2-database)](https://www.h2database.com/)
[![REST API](https://img.shields.io/badge/API-REST-FF6B6B?style=flat-square)](https://restfulapi.net/)
[![Validações](https://img.shields.io/badge/Validações-Completas-green?style=flat-square)](https://github.com/seu-usuario/cadent#validações)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

---

## 📋 Sumário

- [Visão Geral](#-visão-geral)
- [Recursos](#-recursos)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Como Rodar](#-como-rodar)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [API Endpoints](#-api-endpoints)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Arquitetura](#-arquitetura)
- [Banco de Dados](#-banco-de-dados)
- [Tecnologias](#-tecnologias)
- [Roadmap](#-roadmap)
- [Contribuindo](#-contribuindo)
- [Licença](#-licença)
- [Autor](#-autor)

---

## 🎯 Visão Geral

**Cadent** é uma API REST desenvolvida em Java com Spring Boot, projetada para simplificar a gestão de consultórios odontológicos. 

O projeto combina **funcionalidade robusta** com **segurança e validações profissionais**, sendo ideal tanto para gerenciar dados reais quanto para aprender conceitos fundamentais de desenvolvimento backend.

### ⭐ Destaques

✅ **Validações Profissionais** - CPF, Email, CRM validados  
✅ **Gerenciamento de Entidades JPA** - Tratamento de entidades acopladas e desacopladas no salvamento  
✅ **Tratamento de Erros** - Mensagens claras e estruturadas via Exception Handler Central
✅ **Status HTTP Corretos** - 201, 400, 404, 409, 500   

---

## ✨ Recursos

### Core Features

- **👥 Gerenciamento de Pacientes**
  - Cadastro com CPF validado e único
  - Email único e validado
  - Histórico completo de consultas
  - Dados de contato e endereço

- **👨‍⚕️ Gestão de Dentistas**
  - Registro com CRM validado e único
  - Especialidades
  - Agenda pessoal

- **📅 Agendamentos Avançados**
  - Marcação com data/hora
  - Múltiplos procedimentos por consulta
  - Status rastreável (Agendado, Realizado, Cancelado)
  - Relacionamento N:N com Procedimentos

- **💊 Procedimentos**
  - Catálogo extensível
  - Valores padrão customizáveis
  - Histórico de uso

- **💰 Pagamentos**
  - Múltiplos métodos (`DINHEIRO`, `DEBITO`, `CREDITO`, `PIX`)
  - Status financeiro (`PENDENTE`, `PAGO`, `CANCELADO`)
  - Vinculado a agendamentos
  - Métricas com totais de pagamentos pagos e pendentes

### Technical Features

- 🛡️ **Validações Customizadas** - CPF, Email, CRM
- 📊 **Tratamento Global de Erros** - ControllerAdvice centralizando erros
- 🔍 **Queries Customizadas** - Busca avançada por período, paciente, dentista
- 🗄️ **H2 Database** - Console visual para inspecionar dados
- 📝 **Anotações JPA** - Código didático e profissional
- ✅ **Campos Obrigatórios** - Validação de dados necessários
- 🚀 **RESTful** - Endpoints seguindo padrões REST

---

## 📦 Pré-requisitos

| Ferramenta | Versão | Link |
|-----------|--------|------|
| **Java Development Kit (JDK)** | 11 ou superior | [Download](https://www.oracle.com/java/technologies/downloads/) |
| **Maven** | 3.8 ou superior | [Download](https://maven.apache.org/download.cgi) |
| **VS Code** (Recomendado) | Última versão | [Download](https://code.visualstudio.com/) |
| **Git** | Qualquer versão recente | [Download](https://git-scm.com/) |
| **Bruno** (Para testes) | Última versão | [Download](https://www.usebruno.com/) |

---

## 🚀 Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/cadent.git
cd cadent
```

### 2. Instale as Dependências

```bash
mvn clean install
```

### 3. Verifique a Estrutura

Você deve ter:
```
src/main/java/com/cadent/
├── controller/      # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # Entidades JPA
├── exception/       # Exceções customizadas
├── repository/      # Acesso a dados
└── validation/      # Validadores customizados
```

---

## ▶️ Como Rodar

### Opção 1: Maven (Recomendado)

```bash
mvn spring-boot:run
```

### Opção 2: VS Code com Debug

1. Pressione `Ctrl + Shift + D` (Run and Debug)
2. Clique no botão verde **Run**

### ✅ Verificar se Está Rodando

```bash
curl http://localhost:8080/api/pacientes
```

Deve retornar: `[]`

---

## 📁 Estrutura do Projeto

```
cadent/
├── src/main/java/com/cadent/
│   ├── controller/
│   │   ├── PacienteController.java
│   │   ├── DentistaController.java
│   │   └── AgendamentoController.java
│   │   └── PagamentoController.java
│   │   └── ProcedimentoController.java
│   │
│   ├── dto/
│   │   └── ErrorResponse.java
│   │
│   ├── entity/
│   │   ├── Paciente.java
│   │   ├── Dentista.java
│   │   ├── Procedimento.java
│   │   ├── Agendamento.java
│   │   └── Pagamento.java
│   │
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── DuplicateResourceException.java
│   │
│   ├── repository/
│   │   ├── PacienteRepository.java
│   │   ├── DentistaRepository.java
│   │   ├── ProcedimentoRepository.java
│   │   ├── AgendamentoRepository.java
│   │   └── PagamentoRepository.java
│   │
│   └── validation/
│       ├── ValidCpf.java
│       └── CpfValidator.java
│
└── src/main/resources/
    └── application.properties
```

---

## 🔌 API Endpoints

### Pacientes

| Método | Endpoint | Descrição | Status Esperado |
|--------|----------|-----------|-----------------|
| GET | `/api/pacientes` | Lista todos | 200 |
| GET | `/api/pacientes/{id}` | Busca por ID | 200, 404 |
| GET | `/api/pacientes/cpf/{cpf}` | Busca por CPF | 200, 404 |
| POST | `/api/pacientes` | Criar novo | 201, 400, 409 |
| PUT | `/api/pacientes/{id}` | Atualizar | 200, 400, 404 |
| DELETE | `/api/pacientes/{id}` | Deletar | 204, 404 |

### Dentistas

| Método | Endpoint | Descrição | Status Esperado |
|--------|----------|-----------|-----------------|
| GET | `/api/dentistas` | Lista todos | 200 |
| GET | `/api/dentistas/{id}` | Busca por ID | 200, 404 |
| POST | `/api/dentistas` | Criar novo | 201, 400, 409 |
| PUT | `/api/dentistas/{id}` | Atualizar | 200, 400, 404 |
| DELETE | `/api/dentistas/{id}` | Deletar | 204, 404 |

### Procedimento

| Método | Endpoint | Descrição | Status Esperado |
|--------|----------|-----------|-----------------|
| GET | `/api/procedimentos` | Lista todos | 200 |
| GET | `/api/procedimentos/{id}` | Busca por ID | 200, 404 |
| POST | `/api/procedimentos` | Criar novo | 201, 400, 409 |
| PUT | `/api/procedimentos/{id}` | Atualizar | 200, 400, 404 |
| DELETE | `/api/procedimentos/{id}` | Deletar | 204, 404 |

### Agendamentos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/agendamentos` | Lista todos |
| GET | `/api/agendamentos/{id}` | Busca por ID |
| GET | `/api/agendamentos/paciente/{id}` | Por paciente |
| GET | `/api/agendamentos/dentista/{id}` | Por dentista |
| POST | `/api/agendamentos` | Criar novo |
| PUT | `/api/agendamentos/{id}` | Atualizar |
| DELETE | `/api/agendamentos/{id}` | Cancelar |

### Pagamento

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/pagamentos` | Lista todos |
| GET | `/api/pagamentos/{id}` | Busca por ID |
| GET | `/api/pagamentos/agendamento/{id}` | Por agendamento |
| GET | `/api/pagamentos/status/{status}` | Por status (PENDENTE, PAGO, CANCELADO) |
| GET | `/api/pagamentos/stats/total-pago` | Total de pagamentos realizados |
| GET | `/api/pagamentos/stats/total-pendente` | Total de pagamentos pendentes |
| POST | `/api/pagamentos` | Criar novo |
| PUT | `/api/pagamentos/{id}` | Atualizar |
| DELETE | `/api/pagamentos/{id}` | Cancelar |

---
🛡️ Validações
### Paciente

| Campo | Validação | Exemplo Válido | Exemplo Inválido |
|-------|-----------|-----------------|------------------|
| **Nome** | Obrigatório, não vazio | "João Silva" | "" (vazio) |
| **CPF** | Validado com dígitos verificadores | "12345678909" | "11111111111" (repetido) |
| **Email** | Formato válido, único | "joao@email.com" | "email_invalido" |
| **Telefone** | Opcional | "11999999999" | — |

### Dentista

| Campo | Validação | Exemplo Válido | Exemplo Inválido |
|-------|-----------|-----------------|------------------|
| **Nome** | Obrigatório | "Dr. Carlos" | "" (vazio) |
| **CRM** | Obrigatório, único | "12345-SP" | "" (vazio) |
| **Especialidade** | Opcional | "Clínico Geral" | — |

### Agendamento

| Campo | Validação | Obrigatório |
|-------|-----------|-------------|
| Paciente | Deve existir no BD | ✅ Sim |
| Dentista | Deve existir no BD | ✅ Sim |
| Data/Hora | Data válida | ✅ Sim |
| Status | AGENDADO, REALIZADO, CANCELADO | ✅ Sim |

---

## 💡 Exemplos de Uso

Para exemplos práticos completos com requisições prontas para testar, consulte a documentação:

- 📖 **[Exemplos com Bruno](./docs/exemplos-bruno.md)** - Requisições prontas para copiar/colar
- 📖 **[Documentação de Validações](./docs/validacoes.md)** - Regras e como validar dados

---

## 🏗️ Arquitetura

### Padrão de Camadas

```
HTTP Request
    ↓
┌─────────────────────────┐
│   Controller (@Valid)   │  ← Recebe requisições, valida estrutura
└─────────────────────────┘
    ↓
┌─────────────────────────┐
│ Service (Lógica)        │  ← Verifica duplicação, regras de negócio
└─────────────────────────┘
    ↓
┌─────────────────────────┐
│ Repository (JPA)        │  ← Acesso a dados, queries customizadas
└─────────────────────────┘
    ↓
┌─────────────────────────┐
│ Database (H2)           │  ← Persiste dados
└─────────────────────────┘
    ↓
┌─────────────────────────┐
│ GlobalExceptionHandler  │  ← Trata exceções, formata erros
└─────────────────────────┘
    ↓
HTTP Response JSON
```

### Fluxo de Validação

```
1. Request chega no Controller
   ↓
2. @Valid dispara validadores
   - @NotBlank
   - @Email
   - @ValidCpf (customizado)
   ↓
3. Se validação falha:
   → MethodArgumentNotValidException
   → GlobalExceptionHandler captura
   → Retorna 400 com erros por campo
   ↓
4. Se validação passa:
   → Service verifica duplicação
   → Se duplicado: lança DuplicateResourceException
   → GlobalExceptionHandler captura → 409 Conflict
   ↓
5. Se tudo OK:
   → Repository.save()
   → Retorna 201 Created
```

---

## 🗄️ Banco de Dados

### Acessar H2 Console

```
http://localhost:8080/h2-console
```

Credenciais padrão (pré-preenchidas):
- URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (deixe em branco)

### Tabelas Criadas

- `PACIENTE` - Pacientes cadastrados
- `DENTISTA` - Dentistas cadastrados
- `PROCEDIMENTO` - Catálogo de procedimentos
- `AGENDAMENTO` - Agendamentos realizados
- `AGENDAMENTO_PROCEDIMENTO` - Relacionamento N:N
- `PAGAMENTO` - Histórico de pagamentos

---

## 🛠️ Tecnologias

### Backend
- **Java 11+** - Linguagem de programação
- **Spring Boot 2.7.18** - Framework web
- **Spring Data JPA** - ORM para banco de dados
- **Lombok** - Reduz boilerplate
- **javax.validation** - Validações de dados

### Banco de Dados
- **H2 Database** - Banco em memória
- **Hibernate** - Mapeamento objeto-relacional

### Build & Deploy
- **Maven 3.8+** - Gerenciador de dependências
- **Git** - Controle de versão

---

## 📈 Roadmap

### v1.0 - MVP (Atual) ✅

- [x] CRUD Pacientes, Dentistas, Agendamentos
- [x] Validações de CPF, Email, CRM
- [x] Tratamento global de erros
- [x] H2 Database
- [x] Endpoints REST profissionais

### v1.1 (Próximo - 2 semanas)

- [ ] Testes unitários (JUnit 5)
- [ ] Testes de integração
- [ ] Refinar queries SQL
- [ ] Adicionar paginação

### v2.0 (1-2 meses)

- [ ] Frontend em React/Vue
- [ ] Autenticação JWT
- [ ] Swagger/OpenAPI
- [ ] Relatórios PDF

### v3.0 (Futuro)

- [ ] PostgreSQL em produção
- [ ] Docker containerization
- [ ] Deploy na nuvem (AWS, Heroku)
- [ ] CI/CD com GitHub Actions

---

## 📄 Licença

MIT License - veja [LICENSE](LICENSE) para detalhes.

---

## 👤 Autor

**Camila Cardoso**  
- GitHub: [@CamilaCardoso](https://github.com/CamilaCardoso)
- LinkedIn: [ccardosooliveira](https://www.linkedin.com/in/ccardosooliveira/)

---

## � Documentação Completa

- 📖 [Validações e Regras de Dados](./docs/validacoes.md) - Saiba como cada campo é validado
- 💬 [Exemplos de Requisições com Bruno](./docs/exemplos-bruno.md) - Requisições prontas para testar

---

<div align="center">

### ⭐ Se este projeto ajudou você, considere dar uma estrela!

**[Voltar ao Topo](#-cadent)**

</div>
