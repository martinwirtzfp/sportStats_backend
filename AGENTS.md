# AGENTS.md — sportStats_backend

> Documentación técnica completa para agentes de IA y desarrolladores.  
> Proyecto: TFG DAM — Backend de estadísticas deportivas (fútbol).

---

## Descripción General

API REST desarrollada con **Spring Boot 4 + Java 21** siguiendo **arquitectura hexagonal (Ports & Adapters)**. Proporciona datos de competiciones, equipos, partidos, estadísticas y autenticación JWT. Integra con la API externa **API-Football** para ingestar datos reales.

---

## Stack Tecnológico

| Tecnología          | Versión | Rol                                    |
|---------------------|---------|----------------------------------------|
| Java                | 21      | Lenguaje                               |
| Spring Boot         | 4.0.6   | Framework principal                    |
| Spring Security 7   | 6.x     | Autenticación JWT stateless            |
| Spring Data JPA     | 3.x     | Persistencia + repositorios            |
| H2                  | 2.x     | Base de datos en memoria (dev/json)    |
| MapStruct           | 1.6.3   | Mapeo de objetos (entidades ↔ dominio ↔ DTOs) |
| Lombok              | 1.18.x  | Reducción de boilerplate               |
| jjwt                | 0.12.6  | Generación/validación de JWT           |
| SpringDoc OpenAPI   | 2.x     | Swagger UI (`:8090/swagger-ui.html`)   |
| Maven               | 3.x     | Build                                  |

---

## Arranque

```bash
# Perfil json: H2 en memoria + carga automática de datos fixture
./mvnw spring-boot:run "-Dspring-boot.run.profiles=json"

# Perfil dev (H2 sin datos de fixture)
./mvnw spring-boot:run

# Build + run completo
./mvnw clean package -DskipTests
java -jar target/backend-*.jar --spring.profiles.active=json
```

El servidor arranca en **`http://localhost:8090`**.

**CRÍTICO — DevTools + recompilación**: Si el servidor está corriendo cuando se ejecuta `mvn compile`, Spring DevTools detecta cambios en `.class` y puede cargar clases MapStruct corruptas (`ClassFormatError: Duplicate method`). Siempre detener el servidor antes de recompilar:
```powershell
Stop-Process -Name "java" -Force
Remove-Item -Recurse -Force target
./mvnw spring-boot:run "-Dspring-boot.run.profiles=json"
```

---

## Perfiles Spring

| Perfil | Descripción |
|--------|-------------|
| `dev` (default) | H2 en memoria, `create-drop`, sin fixture loader |
| `json` | H2 en memoria + `JsonDataLoaderRunner` carga datos de `src/main/resources/fixtures/` |
| `local-db` | H2 en fichero (`~/sportstatsdb.mv.db`), `update`, datos persistentes entre reinicios. Usar con ingesta real. |

### Datos cargados en perfil `json`

- **Competición**: La Liga (apiId=140, season=2024)
- **8 equipos**: Real Madrid (541), FC Barcelona (529), Atlético Madrid (530), Sevilla (536), Valencia (532), Villarreal (533), Real Betis (543), Real Sociedad (548)
- **12 partidos**: La Liga 2024/25, todos con estado FINISHED, con goles fulltime + halftime

Ficheros de datos: `src/main/resources/fixtures/laliga_teams.json`, `laliga_fixtures.json`

---

## Configuración

### `application.properties` (base)
```properties
server.port=8090
spring.profiles.active=dev
spring.config.import=optional:file:./application-local.properties

app.jwt.secret=${JWT_SECRET:sportstats-super-secret-key-change-this-in-production-min-256-bits}
app.jwt.expiration-ms=86400000

app.apifootball.base-url=https://v3.football.api-sports.io
app.apifootball.key=${APIFOOTBALL_KEY:YOUR_API_KEY_HERE}
```

### API Key (API-Football)

La API key se guarda en `application-local.properties` (en la raíz del proyecto, **gitignoreado**):
```properties
app.apifootball.key=TU_API_KEY_AQUI
```

El fichero `application-local.properties` está en `.gitignore`. **Nunca committed API keys en código fuente.** El import `optional:file:./application-local.properties` en `application.properties` lo carga automáticamente si existe.

URL del dashboard: https://dashboard.api-football.com/

### H2 Console (solo en dev/json)
URL: `http://localhost:8090/h2-console`  
JDBC URL: `jdbc:h2:mem:sportstatsdb`  
Usuario: `sa` / Contraseña: (vacía)

### Swagger UI
URL: `http://localhost:8090/swagger-ui.html`

---

## Arquitectura Hexagonal

Cada módulo sigue la estructura:

