# Majestic — Diseño del ecosistema

> Documento maestro. Visión cruzada de los 4 módulos, grafo de dependencias, contratos
> entre módulos, convenciones y licencias. Los diseños técnicos por módulo están en
> [`design/`](design/) (se moverán al repo de cada librería en `REPO_SETUP`). El contenido
> concreto está en [`PROGRESSION.md`](PROGRESSION.md), [`CONTENT_MAGIC.md`](CONTENT_MAGIC.md)
> y [`CONTENT_WORLD.md`](CONTENT_WORLD.md).

> **Versión de este documento**: 0.3 (borrador de diseño inicial — 2026-09-10)
> **Estado**: concepto aprobado; repos creados en GitLab, aún sin esqueleto de código.
> Cambios v0.3: cerradas E1 (dimensión = "Plano celeste"), E4 (forja = `RitualType`), E7 (full
> multiplayer), E9 (deps requeridas en CurseForge). E3 resuelta con revisión de `regalia_slots_api`.
> Licencias `regalia_slots_api` (LGPL-3.0) y `common_toolkit` (MIT) confirmadas.

---

## 1. Visión

**Majestic** es un mod de **magia y aventura** para Minecraft 1.21.1 / NeoForge 21.1.249.

Fantasía central: **magia arcano-celestial** — estrellas, constelaciones, luz y éter. El poder
se **obtiene explorando** y se **refina ritualizando**. Tres capas encadenadas:

| Capa | Qué es | Referentes conceptuales (solo inspiración; cero código y cero dependencias) |
|---|---|---|
| **1. Rituales / Investigación** | Altares multibloque + grafo de nodos de constelación que se desbloquean con hallazgos del mundo | Occultism, Eidolon, Forbidden Arcanus |
| **2. Hechizos / Habilidades** | Escuelas, recurso *esencia*, foco/báculo, árbol de hechizos que los rituales "graban" | Iron's Spellbooks, Ars Nouveau |
| **3. Artefactos / Reliquias** | Objetos equipables (vía `regalia_slots_api`) con pasivos/activos y sinergias; se forjan en rituales de alto nivel | Reliquary, Relics |

El eje **aventura** hace obligatoria la exploración: cada capa necesita una llave que solo
aparece en una región o dimensión nueva. Acto final en un **plano celeste** (dimensión propia).

---

## 2. Módulos

| Repo GitLab | Rol | Mod ID | Paquete Java | Display name | Licencia |
|---|---|---|---|---|---|
| `stalking-dragons/minecraft/astral-core` | **LIB magia**: esencia, casting, motor de rituales, investigación, reliquias | `astral_core` | `com.skd.astralcore` | Astral Core | **MIT** |
| `stalking-dragons/minecraft/expedition-core` | **LIB aventura**: dimensiones, estructuras, framework de jefes, inyección de loot | `expedition_core` | `com.skd.expeditioncore` | Expedition Core | **MIT** |
| `stalking-dragons/minecraft/almanac-core` | **LIB contenido-como-datos**: códecs datapack, tablas de contenido, plugin JEI, puente de guía | `almanac_core` | `com.skd.almanaccore` | Almanac Core | **MIT** |
| `stalking-dragons/minecraft/majestic` | **MOD principal**: todo el contenido celestial concreto (dimensión, jefes, estructuras, hechizos, rituales, reliquias, questline, guía) | `majestic` | `com.skd.majestic` | Majestic | **All Rights Reserved** |

- **Versión Minecraft**: `1.21.1` — **NeoForge**: `21.1.249`. No subir sin petición explícita.
- **Rama de trabajo** (todos): `minecraft/1.21.1/neoforge-21.1.249/production`.
  - Librerías (`astral_core`, `expedition_core`, `almanac_core`): espejo público `.../main` (CI) como el resto de mods SKD.
  - **`majestic`**: **solo `production`, repo privado, sin espejo `main` público.** CI limitado a build/test.
- **Ruta local** (patrón de la casa): `<mod_id>/neoforge/1.21.1/` — p. ej. `astral_core/neoforge/1.21.1/`.
- **Namespace de recursos**: cada módulo usa su `mod_id` como namespace (`astral_core:`, `majestic:`…).

---

## 3. Grafo de dependencias

