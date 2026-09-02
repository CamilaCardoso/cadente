# 🛡️ Validações do Cadent

Este documento descreve todas as validações implementadas no sistema Cadent para garantir a integridade dos dados.

---

## 📋 Validações por Entidade

### 👥 Paciente

| Campo | Validação | Exemplo Válido | Exemplo Inválido |
|-------|-----------|-----------------|------------------|
| **Nome** | Obrigatório, não vazio | "João Silva" | "" (vazio) |
| **CPF** | Validado com dígitos verificadores | "12345678909" | "11111111111" (repetido) |
| **Email** | Formato válido, único | "joao@email.com" | "email_invalido" |
| **Telefone** | Opcional | "11999999999" | — |

#### Regras de CPF:
- ✅ Deve conter **exatamente 11 dígitos**
- ✅ Não pode ter todos os dígitos iguais (ex: 11111111111)
- ✅ Dígitos verificadores devem estar corretos
- ✅ Deve ser **único** no banco de dados
- ✅ **Obrigatório** (@NotBlank)

#### Regras de Email:
- ✅ Deve estar em formato válido (conter @)
- ✅ Deve ser **único** no banco de dados
- ✅ Pode ser **vazio/nulo** (opcional)

#### Regras de Nome:
- ✅ **Obrigatório** (não pode ser vazio ou null)
- ✅ Não pode conter apenas espaços

---

### 👨‍⚕️ Dentista

| Campo | Validação | Exemplo Válido | Exemplo Inválido |
|-------|-----------|-----------------|------------------|
| **Nome** | Obrigatório | "Dr. Carlos" | "" (vazio) |
| **CRM** | Obrigatório, único | "12345-SP" | "" (vazio) |
| **Especialidade** | Opcional | "Clínico Geral" | — |
| **Telefone** | Opcional | "11999999999" | — |

#### Regras de CRM:
- ✅ **Obrigatório** (não pode ser vazio)
- ✅ Deve ser **único** no banco de dados
- ✅ Formato: "XXXXXX-UF" (ex: 123456-SP)

#### Regras de Nome:
- ✅ **Obrigatório** (não pode ser vazio)
- ✅ Não pode conter apenas espaços

---

### 📅 Agendamento

| Campo | Validação | Obrigatório |
|-------|-----------|-------------|
| **Paciente** | Deve existir no BD | ✅ Sim |
| **Dentista** | Deve existir no BD | ✅ Sim |
| **Data/Hora** | Data válida | ✅ Sim |
| **Status** | AGENDADO, REALIZADO, CANCELADO | ✅ Sim |
| **Observações** | Texto livre | ❌ Não |

#### Regras de Data:
- ✅ Deve ser uma data válida
- ✅ Deve estar no futuro (recomendado)
- ✅ Status deve ser um dos valores permitidos

---

## 🔍 Exemplos de Validação

### ❌ Nome Vazio
**O que fazer:** Sempre enviar um nome preenchido.

```json
// ❌ ERRADO
{
  "nome": "",
  "cpf": "12345678909",
  "email": "joao@email.com"
}

// ✅ CORRETO
{
  "nome": "João Silva",
  "cpf": "12345678909",
  "email": "joao@email.com"
}
```

---

### ❌ CPF Inválido (Menos de 11 dígitos)

**Resposta de Erro:**
```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "erros": {
    "cpf": "CPF deve conter exatamente 11 dígitos"
  },
  "timestamp": "2025-02-15T10:35:20"
}
```

**Solução:** Enviar CPF com 11 dígitos válidos.

```json
// ❌ ERRADO
{
  "cpf": "123"
}

// ✅ CORRETO
{
  "cpf": "12345678909"
}
```

---

### ❌ CPF com Dígitos Repetidos

**Resposta de Erro:**
```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "erros": {
    "cpf": "CPF com dígitos repetidos é inválido"
  },
  "timestamp": "2025-02-15T10:35:20"
}
```

**Solução:** CPFs com todos os dígitos iguais são inválidos (ex: 11111111111, 22222222222).

```json
// ❌ ERRADO
{
  "cpf": "11111111111"  // Todos os dígitos iguais
}

// ✅ CORRETO
{
  "cpf": "12345678909"  // Dígitos variados
}
```

---

### ❌ CPF com Dígitos Verificadores Incorretos

**Resposta de Erro:**
```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "erros": {
    "cpf": "CPF inválido (dígito verificador incorreto)"
  },
  "timestamp": "2025-02-15T10:35:20"
}
```

**Solução:** O CPF deve seguir o algoritmo de validação. Use um CPF teste válido:
- `11144477735` (CPF válido para teste)

---

### ❌ Email Inválido

**Resposta de Erro:**
```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "erros": {
    "email": "Email inválido"
  },
  "timestamp": "2025-02-15T10:35:20"
}
```

**Solução:** Email deve estar em formato válido com @.

```json
// ❌ ERRADO
{
  "email": "email_invalido"  // Falta @
}

// ✅ CORRETO
{
  "email": "joao@email.com"
}
```

---

### ❌ CPF ou Email Já Cadastrado (Duplicado)

**Resposta de Erro:**
```json
{
  "status": 409,
  "mensagem": "Recurso já existe",
  "detalhes": "CPF 12345678909 já cadastrado",
  "timestamp": "2025-02-15T10:36:45"
}
```

**Solução:** Não é possível cadastrar o mesmo CPF ou Email duas vezes. Use dados diferentes.

```json
// ❌ ERRADO (se já existe um paciente com este CPF)
{
  "nome": "Outro João",
  "cpf": "12345678909",  // CPF já existe no BD
  "email": "outro@email.com"
}

// ✅ CORRETO (novo CPF)
{
  "nome": "João Silva",
  "cpf": "98765432100",  // CPF diferente, não cadastrado
  "email": "joao@email.com"
}
```

---

## ✅ Checklist de Validação

Antes de enviar uma requisição POST ou PUT, verifique:

### Para Pacientes:
- [ ] Nome não está vazio?
- [ ] CPF tem 11 dígitos?
- [ ] CPF não tem todos os dígitos iguais?
- [ ] CPF já não está cadastrado?
- [ ] Email tem @ e domínio?
- [ ] Email já não está cadastrado?

### Para Dentistas:
- [ ] Nome não está vazio?
- [ ] CRM foi preenchido?
- [ ] CRM não está duplicado?

### Para Agendamentos:
- [ ] Paciente ID existe no BD?
- [ ] Dentista ID existe no BD?
- [ ] Data está em formato ISO (YYYY-MM-DDTHH:mm:ss)?
- [ ] Status é um valor permitido?

---

## 🧪 Testando Validações

### Com cURL

```bash
# Teste de CPF inválido
curl -X POST http://localhost:8080/api/pacientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria",
    "cpf": "123",
    "email": "maria@email.com"
  }'

# Resposta esperada: 400 Bad Request
```

### Com Bruno (recomendado)

1. Abra Bruno
2. Crie uma requisição POST para `http://localhost:8080/api/pacientes`
3. Adicione body JSON com dados inválidos
4. Envie e verifique a resposta de erro

---

## 📚 Referências

- [Jakarta Validation](https://jakarta.ee/specifications/validation/)
- [HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [Validadores Customizados Java](https://www.baeldung.com/spring-mvc-custom-validator)
