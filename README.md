# Clima API — N703 Técnicas de Integração de Sistemas

API REST que agrega dados climáticos e geográficos de cidades brasileiras, consumindo APIs públicas externas (Brasil API e Open-Meteo).

## 📋 Pré-requisitos

- **Java 17+** — [Download](https://www.oracle.com/java/technologies/downloads/#java17)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **Git** — [Download](https://git-scm.com/)

### Verificar instalação

```bash
java -version
mvn -version
```

## 🚀 Como rodar

### 1. Clonar o repositório
```bash
git clone <URL_DO_REPOSITORIO>
cd clima-api
```

### 2. Compilar as dependências
```bash
mvn clean install
```

### 3. Iniciar a aplicação
```bash
mvn spring-boot:run
```

A API estará disponível em **`http://localhost:3000`**

### Saída esperada
```
2025-03-15 14:30:00 - Tomcat started on port(s): 3000 (http)
2025-03-15 14:30:00 - Started ClimaApiApplication in X.XXX seconds
```

## 📡 Endpoints Detalhados

### 1️⃣ Health Check
```
GET /api/v1/health
```

**Resposta (HTTP 200):**
```json
{
  "status": "healthy",
  "versao": "1.0.0",
  "timestamp": "2025-03-15T14:30:00Z"
}
```

---

### 2️⃣ Clima por Cidade
```
GET /api/v1/clima/{nome_cidade}
```

**Exemplos de requisição:**
```bash
curl http://localhost:3000/api/v1/clima/Fortaleza
curl http://localhost:3000/api/v1/clima/São%20Paulo
curl http://localhost:3000/api/v1/clima/Rio%20de%20Janeiro
```

**Resposta - Sucesso (HTTP 200):**
```json
{
  "nome": "Fortaleza",
  "estado": "CE",
  "clima": {
    "temperatura_min": 24.5,
    "temperatura_max": 32.0,
    "condicao": "Parcialmente Nublado",
    "unidades": {
      "temperatura": "°C"
    }
  },
  "consultado_em": "2025-03-15T14:30:00Z"
}
```

**Resposta - Erro 404 (Cidade não encontrada):**
```json
{
  "erro": true,
  "codigo": "CIDADE_NAO_ENCONTRADA",
  "mensagem": "Nenhuma cidade encontrada com o nome informado",
  "nome_informado": "CidadeInexistente"
}
```

**Resposta - Erro 400 (Nome inválido):**
```json
{
  "erro": true,
  "codigo": "NOME_INVALIDO",
  "mensagem": "O nome da cidade deve conter pelo menos 2 caracteres",
  "nome_informado": "X"
}
```

---

### 3️⃣ Cidades por Estado
```
GET /api/v1/cidades/{sigla_uf}?limite=10
```

**Exemplos de requisição:**
```bash
curl http://localhost:3000/api/v1/cidades/CE
curl http://localhost:3000/api/v1/cidades/SP?limite=5
curl http://localhost:3000/api/v1/cidades/RJ?limite=20
```

**Resposta - Sucesso (HTTP 200):**
```json
{
  "uf": "CE",
  "quantidade_retornada": 5,
  "cidades": [
    { "nome": "Abaiara" },
    { "nome": "Acarape" },
    { "nome": "Acaraú" },
    { "nome": "Acopiara" },
    { "nome": "Aiuaba" }
  ],
  "consultado_em": "2025-03-15T14:30:00Z"
}
```

**Resposta - Erro 404 (UF não encontrada):**
```json
{
  "erro": true,
  "codigo": "UF_NAO_ENCONTRADA",
  "mensagem": "Estado com a sigla informada não foi encontrado",
  "sigla_uf_informada": "XX"
}
```

**Resposta - Erro 400 (Sigla inválida):**
```json
{
  "erro": true,
  "codigo": "SIGLA_UF_INVALIDA",
  "mensagem": "A sigla do estado deve conter exatamente 2 letras",
  "sigla_uf_informada": "ceara"
}
```

## Endpoints

### Health Check
```
GET /api/v1/health
```

### Clima por cidade
```
GET /api/v1/clima/{nome_cidade}
```
Exemplo: `GET /api/v1/clima/Fortaleza`

### Cidades por estado
```
GET /api/v1/cidades/{sigla_uf}?limite=10
```
Exemplo: `GET /api/v1/cidades/CE?limite=5`

## 🧪 Executar testes

### Rodar todos os testes
```bash
mvn test
```

### Rodar teste específico
```bash
mvn test -Dtest=ClimaEndpointTest
mvn test -Dtest=CidadesEndpointTest
```

### Saída esperada
```
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

## 📮 Testando com Postman

1. **Importar a coleção:**
   - Abrir Postman
   - Clique em "Import" → "Upload Files"
   - Selecione `docs/postman_collection.json`

2. **Configurar variáveis de ambiente:**
   - Base URL: `http://localhost:3000`
   - API Version: `v1`

3. **Executar requisições:**
   - Health Check
   - Buscar clima (exemplo: Fortaleza)
   - Listar cidades (exemplo: CE)

## 🔧 Troubleshooting

### Erro: "Porta 3000 já está em uso"
```bash
# Verificar qual processo está usando a porta
lsof -i :3000  # Mac/Linux
netstat -ano | findstr :3000  # Windows

# Matar o processo ou usar outra porta
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

### Erro: "Connection refused" ao chamar APIs externas
- Verificar conexão com a internet
- APIs externas podem estar indisponíveis
- A API retornará HTTP 503 (Service Unavailable)

### Erro de teste com MockMvc
```bash
# Limpar cache e recompilar
mvn clean
mvn test
```

### API retorna "SERVICO_EXTERNO_INDISPONIVEL"
- Verifique a conexão com a internet
- Tente novamente em alguns momentos
- As APIs externas podem estar com problemas temporários

## 💻 Desenvolvimento Local

### Estrutura de pacotes
```
com.climaapi
├── ClimaApiApplication.java — entrada da aplicação
├── controller/
│   └── ClimaController.java — endpoints REST
├── service/
│   └── ClimaService.java — lógica de negócio e integração
├── dto/
│   ├── ClimaResponse.java — resposta de clima
│   ├── CidadesResponse.java — resposta de cidades
│   └── ErroResponse.java — resposta de erro
└── exception/
    ├── CidadeNaoEncontradaException.java
    ├── ServicoExternoException.java
    └── GlobalExceptionHandler.java — tratamento centralizado
```

### Fluxo de requisição
```
HTTP Request
    ↓
ClimaController (validação)
    ↓
ClimaService (lógica + integração)
    ↓
APIs Externas (CPTEC, IBGE, Open-Meteo)
    ↓
JSON Response / Exception Handler
```

## APIs externas utilizadas

- [Brasil API - CPTEC](https://brasilapi.com.br/docs#tag/CPTEC) — dados meteorológicos
- [Brasil API - IBGE](https://brasilapi.com.br/docs#tag/IBGE) — municípios por estado
- [Open-Meteo](https://open-meteo.com/en/docs) — dados climáticos por coordenadas
- [Open-Meteo Geocoding](https://open-meteo.com/en/docs/geocoding-api) — coordenadas por nome de cidade

## Estrutura do projeto

```
/
├── README.md
├── INTEGRANTES.md
├── pom.xml
├── src/
│   ├── main/java/com/climaapi/
│   │   ├── ClimaApiApplication.java
│   │   ├── controller/ClimaController.java
│   │   ├── service/ClimaService.java
│   │   ├── dto/
│   │   │   ├── ClimaResponse.java
│   │   │   ├── CidadesResponse.java
│   │   │   └── ErroResponse.java
│   │   └── exception/
│   │       ├── CidadeNaoEncontradaException.java
│   │       ├── ServicoExternoException.java
│   │       └── GlobalExceptionHandler.java
│   └── test/java/com/climaapi/
│       ├── ClimaEndpointTest.java
│       └── CidadesEndpointTest.java
└── docs/
    └── postman_collection.json
```
