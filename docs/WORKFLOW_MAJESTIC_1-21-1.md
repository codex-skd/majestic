# Flujo de trabajo — Majestic (NeoForge)

> **Versión del workflow**: 1.18.0 (codex-docs)
> Este archivo pertenece al proyecto **Majestic** (mod principal del ecosistema). Cambios aquí solo afectan a este proyecto.
> **Trabaja directamente con este archivo**: es el workflow operativo del mod, autocontenido. No leas `codex-docs/WORKFLOW_AGENT.md` ni `WORKFLOW_GENERIC.md` de forma rutinaria.
> On-demand (solo si la tarea lo necesita): `codex-docs/reference/CURSEFORGE.md` (formato HTML al publicar), `codex-docs/reference/GRAPHIFY.md` (backend LLM de Graphify), `codex-docs/reference/REPO_SETUP.md` (setup único de repo).
> Diseño: [`DESIGN_ECOSYSTEM.md`](DESIGN_ECOSYSTEM.md) (visión cruzada + decisiones §9), [`DESIGN_MAJESTIC_1-21-1.md`](DESIGN_MAJESTIC_1-21-1.md), [`PROGRESSION.md`](PROGRESSION.md), [`CONTENT_MAGIC.md`](CONTENT_MAGIC.md), [`CONTENT_WORLD.md`](CONTENT_WORLD.md), [`INTEGRATIONS.md`](INTEGRATIONS.md), [`LORE.md`](LORE.md).

## Específico del mod

| Campo | Valor |
|---|---|
| **Mod ID** | `majestic` |
| **Clase principal** | `com.skd.majestic.Majestic` |
| **Display name** | `Majestic` |
| **Versión Minecraft** | `1.21.1` |
| **Versión NeoForge** | `21.1.249` |
| **Rama de trabajo** | `minecraft/1.21.1/neoforge-21.1.249/production` (única) |
| **Repositorio GitLab** | `https://gitlab.com/stalking-dragons/minecraft/majestic.git` — **privado** |
| **Paquete base** | `com.skd.majestic` |
| **Licencia** | **All Rights Reserved** |
| **CurseForge project id** | *(pendiente de registrar — `mod_curseforge_project_id` en `gradle.properties`)* |

## Qué es este mod

**Mod de contenido** de magia y aventura arcano-celestial. Construye sobre las 3 librerías del
ecosistema y aporta **todo lo concreto**:

- **Dimensión** `majestic:celestial_plane` ("Plano celeste") + biomas + estructuras (jigsaw).
- **Jefes** (3 en v1) con GeckoLib, sobre el framework `BossEncounter` de `expedition_core`.
- **Magia**: 3 escuelas (Luz Estelar / Éter / Gravedad), hechizos, rituales, nodos de
  investigación, reliquias — **definidos como datos** (JSON de `almanac_core`) generados por datagen.
- **Foco único** con hechizo grabado intercambiable; **esencia única global**; HUD widget en
  esquina visible solo al empuñar el foco.
- **Questline** (cadena de advancements) + libro-guía `vellumli` (revelado progresivo) + 3 tomos de avance.
- Config, i18n (`en_us` generado, `es_es` a mano), assets.

Dependencias (todas **externas, nunca jar-in-jar**): `astral_core`, `expedition_core`,
`almanac_core`, `geckolib`, `vellumli` = required; `jei` = optional. En desarrollo las 3 librerías
SKD se consumen como `compileOnly files("libs/<jar>")`.

## Convenciones de nomenclatura

| Convención | Uso | Ejemplo |
|---|---|---|
| **snake_case** | `mod_id` en gradle.properties, assets/, data/, packages Java | `majestic` |
| **PascalCase** | Clases Java principales | `Majestic` |
| **camelCase** | Variables, métodos, config keys | `essenceHudScale` |
| **Title Case** | Display name en README, CHANGELOG, docs, CurseForge | `Majestic` |

- `mod_id` en `gradle.properties` coincide con el nombre del directorio del proyecto (`majestic`).
- Clase principal en **PascalCase** del `mod_id`: `majestic` → `Majestic`.
- Paquete base `com.skd.majestic` (sin guion bajo, como el resto de mods SKD).
- Namespace de recursos: `majestic:` (assets y data).

## Organización y ramas

| Rama | Propósito |
|---|---|
| `minecraft/1.21.1/neoforge-21.1.249/production` | **Rama única.** Repo privado, todo el trabajo aquí. |

- **No hay rama `main` ni espejo público** (a diferencia de las 3 librerías, que sí lo tienen).
- **No hay `.gitlab-ci.yml` de mirror.** Se puede añadir un CI de build/test más adelante; no publica nada.
- Localmente `majestic/neoforge/1.21.1/` es un clon independiente con su propio `.git/`, en `production`.

## Estructura del proyecto