```
        common_toolkit          regalia_slots_api          GeckoLib          JEI
        (config, red,            (ranuras equipables)      (modelos/anim.)   (recetas)
         registro dinámico)              │                      │              │
              │   │                      │                      │              │
              │   └──────────────────────┤                      │              │
              ▼                          ▼                      │              │
      ┌───────────────┐          (usado por astral_core)         │              │
      │  astral_core  │                                          │              │
      │   LIB magia   │                                          │              │
      └───────┬───────┘                                          │              │
              │                                                  │              │
              ▼                                                  │              │
      ┌───────────────┐                                  (soft) ◄┼──────────────┘
      │  almanac_core │──────────────────────────────────────────┘
      │ LIB contenido │   deps: astral_core (real), JEI (soft),
      │  (datos+JEI+  │         vellumli (soft, SOLO datos)
      │   guía)       │
      └───────┬───────┘
              │            ┌──────────────────┐
              │            │  expedition_core │  deps: common_toolkit,
              │            │   LIB aventura   │        GeckoLib (real)
              │            └────────┬─────────┘
              └────────────┬────────┘
                           ▼
                  ┌──────────────────┐
                  │     majestic     │  deps: astral_core, expedition_core,
                  │   MOD principal  │        almanac_core, GeckoLib
                  └──────────────────┘        (+ jei/vellumli soft vía almanac_core)
```

### Dependencias por módulo

| Módulo | Reales (hard) | Suaves (`ModList.isLoaded`) |
|---|---|---|
| `astral_core` | `common_toolkit`, `regalia_slots_api` | — |
| `expedition_core` | `common_toolkit`, `geckolib` | — |
| `almanac_core` | `astral_core` | `jei`, `vellumli` |
| `majestic` | `astral_core`, `expedition_core`, `almanac_core`, `geckolib` | `jei` (vía almanac), `vellumli` (vía almanac) |

---

## 4. Regla de autonomía

**Majestic y sus librerías son autónomos.** No conviven con ni asumen ningún modpack (en
particular **no** Mystical Realms).

**Dependencias permitidas — lista cerrada:**
- Nuestras: `common_toolkit`, `regalia_slots_api`, `vellumli`.
- Excepciones externas aceptadas (estándar de facto, no aportan contenido de gameplay ajeno):
  **GeckoLib** (real, jefes animados) y **JEI** (soft, visualización de recetas).

**Prohibido** diseñar contenido, balance, recetas o progresión que dependan de —o asuman la
presencia de— mods de terceros de contenido. Lista negra explícita (no integrar, no gate, no
compat): Iron's Spellbooks, Apotheosis / Apothic *, Occultism, Reliquary, equivalent_legacy,
ascendant_enchanting, ascendant_attributes, Patchouli upstream (usar `vellumli`).

Cualquier integración futura fuera de la lista cerrada requiere aprobación explícita del
propietario y se documenta en [`INTEGRATIONS.md`](INTEGRATIONS.md).

---

## 5. Contratos entre módulos

Qué **expone** cada librería y qué **consume** `majestic`. Detalle de firmas en cada `design/DESIGN_*.md`.

### `astral_core` expone
- **Esencia**: capability/attachment por jugador; API de lectura/consumo/regen; hooks de modificadores.
- **Casting**: registro de hechizos/habilidades (`SpellType`), contexto de lanzamiento, resolución de objetivo, cooldowns, efectos, hooks cliente (partículas/render).
- **Rituales**: contrato de altar multibloque, `RitualType`, fases, resultado, riesgo/fallo.
- **Investigación**: grafo de nodos, requisitos, persistencia por jugador, eventos de desbloqueo.
- **Reliquias**: contrato de comportamiento (pasivo/activo), integración con `regalia_slots_api` (qué slot, condiciones), API de sinergias.
- **Registro/red**: helpers sobre `common_toolkit` para registrar contenido de datos y sincronizar estado.

### `expedition_core` expone
- **Dimensiones**: helpers de bootstrap (registro, viaje, retorno); gancho opcional para `tower_waystone`/`teleport_animation` a futuro (no dep).
- **Estructuras**: utilidades jigsaw, colocación controlada, spawn de piezas.
- **Jefes**: `BossEncounter` (barra, fases, bloqueo de arena, tabla de botín, música), integración GeckoLib para el render.
- **Loot**: inyección en tablas vanilla y advancements.

### `almanac_core` expone
- **Códecs + loaders datapack** para: `spell`, `ritual`, `research_node`, `relic` (esquemas en [`design/DESIGN_ALMANAC_CORE.md`](design/DESIGN_ALMANAC_CORE.md)).
- **Recetas**: `RecipeType` propio para **rituales** (envoltorio de `RitualDefinition` para JEI/consulta). La **forja de reliquias** es un `RitualType` de `astral_core` (E4), no un `RecipeType`.
- **JEI**: plugin base con categorías (ritual — incluida la forja como ritual T3, investigación) — soft.
- **Guía**: puente hacia `vellumli` (registro de libro, generación de entradas desde datos) — soft, **solo datos**, sin código derivado de vellumli.

