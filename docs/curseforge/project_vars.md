# CurseForge — Variables del proyecto

> Leído por `../../../codex-docs/scripts/curseforge-upload.ps1`. **Proyecto AÚN NO creado en CurseForge** — crear manualmente en la web (Submit Project → Mods → Minecraft) con el summary y la categoría acordados, copiar aquí el ID asignado antes de subir el primer JAR.

```
project_id =
api_token = ee776b0a-ee95-4850-b554-06be02a8657f
game_versions = 9638, 9639, 11779, 10150
release_type = beta
```

## Proyecto

| Variable | Valor |
|---|---|
| `curseforge_project_id` | *(pendiente de crear)* |
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
| `version` (`gradle.properties`) | `0.0.0-beta.1` |
| `environment` | `Client`, `Server` (requerido en ambos) |

## Rama

```
minecraft/1.21.1/neoforge-21.1.249/production
```

## Tag

Formato: `<mc-version>-<framework>-<version>`. Actual: `1.21.1-neoforge-0.0.0-beta.1` (ya creado y pusheado).

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

## Descripción del proyecto y logo

Sin API para ninguno de los dos. Se pegan a mano desde la web (Edit Project → Description /
Logo) **una vez creado el proyecto**: descripción = HTML de `docs/curseforge/project_description.md`;
logo = imagen generada a partir del prompt acordado (ver conversación de diseño / `docs/LORE.md`
del ecosistema en `majestic` para el tono visual).

## Flujo completo (primera vez)

1. Crear el proyecto en la web CurseForge (Submit Project → Mods) con el summary acordado.
2. Copiar el `project_id` asignado en este archivo.
3. Pegar `docs/curseforge/project_description.md` en Edit Project → Description.
4. Subir el logo generado en Edit Project → Logo.
5. `./gradlew clean build`
6. Crear `docs/curseforge/versions/0.0.0-beta.1.md` (HTML) — release notes de la versión ya taggeada.
7. Subir JAR: `powershell -File ../../../codex-docs/scripts/curseforge-upload.ps1` (desde este repo).
8. Verificar con GET (Core API key) y liberar manualmente desde la web si hace falta.
