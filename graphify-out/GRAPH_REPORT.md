# Graph Report - 1.21.1  (2026-09-24)

## Corpus Check
- 138 files · ~38,325 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 712 nodes · 1102 edges · 71 communities (48 shown, 23 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS · INFERRED: 5 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `88fa77c8`
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
- Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)
- Majestic
- WardenOfTheGateModel
- Majestic — Contenido: mundo y aventura
- Majestic — Ambientación (lore)
- Majestic — Integraciones
- WardenOfTheGateRenderer
- Majestic — Índice de documentación de diseño
- Majestic — GeckoLib model guide: Astral Construct (Boss I minion)

## God Nodes (most connected - your core abstractions)
1. `WardenOfTheGate` - 65 edges
2. `AstralConstruct` - 31 edges
3. `EngraveSpellRitual` - 13 edges
4. `Flujo de trabajo — Majestic (NeoForge)` - 13 edges
5. `Majestic — Diseño técnico (mod principal)` - 12 edges
6. `MajesticBlocks` - 11 edges
7. `MajesticSpells` - 10 edges
8. `Changelog` - 10 edges
9. `Majestic — Diseño del ecosistema` - 10 edges
10. `1. Ítems (9)` - 10 edges

## Surprising Connections (you probably didn't know these)
- `AstralConstructModel` --references--> `AstralConstruct`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/AstralConstructModel.java → src/main/java/com/skd/majestic/content/entity/AstralConstruct.java
- `WardenOfTheGateModel` --references--> `WardenOfTheGate`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/WardenOfTheGateModel.java → src/main/java/com/skd/majestic/content/entity/boss/WardenOfTheGate.java
- `WardenOfTheGateRenderer` --references--> `WardenOfTheGate`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/WardenOfTheGateRenderer.java → src/main/java/com/skd/majestic/content/entity/boss/WardenOfTheGate.java
- `MajesticEntities` --references--> `AstralConstruct`  [EXTRACTED]
  src/main/java/com/skd/majestic/content/entity/MajesticEntities.java → src/main/java/com/skd/majestic/content/entity/AstralConstruct.java
- `MajesticEntities` --references--> `WardenOfTheGate`  [EXTRACTED]
  src/main/java/com/skd/majestic/content/entity/MajesticEntities.java → src/main/java/com/skd/majestic/content/entity/boss/WardenOfTheGate.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **Mod Dependency** — astral_core, almanac_core, expedition_core, geckolib [EXTRACTED 1.00]

## Communities (71 total, 23 thin omitted)

### Community 0 - "Data Management"
Cohesion: 0.09
Nodes (23): BlockStateProvider, CachedOutput, DataProvider, ExistingFileHelper, GatherDataEvent, ItemModelProvider, LanguageProvider, PackOutput (+15 more)

### Community 1 - "Majestic Spells"
Cohesion: 0.08
Nodes (23): BuildCreativeModeTabContentsEvent, CreativeModeTab, DefaultedEntityGeoModel, Logger, Mod, ModContainer, AstralConstructModel, Override (+15 more)

### Community 2 - "Astral Altar"
Cohesion: 0.22
Nodes (14): BaseEntityBlock, BlockEntity, BlockEntityTicker, BlockHitResult, InteractionResult, MapCodec, AstralAltarBlock, BlockEntityType (+6 more)

### Community 3 - "Engrave Ritual"
Cohesion: 0.22
Nodes (11): Ritual, RitualContext, RitualResult, EngraveSpellRitual, Block, ItemStack, Multiblock, Override (+3 more)

### Community 4 - "Item Registration"
Cohesion: 0.17
Nodes (12): BlockItem, DeferredItem, DeferredSpawnEggItem, Items, RegisterGuiLayersEvent, EssenceHudOverlay, EventBusSubscriber, ResourceLocation (+4 more)

### Community 5 - "Starlight Spells"
Cohesion: 0.06
Nodes (32): Spell, ResourceLocation, Schools, DeferredHolder, DeferredRegister, IEventBus, SpellType, MajesticSpells (+24 more)

### Community 8 - "Majestic Blocks"
Cohesion: 0.15
Nodes (17): AltarBlockEntity, Blocks, DeferredBlock, AstralAltarBlockEntity, Block, BlockPos, BlockState, Multiblock (+9 more)

### Community 9 - "Item Interaction"
Cohesion: 0.15
Nodes (15): DataComponentType, InteractionHand, InteractionResultHolder, Item, DeferredHolder, DeferredRegister, IEventBus, ResourceLocation (+7 more)

### Community 10 - "Starlight Surge"
Cohesion: 0.13
Nodes (14): 0. Why (playtest feedback), 1. Animation list (v2), 2. Attacks, 3. Movement, 4. Validation (same tool as v1), 5. Done means, `attack` — horizontal sweep (REWORK, the most frequent attack), `attack_slam` — overhead slam (NEW, used when several players are close) (+6 more)

### Community 11 - "Starlight Ward"
Cohesion: 0.06
Nodes (31): ArenaLock, Attribute, BossBarController, BossEncounter, BossLootTable, DifficultyInstance, GeoBossEntity, Holder (+23 more)

### Community 12 - "Astral Altar Entity"
Cohesion: 0.09
Nodes (20): GeoEntity, GeoEntityRenderer, Monster, AstralConstructRenderer, Context, AstralConstruct, AnimatableInstanceCache, AnimationState (+12 more)

### Community 13 - "Data Components"
Cohesion: 0.09
Nodes (22): 1. Visión, 2. Módulos, 3. Grafo de dependencias, 4. Regla de autonomía, 5. Contratos entre módulos, 6. Convenciones, 7. Licencias, 8. Roadmap de creación y publicación (+14 more)

