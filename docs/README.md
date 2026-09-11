# Majestic — Índice de documentación de diseño

Borrador inicial (2026-09-08). Mod de magia y aventura arcano-celestial para MC 1.21.1 / NeoForge 21.1.249.

## Documentos

| Documento | Contenido |
|---|---|
| [`DESIGN_ECOSYSTEM.md`](DESIGN_ECOSYSTEM.md) | **Empezar aquí.** Los 4 módulos, grafo de dependencias, contratos, convenciones, licencias, roadmap y preguntas abiertas E1–E9. |
| [`PROGRESSION.md`](PROGRESSION.md) | Espina de progresión: actos, gates, llaves, curva de esencia, orden de implementación. |
| [`CONTENT_MAGIC.md`](CONTENT_MAGIC.md) | Escuelas, esencia, hechizos, rituales, nodos de investigación, reliquias (tablas para rellenar). |
| [`CONTENT_WORLD.md`](CONTENT_WORLD.md) | Dimensión, biomas, estructuras, mobs, jefes, worldgen, botín, questline. |
| [`INTEGRATIONS.md`](INTEGRATIONS.md) | Matriz de dependencias, lista negra de mods a no integrar, notas de licencia. |
| [`LORE.md`](LORE.md) | Ambientación, cosmología, glosario de nombres ES/EN, estructura de textos de guía. |
| [`DESIGN_MAJESTIC_1-21-1.md`](DESIGN_MAJESTIC_1-21-1.md) | Diseño técnico del mod principal: paquetes, estrategia de datos, distribución ARR. |

> Los diseños técnicos de las 3 librerías **se movieron a su propio repo** (2026-09-11):
> `astral_core/neoforge/1.21.1/docs/DESIGN_ASTRAL_CORE_1-21-1.md`,
> `expedition_core/…/DESIGN_EXPEDITION_CORE_1-21-1.md`,
> `almanac_core/…/DESIGN_ALMANAC_CORE_1-21-1.md`. La carpeta `design/` ya no existe aquí.

## Módulos y repos

| Repo | Mod ID | Licencia |
|---|---|---|
| `stalking-dragons/minecraft/astral-core` | `astral_core` | MIT |
| `stalking-dragons/minecraft/expedition-core` | `expedition_core` | MIT |
| `stalking-dragons/minecraft/almanac-core` | `almanac_core` | MIT |
| `stalking-dragons/minecraft/majestic` | `majestic` | All Rights Reserved |

## Estado

- [x] Concepto, arquitectura, nombres, dependencias, licencias.
- [x] **Diseño cerrado**: E1–E9, A1–A6, B2/B3, J1–J4, C1–C3, Setup-1/2/3. Ver `DESIGN_ECOSYSTEM.md §9`.
- [ ] **Única acción previa a setup**: fijar versiones Maven de GeckoLib / JEI / vellumli para 1.21.1 (M5, Claude).
- [ ] `REPO_SETUP` de `astral_core` → `expedition_core` → `almanac_core` → `majestic` (verificar estado real de los repos primero).
- [ ] Mover `design/DESIGN_<X>.md` → `<repo>/neoforge/1.21.1/docs/DESIGN_<X>_1-21-1.md` + generar `WORKFLOW_*`.
- [ ] Crear los 4 proyectos CurseForge (Fase 3 del roadmap) cuando haya build que arranque.
- [ ] Implementación por capas (delegada en OpenCode).

> Nota: los `design/DESIGN_*.md` de las librerías viven aquí temporalmente. Al crear cada repo se
> mueven a su `docs/` y se renombran con sufijo `_1-21-1`.