```
majestic/neoforge/1.21.1/                    # Raíz real del proyecto
├── build.gradle                            # net.neoforged.moddev 2.0.142 + Parchment (sin maven-publish)
├── gradle.properties
├── settings.gradle
├── src/main/java/com/skd/majestic/
│   ├── Majestic.java                       # @Mod: bootstrap de contenido (por hito)
│   ├── registry/                           # DeferredRegister: ITEMS, BLOCKS, ENTITIES, SOUNDS, TABS, ...
│   ├── content/{item,block,entity}/        # entity/boss/ usa GeoBossEntity/GeoBossRenderer de expedition_core
│   ├── magic/{schools,effect,ritual,relic}/  # solo lo que un Codec no cubre; el resto es datos
│   ├── world/{dimension,biome,structure,feature,portal}/
│   ├── progression/{triggers,advancement,boss}/
│   ├── datagen/                            # TODO el contenido: recipes, loot, tags, advancements, models, lang, JSON de almanac
│   ├── client/{hud,render,screen,particle}/
│   └── compat/{jei,vellumli}/              # soft, gated ModList.isLoaded
├── src/main/resources/
│   ├── assets/majestic/{icon.png, lang/{en_us.json,es_es.json}}
│   ├── data/majestic/curios/slots/astral.json   # slot propio de reliquia capstone
│   ├── pack.mcmeta
│   └── templates/META-INF/neoforge.mods.toml   # Template con placeholders ${...}
├── libs/                                   # JARs locales de astral_core / expedition_core / almanac_core / vellumli. Versionado.
├── lib_ext/ · temp/                        # NO versionado
├── docs/                                   # DESIGN_ECOSYSTEM, DESIGN_MAJESTIC, PROGRESSION, CONTENT_*, INTEGRATIONS, LORE, WORKFLOW, curseforge/
├── CHANGELOG.md · README.md · LICENSE      # LICENSE = All Rights Reserved (propio)
└── graphify-out/
```

## Versionado

| Estado | Formato | Ejemplos |
|---|---|---|
| Beta / desarrollo | `0.0.0-beta.X` | `0.0.0-beta.1` |
| Alpha jugable | `0.X.0-alpha` | `0.1.0-alpha` |
| Release estable | `X.Y.Z` (SemVer) | `1.0.0` |

- Incrementar en cada commit funcional y al preparar subida a CurseForge; se define en `gradle.properties` (`mod_version`).
- JAR: `majestic-1.21.1-neoforge-21.1.249-<mod_version>.jar` (`base.archivesName`).

## Commits (Conventional Commits)

`<tipo>[<ámbito>]: <descripción>` + body con `v<version>`. Tipos: `feat` · `fix` · `refactor` · `docs` · `chore` · `style` · `perf` · `test`.

```
git commit -m "feat[magic]: Starlight school — 4 spells via datagen

v0.1.0-alpha"
```

Cerrar los mensajes de commit con:
`Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>`

## Sin tags

**No se crean tags git** (ni en GitLab ni en ningún remoto): apuntan a commits de `production` y el mirror los publicaría con el contenido privado. El commit exacto de cada JAR es su `chore: bump version to <version>`.

## Flujo por tarea

**0. Alcance** — Este mod solo tiene una versión (1.21.1) y un framework (NeoForge). No requiere selector.

**1. Desarrollo**

```bash
git checkout minecraft/1.21.1/neoforge-21.1.249/production
# refrescar libs/ con los jars construidos de astral_core/expedition_core/almanac_core si cambiaron
./gradlew.bat runData    # regenerar el contenido datagen
./gradlew.bat build
git add -A
git commit -m "feat[world]: Celestial Plane dimension + 3 biomes

v0.1.0-alpha"
git push
```

**2. Preparar versión para CurseForge** — solo si el usuario confirma:

```bash
# bump en gradle.properties: mod_version=0.1.0-alpha
./gradlew.bat clean runData build
# release notes: docs/curseforge/versions/0.1.0-alpha.md + CHANGELOG.md
git commit -m "chore: bump version to 0.1.0-alpha"
# Subir JAR solo si el usuario confirma:
# powershell -File ../../../codex-docs/scripts/curseforge-upload.ps1
```

CurseForge: proyecto **público, descarga libre**, `mod_license = All Rights Reserved`. Declarar
`astral_core` / `expedition_core` / `almanac_core` / `geckolib` como **required dependencies** y
`vellumli` también como **required** (desde beta.12) y `jei` como **optional**.

**3. Release estable** — `mod_version=1.0.0` + commit `chore: bump version to X.Y.Z` (sin tag).
Los 4 mods del ecosistema (`astral_core`, `expedition_core`, `almanac_core`, `majestic`) suben a
`1.0.0` **a la vez** (ver roadmap `DESIGN_ECOSYSTEM.md §8 Fase 4`).

**4. Actualizar Knowledge Graph (Graphify)** — tras cada push a remoto:

