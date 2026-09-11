# CLAUDE.md — majestic (1.21.1)

Este repositorio es el **mod principal** (contenido) del ecosistema **Majestic**, mod NeoForge del
grupo `stalking-dragons/minecraft`. Construye sobre `astral_core`, `expedition_core` y `almanac_core`.

## Workflow del mod

1. **Trabaja con `docs/WORKFLOW_MAJESTIC_1-21-1.md`** — workflow operativo autocontenido. Léelo y síguelo.
2. Diseño: `docs/DESIGN_ECOSYSTEM.md` (visión cruzada + todas las decisiones cerradas §9), `docs/DESIGN_MAJESTIC_1-21-1.md` (técnico del mod), `docs/PROGRESSION.md`, `docs/CONTENT_MAGIC.md`, `docs/CONTENT_WORLD.md`, `docs/INTEGRATIONS.md`, `docs/LORE.md`.
3. Reglas generales (idioma, no asumir, no borrar, delegación OpenCode, prioridad): `../../../codex-docs/reference/CLAUDE.md`. Manda salvo que el usuario indique lo contrario.
4. On-demand: `../../../codex-docs/reference/CURSEFORGE.md` (publicar), `../../../codex-docs/reference/GRAPHIFY.md`, `../../../codex-docs/reference/REPO_SETUP.md`. No leerlos de forma rutinaria.

## Recordatorios específicos

- **Licencia: All Rights Reserved.** `mod_license=All Rights Reserved`, `LICENSE` propio. Repo **privado**, **solo rama `production`**, **sin espejo `main`**, **sin `.gitlab-ci.yml`** de mirror. CI (build/test) se puede añadir más adelante.
- CurseForge: proyecto **público, descarga libre**, licencia ARR.
- **Sin bundling / jar-in-jar** de NINGUNA dependencia. `astral_core`, `expedition_core`, `almanac_core`, `geckolib` = required deps en `neoforge.mods.toml` y en CurseForge; `jei`, `vellumli` = optional.
- En desarrollo, las 3 librerías SKD se consumen como `compileOnly files("libs/<jar>")` (jars construidos de los repos hermanos).
- **Todo el contenido por datagen** (E8). `es_es` a mano; `en_us` generado.
- 3 creative tabs: magia / mundo / reliquias (J3).
- Implementación por capas, delegada en OpenCode (ver `docs/PROGRESSION.md §5`). Diseño, docs, git, Graphify y publicación los lleva Claude.

## Prioridad de instrucciones

1. Petición del usuario en esta sesión.
2. Este archivo + `codex-docs/reference/CLAUDE.md`.
3. Workflow del proyecto (`docs/WORKFLOW_MAJESTIC_1-21-1.md`).
4. Convenciones existentes del proyecto.
