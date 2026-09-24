# Graph Report - 1.21.1  (2026-09-24)

## Corpus Check
- 85 files · ~25,210 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 455 nodes · 632 edges · 59 communities (36 shown, 23 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `bd242e9e`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Data Management
- Majestic Spells
- Astral Altar
- Engrave Ritual
- Item Registration
- Starlight Spells
- Project Overview
- Starlight Bolt
- Majestic Blocks
- Item Interaction
- Starlight Surge
- Starlight Ward
- Astral Altar Entity
- Data Components
- Ritual Registration
- Almanac Guide
- Starlight School
- Event Handling
- Build Scripts
- Changelog
- 1. Ítems (9)
- Majestic — Guía de construcción: Observatory (Acto II)
- Flujo de trabajo — Majestic (NeoForge)
- CurseForge — Variables del proyecto
- Majestic — Diseño técnico (mod principal)
- Majestic — Progresión
- Majestic — Contenido: magia
- Majestic — Guía de construcción de estructuras (Acto I–II)
- CLAUDE.md — majestic (1.21.1)
- Astral Altar
- Astral Core
- Blank Page
- Majestic - Content: Magic
- Project Description
- Project Variables
- v0.0.0-beta.2 - Expedition Core & GeckoLib wired in
- v0.0.0-beta.3 - Astral Altar, research and the Almanac guide book
- Expedition Core
- First Light
- GeckoLib
- JEI
- Spell Sigils
- Regalia Slots API
- Starlight Bolt
- Starlight Focus
- Starlight Reveal
- Starlight Surge
- Starlight Ward
- Vellumli

## God Nodes (most connected - your core abstractions)
1. `EngraveSpellRitual` - 13 edges
2. `Flujo de trabajo — Majestic (NeoForge)` - 13 edges
3. `MajesticBlocks` - 11 edges
4. `Majestic` - 11 edges
5. `MajesticSpells` - 10 edges
6. `Majestic — Diseño del ecosistema` - 10 edges
7. `Majestic — Diseño técnico (mod principal)` - 10 edges
8. `1. Ítems (9)` - 10 edges
9. `CurseForge — Variables del proyecto` - 10 edges
10. `StarlightBoltSpell` - 9 edges

## Surprising Connections (you probably didn't know these)
- `MajesticBlocks` --references--> `AstralAltarBlockEntity`  [EXTRACTED]
  src/main/java/com/skd/majestic/content/block/MajesticBlocks.java → src/main/java/com/skd/majestic/content/block/AstralAltarBlockEntity.java
- `MajesticItems` --references--> `FocusItem`  [EXTRACTED]
  src/main/java/com/skd/majestic/content/item/MajesticItems.java → src/main/java/com/skd/majestic/content/item/FocusItem.java
- `MajesticRituals` --references--> `EngraveSpellRitual`  [EXTRACTED]
  src/main/java/com/skd/majestic/magic/ritual/MajesticRituals.java → src/main/java/com/skd/majestic/magic/ritual/EngraveSpellRitual.java
- `MajesticSpells` --references--> `StarlightBoltSpell`  [EXTRACTED]
  src/main/java/com/skd/majestic/magic/spell/MajesticSpells.java → src/main/java/com/skd/majestic/magic/spell/StarlightBoltSpell.java
- `MajesticSpells` --references--> `StarlightRevealSpell`  [EXTRACTED]
  src/main/java/com/skd/majestic/magic/spell/MajesticSpells.java → src/main/java/com/skd/majestic/magic/spell/StarlightRevealSpell.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **Mod Dependency** — astral_core, almanac_core, expedition_core, geckolib [EXTRACTED 1.00]

## Communities (59 total, 23 thin omitted)

### Community 0 - "Data Management"
Cohesion: 0.11
Nodes (19): BlockStateProvider, CachedOutput, DataProvider, ExistingFileHelper, GatherDataEvent, ItemModelProvider, LanguageProvider, PackOutput (+11 more)

### Community 1 - "Majestic Spells"
Cohesion: 0.14
Nodes (16): CommandContext, CommandDispatcher, CommandSourceStack, Logger, Mod, ModContainer, ServerPlayer, SpellType (+8 more)

### Community 2 - "Astral Altar"
Cohesion: 0.22
Nodes (14): BaseEntityBlock, BlockEntity, BlockEntityTicker, BlockHitResult, InteractionResult, MapCodec, Nullable, AstralAltarBlock (+6 more)

### Community 3 - "Engrave Ritual"
Cohesion: 0.22
Nodes (11): Ritual, RitualContext, RitualResult, EngraveSpellRitual, Block, ItemStack, Multiblock, Override (+3 more)

### Community 4 - "Item Registration"
Cohesion: 0.13
Nodes (15): DeferredItem, Items, Dependencies (required), License, Majestic, Requirements, The idea, RegisterGuiLayersEvent (+7 more)

### Community 5 - "Starlight Spells"
Cohesion: 0.12
Nodes (15): Spell, ResourceLocation, Schools, CastContext, CastResult, Override, ResourceLocation, SpellType (+7 more)

### Community 7 - "Starlight Bolt"
Cohesion: 0.09
Nodes (23): 1. El Plano celeste (dimensión), 2. Biomas, 3. Estructuras, 4. Mobs (no jefes), 5. Jefes, 6. Worldgen técnico, 7. Botín y questline, Majestic — Contenido: mundo y aventura (+15 more)

### Community 8 - "Majestic Blocks"
Cohesion: 0.29
Nodes (9): Blocks, DeferredBlock, Block, BlockEntityType, DeferredHolder, DeferredRegister, IEventBus, TagKey (+1 more)

### Community 9 - "Item Interaction"
Cohesion: 0.15
Nodes (15): DataComponentType, InteractionHand, InteractionResultHolder, Item, DeferredHolder, DeferredRegister, IEventBus, ResourceLocation (+7 more)

### Community 10 - "Starlight Surge"
Cohesion: 0.30
Nodes (6): CastContext, CastResult, Override, ResourceLocation, SpellType, StarlightSurgeSpell

### Community 11 - "Starlight Ward"
Cohesion: 0.30
Nodes (6): CastContext, CastResult, Override, ResourceLocation, SpellType, StarlightWardSpell

### Community 12 - "Astral Altar Entity"
Cohesion: 0.31
Nodes (8): AltarBlockEntity, AstralAltarBlockEntity, Block, BlockPos, BlockState, Multiblock, Override, TagKey

### Community 13 - "Data Components"
Cohesion: 0.09
Nodes (22): 1. Visión, 2. Módulos, 3. Grafo de dependencias, 4. Regla de autonomía, 5. Contratos entre módulos, 6. Convenciones, 7. Licencias, 8. Roadmap de creación y publicación (+14 more)

### Community 14 - "Ritual Registration"
Cohesion: 0.39
Nodes (5): DeferredHolder, DeferredRegister, IEventBus, RitualType, MajesticRituals

### Community 17 - "Event Handling"
Cohesion: 0.47
Nodes (3): NodeUnlockedEvent, ResourceLocation, MajesticEvents

### Community 18 - "Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 20 - "Changelog"
Cohesion: 0.07
Nodes (26): [0.0.0-beta.1], [0.0.0-beta.2], [0.0.0-beta.3], [0.0.0-beta.4], [0.0.0-beta.5], [0.0.0-beta.6], [0.0.0-beta.7], [0.0.0-beta.8] (+18 more)

### Community 22 - "1. Ítems (9)"
Cohesion: 0.11
Nodes (18): 0. Formato técnico (aplica a TODOS los ítems), 1. Ítems (9), 2. Bloques (2), 3. Opción: generar referencia con IA antes de pixelar a mano, 4. Cuando termines, `altar_blueprint_t2.png`, `astral_altar.png`, `astral_dust.png` (+10 more)

### Community 23 - "Majestic — Guía de construcción: Observatory (Acto II)"
Cohesion: 0.14
Nodes (13): 0. Resumen de la estructura, 1. Mecánica técnica (repetir por cada una de las 4 piezas), 2. Piezas (especificación exacta), 3. Resumen de exportación (tabla rápida), 4. Botín (referencia — no hace falta que construyas los ítems, ya existen o los creo yo), 5. Cuando termines, Bloques Jigsaw — cómo rellenarlos, Majestic — Guía de construcción: Observatory (Acto II) (+5 more)

### Community 24 - "Flujo de trabajo — Majestic (NeoForge)"
Cohesion: 0.15
Nodes (13): Buenas prácticas, Commits (Conventional Commits), Convenciones de nomenclatura, Específico del mod, Estructura del proyecto, Flujo de trabajo — Majestic (NeoForge), Flujo por tarea, Hitos de implementación (delegación OpenCode) (+5 more)

### Community 25 - "CurseForge — Variables del proyecto"
Cohesion: 0.17
Nodes (11): CurseForge — Variables del proyecto, Descripción del proyecto y logo, Entorno "Client & Server", Flujo completo (primera vez), IDs de `gameVersions` para 1.21.1 (verificados, mismos que el resto de mods 1.21.1 del workspace), Proyecto, Rama, Relaciones (dependencias declaradas en CurseForge) (+3 more)

### Community 26 - "Majestic — Diseño técnico (mod principal)"
Cohesion: 0.18
Nodes (11): 1. Alcance, 2. Paquetes, 3. Estrategia de datos, 4. Distribución (licencia ARR), 5. Preguntas abiertas (además de E1–E9 del ecosistema), 5b. Hito M2 — Altar T1, nodo de investigación inicial, guía (2026-09-22), 5c. Hito M3 — Fallen Shrine (Acto I) (2026-09-23), 5e. Bug real: duplicados JSON a mano vs datagen (2026-09-24) (+3 more)

### Community 27 - "Majestic — Progresión"
Cohesion: 0.18
Nodes (11): 1. Bucle de juego, 2. Actos, 3. Curva de esencia, 4. Llaves de progresión (items-gate), 5. Orden de implementación sugerido, 6. Alcance v1 (baseline aceptado — E5), 7. Guía y tomos de avance (E6), Libro-guía principal — `majestic:almanac` (working title "Almanaque celeste") (+3 more)

### Community 28 - "Majestic — Contenido: magia"
Cohesion: 0.20
Nodes (10): 1. Esencia, 2. Escuelas, 3. Hechizos, 4. Rituales, 5. Investigación — nodos de constelación, 6. Reliquias, Altares, HUD de esencia (propuesta para E2) (+2 more)

### Community 29 - "Majestic — Guía de construcción de estructuras (Acto I–II)"
Cohesion: 0.25
Nodes (7): 0. Herramientas, 1. Flujo de exportación (repetir por cada pieza), 2. `fallen_shrine` (Acto I) — ruina pequeña, sin jigsaw real, 3. `observatory` (Acto II) — jigsaw real, con sala del jefe, 4. Cuando termines, Bloques Jigsaw (solo para piezas de `observatory`, `fallen_shrine` no los necesita), Majestic — Guía de construcción de estructuras (Acto I–II)

### Community 30 - "CLAUDE.md — majestic (1.21.1)"
Cohesion: 0.40
Nodes (4): CLAUDE.md — majestic (1.21.1), Prioridad de instrucciones, Recordatorios específicos, Workflow del mod

## Knowledge Gaps
- **147 isolated node(s):** `Workflow del mod`, `Recordatorios específicos`, `Prioridad de instrucciones`, `Fixed`, `Notes` (+142 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **23 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Majestic` connect `Item Registration` to `Majestic Blocks`, `Majestic Spells`, `Item Interaction`, `Data Management`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Why does `MajesticSpells` connect `Majestic Spells` to `Starlight Surge`, `Starlight Ward`, `Starlight Spells`?**
  _High betweenness centrality (0.049) - this node is a cross-community bridge._
- **Why does `MajesticBlocks` connect `Majestic Blocks` to `Astral Altar Entity`?**
  _High betweenness centrality (0.027) - this node is a cross-community bridge._
- **What connects `Workflow del mod`, `Recordatorios específicos`, `Prioridad de instrucciones` to the rest of the system?**
  _147 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Data Management` be split into smaller, more focused modules?**
  _Cohesion score 0.10960960960960961 - nodes in this community are weakly interconnected._
- **Should `Majestic Spells` be split into smaller, more focused modules?**
  _Cohesion score 0.13846153846153847 - nodes in this community are weakly interconnected._
- **Should `Item Registration` be split into smaller, more focused modules?**
  _Cohesion score 0.12554112554112554 - nodes in this community are weakly interconnected._