```
{módulo}/v1/
├── application/
│   ├── domain/
│   │   ├── model/          # POJOs de dominio (@Data Lombok)
│   │   └── port/           # Interfaces de puerto (sin implementación)
│   └── service/            # Lógica de negocio (usa solo puertos)
└── infrastructure/
    ├── adapter/
    │   ├── persistence/
    │   │   ├── model/       # Entidades JPA (@Entity)
    │   │   │   └── converter/  # MapStruct: Entity ↔ Domain
    │   │   └── repository/  # Implementación del puerto + JpaRepository
    │   └── rest/
    │       ├── controller/  # @RestController
    │       └── model/       # DTOs de respuesta
    │           └── converter/  # MapStruct: Domain ↔ DTO
```

### Regla clave: dependencias

- **Service** depende solo de **Port** (interfaz)
- **Repository** implementa el Port e inyecta el JpaRepository
- **Controller** inyecta el Service y el DtoConverter
- **Nunca** el dominio/aplicación depende de infraestructura

---

## Módulos

### competition
- Dominio: `Competition` { id, name, apiId, type, season, logoUrl }
- Endpoints: `GET /api/competitions`, `GET /api/competitions/{id}`
- Sin lógica de negocio especial

### team
- Dominio: `Team` { id, name, shortName, logoUrl, apiId, competitionId, **competitionName** }
- `competitionName` existe en el dominio pero **NO en `TeamEntity`** (se obtiene por join)
- **MapStruct CRÍTICO**: `@Mapping(target="competitionName", ignore=true)` debe estar en `toDomain(TeamEntity)`, NO en `toEntity(Team)`
- Endpoints: `GET /api/teams?competitionId=X`, `GET /api/teams/{id}`

### match
- Dominio: `Match` { id, homeTeamId, homeTeamName, homeTeamLogo, awayTeamId, awayTeamName, awayTeamLogo, matchDate, status, homeGoals, awayGoals, htHomeGoals, htAwayGoals, competitionId, competitionName, season, apiId }
- Status values: `FINISHED`, `SCHEDULED`, `LIVE`, etc.
- Endpoints: `GET /api/matches/{id}`, `GET /api/matches/teams/{teamId}?lastN=10`, `GET /api/matches/h2h?team1Id=X&team2Id=Y`
- **JPQL Bug histórico corregido**: `findByBothTeams` tenía precedencia incorrecta de `AND/OR`. Forma correcta:
  ```sql
  WHERE ((m.homeTeamId = :team1Id AND m.awayTeamId = :team2Id)
      OR (m.homeTeamId = :team2Id AND m.awayTeamId = :team1Id))
  AND m.status = 'FINISHED'
  ```

### statistics
Tres servicios independientes:

#### StatisticsService — stats de un equipo
- `getTeamStats(teamId, lastN)`: calcula W/D/L, goles, promedios, desglose casa/fuera
- Percentages en escala 0-100 (ya multiplicados × 100), redondeados a 2 decimales
- Endpoint: `GET /api/statistics/teams/{teamId}?lastN=10`

#### RiskCalculationService — probabilidades de apuestas
- `calculate(homeTeamId, awayTeamId, lastN)`: modelo de probabilidad con decaimiento exponencial (λ=0.1)
- Todos los campos de porcentaje están en escala **0-100** (NO 0-1)
  - `probability1X2.homeWin/draw/awayWin`: porcentajes 1X2
  - `overPercentage`, `underPercentage`: más/menos de 2.5 goles
  - `bttsYesPercentage`, `bttsNoPercentage`: ambos equipos marcan
  - `halfTimeProbability.*`: resultado al descanso
- Endpoint: `GET /api/risk?homeTeamId=X&awayTeamId=Y&lastN=10`

#### HeadToHeadService — historial entre dos equipos
- `getHeadToHead(team1Id, team2Id)`: victorias, empates, promedios de goles, BTTS
- Campos: `team1GoalsAvg`, `team2GoalsAvg`, `avgTotalGoals`, `bttsPercentage` (0-100)
- `recentMatches`: lista de `Match` objetos de dominio
- Endpoint: `GET /api/h2h?team1Id=X&team2Id=Y`

### user + auth
- Registro: `POST /api/auth/register` → devuelve `LoginResponseDto` { token, userId, username, email }
- Login: `POST /api/auth/login` → igual
- Favoritos (requieren JWT):
  - `GET /api/users/me/favorites`
  - `POST /api/users/me/favorites/{teamId}`
  - `DELETE /api/users/me/favorites/{teamId}` → 204

El `UserController` extrae el `userId` del JWT (campo custom `"userId"` en el claim). La extracción tiene validación de null en el header Authorization.

