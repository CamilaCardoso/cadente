# 💡 Exemplos de Uso com Bruno

Este documento contém exemplos práticos de como usar a API Cadent com a ferramenta **Bruno**.

---

## 🚀 Preparação

### 1. Instale Bruno

Baixe em: https://www.usebruno.com/

### 2. Inicie a Aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### 3. Crie uma Collection no Bruno

1. Abra Bruno
2. Clique em "Create Collection"
3. Nomeie como "Cadent API"
4. Crie as requisições abaixo

---

## 📝 Exemplos de Requisições

### 1️⃣ Criar Paciente (Sucesso)

**Método:** `POST`  
**URL:** `http://localhost:8080/api/pacientes`  
**Content-Type:** `application/json`

#### Request (Body):
```json
{
  "nome": "João Silva",
  "cpf": "11144477735",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "dataNascimento": "1990-05-15"
}
```

#### Response (201 Created):
```json
{
  "id": 1,
  "nome": "João Silva",
  "cpf": "11144477735",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "dataNascimento": "1990-05-15",
  "endereco": null,
  "dataCadastro": "2025-02-15T10:30:45"
}
```

✅ **Status esperado:** `201 Created`

---

### 2️⃣ Criar Paciente (CPF Inválido)

**Método:** `POST`  
**URL:** `http://localhost:8080/api/pacientes`

#### Request (Body):
```json
{
  "nome": "Maria",
  "cpf": "123",
  "email": "maria@email.com"
}
```

#### Response (400 Bad Request):
```json
{
  "status": 400,
  "mensagem": "Erro de validação",
  "detalhes": "Um ou mais campos estão inválidos",
  "erros": {
    "cpf": "CPF deve conter exatamente 11 dígitos"
  },
  "timestamp": "2025-02-15T10:35:20",
  "caminho": "/api/pacientes"
}
```

❌ **Status esperado:** `400 Bad Request`

---

### 3️⃣ Criar Paciente (CPF Duplicado)

**Método:** `POST`  
**URL:** `http://localhost:8080/api/pacientes`

#### Request (Body - após já ter criado com CPF 11144477735):
```json
{
  "nome": "Outro João",
  "cpf": "11144477735",
  "email": "outro@email.com"
}
```

#### Response (409 Conflict):
```json
{
  "status": 409,
  "mensagem": "Recurso já existe",
  "detalhes": "CPF 11144477735 já cadastrado",
  "timestamp": "2025-02-15T10:36:45",
  "caminho": "/api/pacientes"
}
```

⚠️ **Status esperado:** `409 Conflict`

---

### 4️⃣ Listar Todos os Pacientes

**Método:** `GET`  
**URL:** `http://localhost:8080/api/pacientes`

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "cpf": "11144477735",
    "email": "joao@email.com",
    "dataCadastro": "2025-02-15T10:30:45"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "cpf": "98765432100",
    "email": "maria@email.com",
    "dataCadastro": "2025-02-15T10:35:00"
  }
]
```

✅ **Status esperado:** `200 OK`

---

### 5️⃣ Buscar Paciente por ID

**Método:** `GET`  
**URL:** `http://localhost:8080/api/pacientes/1`

#### Response (200 OK):
```json
{
  "id": 1,
  "nome": "João Silva",
  "cpf": "11144477735",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "dataNascimento": "1990-05-15",
  "endereco": null,
  "dataCadastro": "2025-02-15T10:30:45"
}
```

✅ **Status esperado:** `200 OK`

---

### 6️⃣ Buscar Paciente Inexistente

**Método:** `GET`  
**URL:** `http://localhost:8080/api/pacientes/9999`

#### Response (404 Not Found):
```json
{
  "status": 404,
  "mensagem": "Recurso não encontrado",
  "detalhes": "Paciente com ID 9999 não encontrado",
  "timestamp": "2025-02-15T10:37:10",
  "caminho": "/api/pacientes/9999"
}
```

❌ **Status esperado:** `404 Not Found`

---

### 7️⃣ Atualizar Paciente

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/pacientes/1`

#### Request (Body):
```json
{
  "nome": "João Silva da Costa",
  "cpf": "11144477735",
  "email": "joao.costa@email.com",
  "telefone": "11988888888",
  "dataNascimento": "1990-05-15"
}
```

#### Response (200 OK):
```json
{
  "id": 1,
  "nome": "João Silva da Costa",
  "cpf": "11144477735",
  "email": "joao.costa@email.com",
  "telefone": "11988888888",
  "dataNascimento": "1990-05-15",
  "endereco": null,
  "dataCadastro": "2025-02-15T10:30:45"
}
```

✅ **Status esperado:** `200 OK`

---

### 8️⃣ Deletar Paciente

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/pacientes/1`

#### Response (204 No Content):
```
(sem corpo na resposta)
```

✅ **Status esperado:** `204 No Content`

---

## 👨‍⚕️ Exemplos com Dentistas

### Criar Dentista

**Método:** `POST`  
**URL:** `http://localhost:8080/api/dentistas`

#### Request (Body):
```json
{
  "nome": "Dr. Carlos Silva",
  "crm": "123456-SP",
  "especialidade": "Clínico Geral",
  "telefone": "11987654321"
}
```

#### Response (201 Created):
```json
{
  "id": 1,
  "nome": "Dr. Carlos Silva",
  "crm": "123456-SP",
  "especialidade": "Clínico Geral",
  "telefone": "11987654321"
}
```