### `majestic` consume
Todo lo anterior. No expone API pública para terceros (mod de contenido, no librería).

---

## 6. Convenciones

| Convención | Uso | Ejemplo |
|---|---|---|
| **snake_case** | `mod_id`, `assets/`, `data/`, paquetes Java | `astral_core` |
| **PascalCase** | Clases Java principales | `AstralCore` |
| **camelCase** | Variables, métodos, claves de config | `essenceCapacity` |
| **Title Case** | Display name (README, CHANGELOG, CurseForge) | `Astral Core` |

- Clase principal = PascalCase del `mod_id` (`astral_core` → `AstralCore`, `majestic` → `Majestic`).
- Paquete base = equivalente sin guiones (`com.skd.astralcore`, `com.skd.majestic`).
- Config: `config/<mod_id>/config.toml` (`ModConfigSpec`, tipo SERVER; CLIENT aparte si hace falta).
- Un `DESIGN_*_1-21-1.md` + `WORKFLOW_*_1-21-1.md` por repo (el WORKFLOW se genera en `REPO_SETUP`).

---

## 7. Licencias

| Componente | Licencia | Implicación |
|---|---|---|
| **`majestic`** | **All Rights Reserved** | Sin redistribución de binario ni fuente fuera de los canales del propietario. `mod_license` = `All Rights Reserved`; CurseForge en modo "All Rights Reserved". **No** empaquetar (shade/jar-in-jar) ninguna dependencia: todas se instalan aparte por el usuario. Repo privado. |
| `astral_core`, `expedition_core`, `almanac_core` | **MIT** | Librerías abiertas y reutilizables por otros mods SKD. Sin código derivado de mods con licencia no permisiva. |
| `common_toolkit` | **MIT** (fork de Placebo, MIT) | Dependencia nuestra. Sin bundling. |
| `regalia_slots_api` | **LGPL-3.0** (reimplementación de Curios, LGPL-3.0) | Dependencia nuestra real de `astral_core`. LGPL permite el enlace desde una obra propietaria (`majestic` ARR) **siempre que**: (1) la librería se instale como jar aparte, no bundleada; (2) no se modifique su código dentro de nuestros repos; (3) su fuente siga disponible (lo está, es repo SKD). `astral_core` (MIT) puede depender de LGPL sin problema. |
| **`vellumli`** | **CC BY-NC-SA 3.0** (NonCommercial + ShareAlike) | ⚠️ Integración de `almanac_core` **soft y solo por datos**: llamar a su API pública y enviar el libro como JSON formato Patchouli/vellumli. **Nada de código copiado ni derivado.** ShareAlike solo aplica a obras derivadas del propio `vellumli`, no a consumidores. NonCommercial afecta a `vellumli`, no a un mod independiente que lo requiera como dependencia instalada aparte (nunca bundleado). |
| **GeckoLib** | (verificar versión 1.21.1) | Dependencia real; solo se enlaza su API en runtime. GeckoLib permite su uso como dependencia en mods de código cerrado. No redistribuir ni modificar. |
| **JEI** | MIT | Soft. |

Acción pendiente antes de `REPO_SETUP`: fijar la versión exacta de **GeckoLib** para MC 1.21.1 /
NeoForge 21.1.249 y sus coordenadas Maven; ídem **JEI** y **vellumli** (artefacto 1.21.1).

---

## 8. Roadmap de creación y publicación

**4 mods independientes** en carpeta local y en CurseForge: `astral_core`, `expedition_core`,
`almanac_core` (librerías) y `majestic` (principal). Cada uno con su proyecto CF propio, su ciclo
de versión, su `docs/curseforge/` y su changelog. Nada de mono-proyecto.

### Fase 0 — Diseño *(hecho)*
Concepto, arquitectura, nombres, dependencias, licencias — este documento y los de `design/`.

### Fase 1 — Cierre de decisiones
Recorrer §9 (E2/E6/E8 + A2, M5, J1–J6) uno por uno con el propietario.

### Fase 2 — Setup de repos
1. Versiones fijadas (1.21.1): NeoForge `21.1.249`, MDK `net.neoforged.moddev 2.0.142`,
   Parchment `2024.11.17`. **JEI** `mezz.jei:jei-1.21.1-neoforge:19.21.0.247`
   (maven `https://maven.blamejared.com`). **GeckoLib**
   `software.bernie.geckolib:geckolib-neoforge-1.21.1:4.7.6` (CF proyecto 388172) — re-verificar
   la última 1.21.1 antes de cablearla en `expedition_core`. **vellumli**: mod SKD propio
   (`com.skd.vellumli`, CF 1638492); en dev `compileOnly files("libs/vellumli-*.jar")`, runtime
   `optional`.