### ingestion
- `POST /api/ingestion/leagues/{leagueApiId}?season=XXXX&competitionId=YY`
- Requiere JWT. Llama a API-Football para traer equipos y partidos.
- Header que usa: `x-apisports-key` (**NO** `x-rapidapi-key`)
- Base URL: `https://v3.football.api-sports.io`
- Evita duplicados por `apiId`

---

## Seguridad (Spring Security + JWT)

### Endpoints públicos (sin JWT)
```
GET  /api/competitions/**
GET  /api/teams/**
GET  /api/matches/**
GET  /api/statistics/**
GET  /api/risk/**
GET  /api/h2h/**
POST /api/auth/register
POST /api/auth/login
GET  /h2-console/**
GET  /swagger-ui/**
GET  /v3/api-docs/**
```

### Endpoints protegidos (requieren JWT)
```
/api/users/**
/api/ingestion/**
```

### JWT
- Algoritmo: HMAC-SHA (key derivada del secreto en config)
- Expiración: 24 horas (86400000 ms)
- Claims: `sub` = email, `userId` = Long
- Header esperado: `Authorization: Bearer <token>`

---

## MapStruct — Reglas Importantes

1. **`@Mapper(componentModel = "spring")`** — obligatorio para inyección Spring
2. Los mappers de entity↔domain van en `infrastructure/adapter/persistence/model/converter/`
3. Los mappers de domain↔dto van en `infrastructure/adapter/rest/model/converter/`
4. Si un campo existe en el destino pero no en la fuente → `@Mapping(target="campo", ignore=true)` en el método correspondiente
5. **Team específico**: `competitionName` NO existe en `TeamEntity`. El `@Mapping(target="competitionName", ignore=true)` va en `toDomain(TeamEntity entity)`.

---

## Manejo de Errores

### GlobalExceptionHandler
- `ApplicationException` → respuesta JSON con `errorCode`, `message`, `timestamp` + HTTP status del enum
- `Exception` genérica → 500 Internal Server Error

### ApplicationError (enum)
```
INVALID_PARAMETERS       400
RESOURCE_NOT_FOUND       404
INTERNAL_ERROR           500
INVALID_CREDENTIALS      401
TOKEN_EXPIRED            401
TOKEN_INVALID            401
EMAIL_ALREADY_EXISTS     409
USER_NOT_FOUND           404
COMPETITION_NOT_FOUND    404
TEAM_NOT_FOUND           404
MATCH_NOT_FOUND          404
FAVORITE_ALREADY_EXISTS  409
APIFOOTBALL_ERROR        502
```

---

## Bugs Históricos Corregidos (no reintroducir)

| Archivo | Bug | Corrección |
|---------|-----|------------|
| `MatchJpaRepository` | `findByBothTeams`: `AND m.status='FINISHED'` solo aplicaba a una rama del OR | Añadidos paréntesis alrededor del OR |
| `TeamEntityConverter` | `@Mapping(target="competitionName", ignore=true)` en método incorrecto | Movido a `toDomain(TeamEntity)` |
| `ApiFootballClient` | Usaba `x-rapidapi-key` en lugar de `x-apisports-key` | Corregido el header |
| `WebConfig` | `ObjectMapper` no registrado como bean → `JsonDataLoaderService` no podía inyectarlo | Añadido `@Bean ObjectMapper` en `WebConfig` |
| `SecurityConfig` | `DaoAuthenticationProvider` usaba setter en lugar de constructor | Cambiado a constructor `new DaoAuthenticationProvider(userDetailsService)` |
| `application-dev.properties` | `spring.jpa.database-platform=org.hibernate.dialect.H2Dialect` (deprecado en Hibernate 6) | Eliminado (auto-detección) |
| `UserController` | `extractUserId` sin null check en Authorization header | Añadida validación |

---

## CORS

Configurado en `WebConfig.java`:
```java
registry.addMapping("/api/**")
    .allowedOriginPatterns("*")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("*")
    .allowCredentials(true);
```

En desarrollo, el frontend en `:5173` usa el proxy Vite (no hay peticiones cross-origin directas). En producción, ajustar `allowedOriginPatterns` con el dominio real.

---

## Decisiones de Diseño

- **Puerto 8090**: el 8080 estaba ocupado en la máquina de desarrollo
- **Perfil json vs dev**: el perfil `json` carga datos sin necesidad de MySQL; útil para demos y desarrollo offline
- **H2 sin dialect explícito**: desde Hibernate 6, la dialect se auto-detecta. No especificar `spring.jpa.database-platform` para evitar warnings de deprecación
- **`spring.jpa.hibernate.ddl-auto=create-drop`** en perfiles de dev/json: la base de datos se crea de cero en cada arranque, garantizando un estado limpio
- **Algoritmo de riesgo con decaimiento exponencial**: los partidos más recientes tienen más peso (λ=0.1). No es un modelo estadístico de producción, es suficiente para TFG