### Community 14 - "Ritual Registration"
Cohesion: 0.13
Nodes (13): BoundingBox, Factory, Post, ResourceKey, SavedData, CompoundTag, Override, Provider (+5 more)

### Community 17 - "Event Handling"
Cohesion: 0.17
Nodes (10): CommandContext, CommandDispatcher, CommandSourceStack, NodeUnlockedEvent, PlayerLoggedInEvent, ServerPlayer, SpellType, MajesticCommand (+2 more)

### Community 18 - "Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 20 - "Changelog"
Cohesion: 0.06
Nodes (35): [0.0.0-beta.1], [0.0.0-beta.12], [0.0.0-beta.2], [0.0.0-beta.3], [0.0.0-beta.4], [0.0.0-beta.5], [0.0.0-beta.6], [0.0.0-beta.7] (+27 more)

### Community 22 - "1. Ítems (9)"
Cohesion: 0.08
Nodes (24): 0. Formato técnico (aplica a TODOS los ítems), 1. Ítems (9), 2. Bloques (2), 3. Opción: generar referencia con IA antes de pixelar a mano, 4. Batch 2 — Boss I milestone (done, beta.11), 4b. Batch 3 — guide book (pending, for taller_minecraft), 5. Cuando termines, `almanac.png` (item — the guide book "The Almanac") (+16 more)

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
Cohesion: 0.15
Nodes (13): 1. Alcance, 2. Paquetes, 3. Estrategia de datos, 4. Distribución (licencia ARR), 5. Preguntas abiertas (además de E1–E9 del ecosistema), 5b. Hito M2 — Altar T1, nodo de investigación inicial, guía (2026-09-22), 5c. Hito M3 — Fallen Shrine (Acto I) (2026-09-23), 5e. Bug real: duplicados JSON a mano vs datagen (2026-09-24) (+5 more)

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

### Community 59 - "Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)"
Cohesion: 0.13
Nodes (12): EntityAttributeCreationEvent, RegisterRenderers, EventBusSubscriber, SubscribeEvent, MajesticEntityRenderers, Builder, Builder, DeferredHolder (+4 more)

### Community 60 - "Majestic"
Cohesion: 0.33
Nodes (5): Dependencies (required), License, Majestic, Requirements, The idea

### Community 62 - "WardenOfTheGateModel"
Cohesion: 0.18
Nodes (8): 0. Concepto, 1. Herramientas, 2. Ficheros a exportar (3, rutas exactas), 3. Jerarquía de huesos (bones), 4. Animaciones (6 — nombres exactos), 5. Qué hace Claude con esto (no hace falta que te preocupes por ello), 6. Cuando termines, Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)

### Community 63 - "Majestic — Contenido: mundo y aventura"
Cohesion: 0.25
Nodes (8): 1. El Plano celeste (dimensión), 2. Biomas, 3. Estructuras, 4. Mobs (no jefes), 5. Jefes, 6. Worldgen técnico, 7. Botín y questline, Majestic — Contenido: mundo y aventura

### Community 64 - "Majestic — Ambientación (lore)"
Cohesion: 0.33
Nodes (6): 1. Premisa, 2. Cosmología, 3. Escuelas (tono narrativo), 4. Glosario de nombres (coherencia), 5. Textos de guía (estructura), Majestic — Ambientación (lore)

### Community 65 - "Majestic — Integraciones"
Cohesion: 0.40
Nodes (5): 1. Matriz, 2. Prohibido integrar (lista negra), 3. Notas de licencia relevantes para integración, 4. Historial de decisiones, Majestic — Integraciones

### Community 66 - "WardenOfTheGateRenderer"
Cohesion: 0.50
Nodes (3): GeoBossRenderer, Context, WardenOfTheGateRenderer

### Community 67 - "Majestic — Índice de documentación de diseño"
Cohesion: 0.50
Nodes (4): Documentos, Estado, Majestic — Índice de documentación de diseño, Módulos y repos

### Community 68 - "Majestic — GeckoLib model guide: Astral Construct (Boss I minion)"
Cohesion: 0.25
Nodes (8): 0. Concept, 1. Output files (3), 2. Bone hierarchy (exact names), 3. Texture, 4. Animations (5 — exact names), 5. Validation (same tool as the Warden), 6. Done means, Majestic — GeckoLib model guide: Astral Construct (Boss I minion)

## Knowledge Gaps
- **186 isolated node(s):** `Workflow del mod`, `Recordatorios específicos`, `Prioridad de instrucciones`, `Added`, `Fixed` (+181 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **23 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `WardenOfTheGate` connect `Starlight Ward` to `Majestic Spells`, `WardenOfTheGateRenderer`, `Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)`, `Astral Altar Entity`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `AstralConstruct` connect `Astral Altar Entity` to `Majestic Spells`, `Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)`?**
  _High betweenness centrality (0.049) - this node is a cross-community bridge._
- **What connects `Workflow del mod`, `Recordatorios específicos`, `Prioridad de instrucciones` to the rest of the system?**
  _186 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Data Management` be split into smaller, more focused modules?**
  _Cohesion score 0.09302325581395349 - nodes in this community are weakly interconnected._
- **Should `Majestic Spells` be split into smaller, more focused modules?**
  _Cohesion score 0.08333333333333333 - nodes in this community are weakly interconnected._
- **Should `Starlight Spells` be split into smaller, more focused modules?**
  _Cohesion score 0.060655737704918035 - nodes in this community are weakly interconnected._
- **Should `Majestic Blocks` be split into smaller, more focused modules?**
  _Cohesion score 0.14624505928853754 - nodes in this community are weakly interconnected._