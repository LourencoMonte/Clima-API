# Clima API — N703 Técnicas de Integração de Sistemas

API REST que agrega dados climáticos e geográficos de cidades brasileiras,
consumindo APIs públicas externas (Brasil API e Open-Meteo).

## Pré-requisitos

- Java 17+
- Maven 3.8+

## Como rodar

```bash
# Clonar o repositório
git clone <URL_DO_REPOSITORIO>
cd clima-api

# Compilar e subir
mvn spring-boot:run
```

A API estará disponível em `http://localhost:3000`.

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

## Executar testes

```bash
mvn test
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