```bash
GRAPHIFY="C:\Users\llagu\AppData\Local\Packages\PythonSoftwareFoundation.Python.3.13_qbz5n2kfra8p0\LocalCache\local-packages\Python313\Scripts\graphify.exe"
"$GRAPHIFY" extract .              # 1ª vez (si graphify-out/ NO existe)
"$GRAPHIFY" update . --force       # actualización tras cambios de código
git add graphify-out/ && git commit -m "chore: update knowledge graph" && git push
```

Leer solo `GRAPH_REPORT.md`, nunca `graph.json`/`graph.html`. Sin copias fechadas.

## Buenas prácticas

- **Un commit por cambio lógico** · commit+push tras cada cambio funcional y de documentación.
- **`clean build` siempre antes del JAR final**. `runData` antes de `build` si cambió el contenido.
- **Versionar antes de subir a CurseForge** · **CHANGELOG.md siempre actualizado**.
- **Nunca actualizar** NeoForge, MC ni dependencias sin petición explícita.
- **Nunca borrar archivos** sin petición explícita.
- **Licencia ARR**: repo privado, sin espejo `main`, sin bundling de dependencias. No relicenciar.
- **Todo el contenido por datagen** (E8): hechizos/rituales/nodos/reliquias JSON de `almanac_core`
  + recetas/loot/tags/advancements/models/`en_us`. A mano solo lo que un `Codec` no exprese.
- **`es_es` a mano** (contenido propio); otros idiomas después con el flujo habitual.
- Deps de las 3 librerías SKD: `compileOnly files("libs/<jar>")` en dev, `required` en `neoforge.mods.toml`. GeckoLib `implementation` + `required`. Nunca jar-in-jar.
- Integraciones solo con la lista cerrada (`INTEGRATIONS.md`): nada de Iron's Spellbooks, Apotheosis, Occultism, Reliquary, equivalent_legacy, ascendant_*.
- README.md en inglés y actualizado.

## Idioma

| Ámbito | Idioma |
|---|---|
| Código fuente, logs, nombres técnicos, commits | **Inglés** (en-US) |
| README.md | **Inglés** (en-US) |
| Documentación interna (docs/, CHANGELOG, WORKFLOW) | **Castellano** (es-ES) |
| CurseForge (descripción, release notes) | **Inglés** (en-US) |
| Guías para `taller_minecraft` (`*_MODEL_GUIDE.md`, tandas nuevas de `TEXTURE_GUIDE.md`) | **Inglés** (en-US) — mejor comprensión en el taller |

**Guías para el taller**: el taller genera y valida todo en su propio repo (`taller_minecraft/.../output/`);
nunca se le pide copiar nada a majestic ni a otro proyecto. Majestic trae los ficheros desde ahí.

---

## Hitos de implementación (delegación OpenCode)

Orden global y detalle en [`PROGRESSION.md §5`](PROGRESSION.md) y `§6` (alcance v1). Resumen para `majestic`
(cada hito depende del hito correspondiente en las librerías):

1. **[hecho] Alpha jugable** — 1 escuela (Luz Estelar) + 3–4 hechizos + foco + HUD de esencia. `astral_core` M1 + `almanac_core` M1.
2. **[hecho, solo T1] Rituales + investigación** — altar T1 (T2 pendiente, ver `DESIGN_MAJESTIC_1-21-1.md §5b`), nodo `first_light`, libro-guía. `astral_core` M2 + `almanac_core` M2+M3.
3. **[hecho, falta probar en juego] Estructuras + Jefe I** — Fallen Shrine (M3), Observatory + arena (M4), Jefe I `warden_of_the_gate` (M5, ver `DESIGN_MAJESTIC_1-21-1.md §5f`). Assets del taller entregados (beta.11). Pendiente: altar T2. `expedition_core` M1.
3b. **[hecho, beta.14] Journey I — la guía como viaje**: 4 mobs nocturnos, hojas del diario que desbloquean capítulos del Almanaque (Prólogo → Cap. 1 Santuario → Cap. 2 Observatorio). Assets del taller integrados en beta.15. Siguiente tramo del viaje: a definir con el usuario.
4. **Plano celeste** (**siguiente hito**) — dimensión, biomas, Actos III–IV, reliquias, jefes finales, tomos. `expedition_core` M2 + `almanac_core` M3.
5. **Contenido v1 completo** (`PROGRESSION.md §6`) + pulido → `1.0.0` de los 4 mods.
   - **Antes de la primera release (1.0.0): integrar más idiomas** además de `en_us` y `es_es` (pedido del usuario 2026-09-24).
   - **Pase de progresión pendiente**: foco/altar/pilar sin forma de conseguirse en supervivencia, sigilos solo en el Acto II, entradas de hechizos del libro sin desbloqueo (ver `DESIGN_MAJESTIC_1-21-1.md §5g`).
   - **Jefe I animaciones v2**: hechas (beta.13). Falta probar el combate en juego.

Claude compila y verifica cada hito (`./gradlew.bat runData build` + arranque `runClient`/`runServer`)
antes de pasar al siguiente. La implementación se delega en OpenCode; el diseño, docs, git,
Graphify y publicación los lleva Claude.