2. `REPO_SETUP.md` — **los 4 hechos (2026-09-11)**. Esqueleto MDK (moddev 2.0.142) copiado de
   `tick_smoothing`: `build.gradle`, `gradle.properties`, `settings.gradle`, `gradlew`+wrapper,
   `templates/META-INF/neoforge.mods.toml`, `pack.mcmeta`, `@Mod` vacío, `LICENSE`, `README.md`,
   `CHANGELOG.md`, `.gitignore`, `.claude/CLAUDE.md`, `docs/WORKFLOW_*` + `DESIGN_*` movido.
3. Estructura local definitiva `<mod_id>/neoforge/1.21.1/`. La carpeta `majestic/docs/design/` se eliminó.

**Estado de los 4 repos (2026-09-11) — todos `BUILD SUCCESSFUL`, `mod_version=0.0.0`:**

| Repo | Ramas | Commit | Licencia | Deps cableadas en el esqueleto | JAR |
|---|---|---|---|---|---|
| `astral-core` | `production` + `main` | `001cc2e` | MIT | — (comentadas) | `astral_core-1.21.1-neoforge-21.1.249-0.0.0.jar` |
| `expedition-core` | `production` + `main` | `3df8563` | MIT | **GeckoLib 4.7.6** (real, resuelve) | `expedition_core-…-0.0.0.jar` |
| `almanac-core` | `production` + `main` | `075ef87` | MIT | — (repos maven blamejared/central declarados) | `almanac_core-…-0.0.0.jar` |
| `majestic` | `production` (privado, sin `main`, sin CI) | `bd208c6` | **All Rights Reserved** | — (comentadas) | `majestic-…-0.0.0.jar` |

**Pendiente del operador en GitLab UI** (por repo de librería): default branch → `production`,
proteger `minecraft/*/neoforge-*/main`, configurar mirror a GitHub. Para `majestic`: solo confirmar
que es privado. **CF: los 4 proyectos aún sin crear** (Fase 3, cuando cada mod tenga hito funcional).

### Fase 3 — Creación de proyectos CurseForge *(pronto, no esperar a v1)*
- Crear los **4 proyectos CF** en cuanto cada mod tenga un primer build que arranque:
  - Orden recomendado: `astral_core` primero (los demás lo declaran como dep), luego
    `expedition_core` y `almanac_core`, luego `majestic`.
  - Cada proyecto: `docs/curseforge/project_description.md`, `project_vars.md`, logo, categorías,
    `mod_license` correcto (MIT ×3, All Rights Reserved para `majestic`).
  - Declarar relaciones de dependencia en CF: `almanac_core` → requiere `astral_core`;
    `majestic` → requiere `astral_core`, `expedition_core`, `almanac_core`, `geckolib`; `jei` y
    `vellumli` como *optional*.
- Formato de descripción/჻changelog: `codex-docs/reference/CURSEFORGE.md` (no improvisar).

### Fase 4 — Implementación y releases por capas
Orden de trabajo en `PROGRESSION.md §5`. El código se delega en OpenCode; diseño, docs, git,
Graphify y publicación los lleva Claude. Publicación **incremental**:

| Hito | Sube a CF | Tipo |
|---|---|---|
| `astral_core` compila + esencia/casting mínimos | `astral_core 0.1.0-alpha` | alpha |
| `majestic` con 1 escuela + 3–4 hechizos + HUD (jugable mínimo) | `majestic 0.1.0-alpha` + deps alpha | alpha |
| Rituales + investigación (`astral_core`/`almanac_core`) | bump libs a `0.2.x` | alpha/beta |
| Estructuras + Jefe I (`expedition_core` + `majestic`) | bump afectados | beta |
| Dimensión + Actos III–IV + reliquias + jefes finales | bump afectados | beta |
| Contenido v1 completo (`PROGRESSION.md §6`) + pulido | los 4 a `1.0.0` | release |

Cada release de `majestic` fija las versiones mínimas de las 3 librerías que necesita (rango en
`neoforge.mods.toml`) y lo refleja en las *required dependencies* de CF.

---

## 9. Decisiones y preguntas

### Cerradas (2026-09-10)

