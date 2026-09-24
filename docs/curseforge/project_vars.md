# CurseForge — Variables del proyecto

> Leído por `../../../codex-docs/scripts/curseforge-upload.ps1`. Proyecto creado en CurseForge (ID `1692772`).

```
project_id = 1692772
api_token = ee776b0a-ee95-4850-b554-06be02a8657f
game_versions = 9638, 9639, 11779, 10150
release_type = beta
relations = astral-core:requiredDependency,almanac-core:requiredDependency,expedition-core:requiredDependency,geckolib:requiredDependency,vellumli:requiredDependency
```

## Proyecto

| Variable | Valor |
|---|---|
| `curseforge_project_id` | `1692772` |
| `mod_id` | `majestic` |
| `display_name` | `Majestic` |

## Tokens

| API | Token | Uso |
|-----|-------|-----|
| Upload | `ee776b0a-ee95-4850-b554-06be02a8657f` | Subir archivos JAR (token de cuenta, compartido con todos los mods) |
| Core (GET) | `$2a$10$yGwryAfmRkS9ZJsJUDf5YOKZpOIsmHB8Fji2D8JVCKBSZEKYlwmaO` | Consultar datos del mod |

Autenticación Upload: cabecera `X-Api-Token`. Core: cabecera `x-api-key`.

## Versión actual

| Variable | Valor |
|----------|-------|
| `minecraft_version` | `1.21.1` |
| `neoforge_version` (loader) | `21.1.249` |
| `framework` | `neoforge` |
| `java_version` | `21` |
| `version` (`gradle.properties`) | `0.0.0-beta.12` |
| `environment` | `Client`, `Server` (requerido en ambos) |

## Rama

```
minecraft/1.21.1/neoforge-21.1.249/production
```

## Tag

Formato: `<mc-version>-<framework>-<version>`. Actual: `1.21.1-neoforge-beta.12`.

### IDs de `gameVersions` para 1.21.1 (verificados, mismos que el resto de mods 1.21.1 del workspace)

| Nombre | ID | gameVersionTypeID |
|--------|-----|--------|
| `Client` | `9638` | 75208 |
| `Server` | `9639` | 75208 |
| `1.21.1` | `11779` | 77784 |
| `NeoForge` | `10150` | 68441 |

## Entorno "Client & Server"

`game_versions` incluye `9638` (Client) y `9639` (Server) → CurseForge marca "Client & Server"
automáticamente, sin paso manual.

## Relaciones (dependencias declaradas en CurseForge)

Majestic requiere en tiempo de ejecución los 3 cores del ecosistema + GeckoLib (los 4 `required`
en `neoforge.mods.toml`). Declarado arriba (`relations = ...`) para que cada subida marque estas
dependencias — el launcher de CurseForge las instala automáticamente al instalar `majestic`.

**`expedition-core`**: aprobado (`isAvailable: true` confirmado 2026-09-24) y añadido a `relations`
arriba. Las 4 dependencias (`astral-core`, `almanac-core`, `expedition-core`, `geckolib`) quedan
declaradas desde la subida de beta.7.

## Descripción del proyecto y logo

Sin API para ninguno de los dos. Se pegan a mano desde la web (Edit Project → Description /
Logo) **una vez creado el proyecto**: descripción = HTML de `docs/curseforge/project_description.md`;
logo = imagen generada a partir del prompt acordado (ver conversación de diseño / `docs/LORE.md`
del ecosistema en `majestic` para el tono visual).

## Flujo completo (primera vez)

1. **[hecho]** Proyecto creado en la web CurseForge (`1692772`).
2. **[hecho]** `project_id` copiado en este archivo y en `gradle.properties`.
3. Pegar `docs/curseforge/project_description.md` en Edit Project → Description — **pendiente del usuario** (sin API).
4. Subir el logo (`ChatGPT Image 12 sept 2026, 16_48_46.png`) en Edit Project → Logo — **pendiente del usuario** (sin API).
5. `./gradlew clean build` — **[hecho]**, jar `majestic-1.21.1-neoforge-21.1.249-0.0.0-beta.8.jar`.
6. `docs/curseforge/versions/0.0.0-beta.8.md` (HTML) — release notes de la versión ya taggeada.
7. Subir JAR: `powershell -File ../../../codex-docs/scripts/curseforge-upload.ps1` (desde este repo) — **[hecho]**.
8. Verificar con GET (Core API key) y liberar manualmente desde la web si hace falta.