✅ **Status esperado:** `201 Created`

---

### Listar Dentistas

**Método:** `GET`  
**URL:** `http://localhost:8080/api/dentistas`

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "nome": "Dr. Carlos Silva",
    "crm": "123456-SP",
    "especialidade": "Clínico Geral",
    "telefone": "11987654321"
  }
]
```

---

## 📅 Exemplos com Agendamentos

### Criar Agendamento

**Método:** `POST`  
**URL:** `http://localhost:8080/api/agendamentos`

#### Request (Body):
```json
{
  "pacienteId": 1,
  "dentistaId": 1,
  "dataHora": "2025-03-15T10:30:00",
  "status": "AGENDADO",
  "observacoes": "Limpeza e avaliação"
}
```

#### Response (201 Created):
```json
{
  "id": 1,
  "pacienteId": 1,
  "dentistaId": 1,
  "dataHora": "2025-03-15T10:30:00",
  "status": "AGENDADO",
  "observacoes": "Limpeza e avaliação"
}
```

✅ **Status esperado:** `201 Created`

---

### Listar Agendamentos por Paciente

**Método:** `GET`  
**URL:** `http://localhost:8080/api/agendamentos/paciente/1`

#### Response (200 OK):
```json
[
  {
    "id": 1,
    "pacienteId": 1,
    "dentistaId": 1,
    "dataHora": "2025-03-15T10:30:00",
    "status": "AGENDADO",
    "observacoes": "Limpeza e avaliação"
  }
]
```

---

## 📅 Exemplos com Pagamentos

### Criar Pagamento

**Método:** `POST`  
**URL:** `http://localhost:8080/api/pagamentos`

#### Request (Body):
```json
{
"agendamento": {
  "id": 1
   },
    "dataPagamento": "2026-09-07T14:30:00",
    "valorTotal": 150.00,
    "metodo": "DINHEIRO",
    "status": "PENDENTE"
}
```

#### Response (201 Created):
```json
{
  "id": 1,
  "dataPagamento": "2026-09-07T14:30:00",
  "valorTotal": 150.00,
  "metodo": "DINHEIRO",
  "status": "PENDENTE"
}
```

✅ **Status esperado:** `201 Created`

---

### Listar Pagamento por Id de Agendamento

**Método:** `GET`  
**URL:** `http://localhost:8080/api/pagamentos/agendamento/1`

#### Response (200 OK):
```json
{
  "id": 1,
  "dataPagamento": "2026-09-10T15:00:00",
  "valorTotal": 350.00,
  "metodo": "PIX",
  "status": "PAGO"
}
```

---

## 🔄 Fluxo Completo

Siga esta sequência lógica para testar o sistema do zero, respeitando as integridades referenciais e relacionamentos do banco de dados:

1. **POST** `/api/pacientes` - Crie o paciente base para a consulta
2. **POST** `/api/dentistas` - Crie o dentista responsável pelo atendimento
3. **POST** `/api/procedimentos` - Cadastre os procedimentos no catálogo (ex: Limpeza, Restauração)
4. **POST** `/api/agendamentos` - Crie o agendamento vinculando `paciente_id`, `dentista_id` e a lista de `procedimentos` (tabela de junção)
5. **POST** `/api/pagamentos` - Registre a quitação/pagamento financeiro atrelado ao `agendamento_id`
6. **GET** `/api/agendamentos/paciente/1` - Consulte o histórico do paciente confirmando se trouxe dados, procedimentos e pagamentos
7. **GET** `/api/pagamentos/agendamento/1` - Verifique os detalhes do pagamento gerado para a consulta
8. **PUT** `/api/agendamentos/1` - Atualize o status da consulta (ex: de `AGENDADO` para `REALIZADO`) ou observações
9. **DELETE** `/api/pagamentos/1` ou **DELETE** `/api/agendamentos/1` - Remova/cancele registros para testar a remoção em cascata (opcional)

---

## 💡 Dicas Bruno

### 1. Salvar Respostas como Templates

Depois de testar uma requisição:
1. Clique em "Save as Example"
2. Use para próximas requisições similares

### 2. Usar Variáveis de Ambiente

Crie variáveis para facilitar:
```json
{
  "baseUrl": "http://localhost:8080",
  "pacienteId": "1",
  "dentistaId": "1"
}
```

Depois use: `{{baseUrl}}/api/pacientes/{{pacienteId}}`

### 3. Validar Respostas

Bruno permite verificar:
- Status code
- Tempo de resposta
- Estrutura JSON

---

## 📚 CPF de Teste

Use este CPF válido para testes:
- **11144477735** ✅ CPF válido

CPFs que devem falhar:
- **123** ❌ Menos de 11 dígitos
- **11111111111** ❌ Todos os dígitos iguais
- **12345678900** ❌ Dígitos verificadores incorretos

---

## 🆘 Troubleshooting

### "Connection refused"
- Verifique se a aplicação está rodando: `mvn spring-boot:run`
- Confirme URL: `http://localhost:8080`

### "400 Bad Request"
- Verifique JSON do body
- Confirme Content-Type: `application/json`
- Leia a mensagem de erro na resposta

### "409 Conflict"
- Dados já existem (CPF, Email ou CRM duplicado)
- Use dados diferentes

### "404 Not Found"
- ID não existe no banco
- Crie o recurso antes

---

## 📖 Mais Informações

- [Documentação de Validações](./validacoes.md)
- [README Principal](../README.md)