| # | Tema | Decisión |
|---|---|---|
| **E1** | Dimensión | Se llama **"Plano celeste"** / EN "Celestial Plane". id: `majestic:celestial_plane`. Ver `CONTENT_WORLD.md`. |
| **E3** | Reliquias / slots | `regalia_slots_api` ya trae los slots Curios estándar. Majestic **reutiliza `charm`** para reliquias generales y **registra 1 slot propio `majestic:astral`** para la(s) reliquia(s) capstone. Slots por JSON en `data/majestic/curios/slots/`. Ítems = `ICurioItem` asignados por tag `#regalia_slots_api:<slot>` + `#curios:<slot>`. Ver `astral_core/neoforge/1.21.1/docs/DESIGN_ASTRAL_CORE_1-21-1.md §Reliquias`. |
| **E4** | Forja de reliquias | Es un **`RitualType`** de `astral_core` (ritual de alto nivel en altar T3), **no** un `RecipeType`. `almanac_core` no lleva `RelicForgeRecipe`. |
| **E5** | Alcance v1 | Baseline aceptado — ver tabla en `PROGRESSION.md §6`. |
| **E7** | Multiplayer | **Full multiplayer siempre.** Todo el diseño asume servidor: estado por jugador sincronizado, altares y dimensión compartidos, escalado de jefes por nº de jugadores en arena, sin supuestos singleplayer. |
| **E9** | Distribución | `majestic` ARR sin bundling. `astral_core`, `expedition_core`, `almanac_core` y GeckoLib se declaran como **required dependencies** en la página de CurseForge y en `neoforge.mods.toml`. `jei` / `vellumli` = optional. |
| **E2** | Esencia + HUD | Esencia **única global**. HUD propio = **widget en esquina** (ancla/escala config), visible **solo al empuñar el foco**. Elementos: barra+valor, regen, hechizo grabado, cooldown, iconos contextuales (altar/bioma/eclipse/sobrecarga). Reliquias en HUD → v2. Detalle en `CONTENT_MAGIC.md §1`. |
| **E6** | Guía y tomos | Libro-guía principal (crafteable + entregado, revelado progresivo) + **3 tomos de avance** por misión. Misiones aceptadas; **todas las recompensas son opcionales** (ningún tomo bloquea la ruta crítica ni el 100%). Detalle en `PROGRESSION.md §7`. |
| **E8** | Datagen | **Todo el contenido de `majestic` por datagen** (JSON de `almanac_core` + recetas/loot/tags/advancements/models/lang `en_us`). JSON a mano solo para lo que un `Codec` no exprese. `es_es` a mano (contenido propio). |
| **B2** | astral_core | `Multiblock` de altar: **fuente primaria en código**; datapack puede sobrescribir la forma. |
| **B3** | almanac_core | Contenido de datapack en **`Registry` datapack propio de `almanac_core` + sync S→C**. No se inyecta en `DeferredRegister`. Permite packs de terceros y recarga en caliente. |
| **A5/J2** | majestic | **Foco único** con hechizo grabado intercambiable vía ritual. Ranuras múltiples de hechizo = posible v1.x. |
| **J1/B5** | majestic | v1.0: árbol de investigación **solo visible en la guía** (`vellumli`). Pantalla propia = v1.x. |

| **Setup-1** | Ramas `majestic` | `majestic`: **solo `production`, repo privado, sin espejo `main`**. Librerías: flujo normal con `main` público. |
| **Setup-2** | CF `majestic` | Proyecto CurseForge **público, descarga libre**, `mod_license = All Rights Reserved`. |
| **Setup-3** | Repos GitLab | Existen los 4: `astral-core`, `expedition-core`, `almanac-core`, `majestic`. Se asumen recién creados; **verificar su estado real (`git ls-remote`) al inicio de `REPO_SETUP`** y avisar antes de tocar nada. |
| **A4** | Escuelas v1 | Luz Estelar / *Starlight* · Éter / *Ether* · Gravedad / *Gravity*. |
| **A6** | Releases | Por capas: `0.x-alpha` (1 escuela jugable) → beta → `1.0.0` los 4 mods a la vez. |
| **C1–C3** | Proceso | `WORKFLOW_*` se generan en `REPO_SETUP`; orden `astral_core`→`expedition_core`→`almanac_core`→`majestic`; Claude orquesta setup/docs/git/CF, OpenCode implementa features. |
| **J3/C5** | Creative tabs | Tres: magia / mundo / reliquias. |
| **J4/C6** | `es_es` | A mano durante el desarrollo; otros idiomas después con el flujo habitual. |

### Abiertas

| # | Tema | Pregunta |
|---|---|---|
| M5 | setup | Fijar versiones/coordenadas Maven de GeckoLib / JEI / vellumli para 1.21.1 (acción de Claude antes de `REPO_SETUP`). |
