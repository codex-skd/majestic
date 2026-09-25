# Graph Report - 1.21.1  (2026-09-25)

## Corpus Check
- 190 files · ~56,936 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 990 nodes · 1668 edges · 90 communities (61 shown, 29 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 15 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `300c02aa`
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
- WardenAttackGoal
- .tickCharge
- .sweepImpact
- WardenOfTheGateModel
- CompoundTag
- FallenWatcher
- StarlightBoltProjectile.java
- MeteorCrawler
- 1. Four night mobs (GeckoLib models)
- Majestic — GeckoLib model guide: Astral Construct (Boss I minion)
- Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)
- .applyScaling
- MajesticEvents
- Majestic — Journey I, part 2: taller work orders (playtest feedback 2026-09-25)
- MajesticEntityRenderers.java
- CompoundTag

## God Nodes (most connected - your core abstractions)
1. `WardenOfTheGate` - 98 edges
2. `AstralConstruct` - 31 edges
3. `FallenWatcher` - 27 edges
4. `MeteorCrawler` - 24 edges
5. `StargazerCultist` - 24 edges
6. `UmbralMoth` - 23 edges
7. `MajesticEntities` - 15 edges
8. `[0.0.0-beta.16]` - 15 edges
9. `EngraveSpellRitual` - 13 edges
10. `Flujo de trabajo — Majestic (NeoForge)` - 13 edges

## Surprising Connections (you probably didn't know these)
- `AstralConstructRenderer` --references--> `AstralConstruct`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/AstralConstructRenderer.java → src/main/java/com/skd/majestic/content/entity/AstralConstruct.java
- `FallenWatcherModel` --inherits--> `MajesticEntityGeoModel`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/FallenWatcherModel.java → src/main/java/com/skd/majestic/client/entity/MajesticEntityGeoModel.java
- `MeteorCrawlerModel` --inherits--> `MajesticEntityGeoModel`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/MeteorCrawlerModel.java → src/main/java/com/skd/majestic/client/entity/MajesticEntityGeoModel.java
- `StargazerCultistModel` --inherits--> `MajesticEntityGeoModel`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/StargazerCultistModel.java → src/main/java/com/skd/majestic/client/entity/MajesticEntityGeoModel.java
- `UmbralMothModel` --inherits--> `MajesticEntityGeoModel`  [EXTRACTED]
  src/main/java/com/skd/majestic/client/entity/UmbralMothModel.java → src/main/java/com/skd/majestic/client/entity/MajesticEntityGeoModel.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **Mod Dependency** — astral_core, almanac_core, expedition_core, geckolib [EXTRACTED 1.00]

## Communities (90 total, 29 thin omitted)

### Community 0 - "Data Management"
Cohesion: 0.09
Nodes (24): BlockStateProvider, CachedOutput, DataProvider, ExistingFileHelper, GatherDataEvent, ItemModelProvider, Items, LanguageProvider (+16 more)

### Community 1 - "Majestic Spells"
Cohesion: 0.06
Nodes (33): BlockItem, BuildCreativeModeTabContentsEvent, CommandContext, CommandDispatcher, CommandSourceStack, CreativeModeTab, DeferredItem, DeferredSpawnEggItem (+25 more)

### Community 2 - "Astral Altar"
Cohesion: 0.20
Nodes (15): BaseEntityBlock, BlockEntity, BlockEntityTicker, BlockHitResult, InteractionResult, MapCodec, RenderShape, AstralAltarBlock (+7 more)

### Community 3 - "Engrave Ritual"
Cohesion: 0.14
Nodes (17): DataComponentType, Ritual, RitualContext, RitualResult, DeferredHolder, DeferredRegister, IEventBus, ResourceLocation (+9 more)

### Community 4 - "Item Registration"
Cohesion: 0.13
Nodes (21): Component, Item, FocusItem, InteractionHand, InteractionResultHolder, ItemStack, Level, Override (+13 more)

### Community 5 - "Starlight Spells"
Cohesion: 0.07
Nodes (26): Spell, ResourceLocation, Schools, DeferredHolder, DeferredRegister, IEventBus, SpellType, MajesticSpells (+18 more)

### Community 8 - "Majestic Blocks"
Cohesion: 0.15
Nodes (17): AltarBlockEntity, Blocks, DeferredBlock, AstralAltarBlockEntity, Block, BlockPos, BlockState, Multiblock (+9 more)

### Community 9 - "Item Interaction"
Cohesion: 0.11
Nodes (20): GeoEntity, Phantom, RandomSource, UmbralMothModel, AnimatableInstanceCache, AnimationState, BlockPos, ControllerRegistrar (+12 more)

### Community 10 - "Starlight Surge"
Cohesion: 0.13
Nodes (14): 0. Why (playtest feedback), 1. Animation list (v2), 2. Attacks, 3. Movement, 4. Validation (same tool as v1), 5. Done means, `attack` — horizontal sweep (REWORK, the most frequent attack), `attack_slam` — overhead slam (NEW, used when several players are close) (+6 more)

### Community 11 - "Starlight Ward"
Cohesion: 0.12
Nodes (15): BossBarController, BossEncounter, BossLootTable, EntityDataAccessor, GeoBossEntity, AnimationState, DifficultyInstance, EntityType (+7 more)

### Community 12 - "Astral Altar Entity"
Cohesion: 0.07
Nodes (25): DefaultedEntityGeoModel, AstralConstructModel, Override, ResourceLocation, Override, ResourceLocation, MajesticEntityGeoModel, Override (+17 more)

### Community 13 - "Data Components"
Cohesion: 0.09
Nodes (22): 1. Visión, 2. Módulos, 3. Grafo de dependencias, 4. Regla de autonomía, 5. Contratos entre módulos, 6. Convenciones, 7. Licencias, 8. Roadmap de creación y publicación (+14 more)

### Community 14 - "Ritual Registration"
Cohesion: 0.13
Nodes (13): BoundingBox, Factory, Post, ResourceKey, SavedData, CompoundTag, Override, Provider (+5 more)

### Community 18 - "Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 20 - "Changelog"
Cohesion: 0.05
Nodes (41): [0.0.0-beta.1], [0.0.0-beta.16], [0.0.0-beta.2], [0.0.0-beta.3], [0.0.0-beta.4], [0.0.0-beta.5], [0.0.0-beta.6], [0.0.0-beta.7] (+33 more)

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
Nodes (15): Monster, RangedAttackMob, StargazerCultistModel, AnimatableInstanceCache, AnimationState, ControllerRegistrar, DamageSource, EntityType (+7 more)

### Community 60 - "Majestic"
Cohesion: 0.33
Nodes (5): Dependencies (required), License, Majestic, Requirements, The idea

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
Cohesion: 0.16
Nodes (6): RemovalReason, Builder, ControllerRegistrar, DamageSource, Override, SoundEvent

### Community 71 - "WardenAttackGoal"
Cohesion: 0.29
Nodes (4): Goal, LivingEntity, Override, WardenAttackGoal

### Community 74 - "WardenOfTheGateModel"
Cohesion: 0.20
Nodes (9): GeoEntityRenderer, GeoModel, MultiBufferSource, PoseStack, AstralConstructRenderer, Context, Context, Override (+1 more)

### Community 75 - "CompoundTag"
Cohesion: 0.29
Nodes (3): BlockPos, BlockState, Nullable

### Community 77 - "FallenWatcher"
Cohesion: 0.11
Nodes (17): FallenWatcherModel, FallenWatcher, AnimatableInstanceCache, AnimationState, BlockPos, BlockState, ControllerRegistrar, DamageSource (+9 more)

### Community 78 - "StarlightBoltProjectile.java"
Cohesion: 0.06
Nodes (30): AbstractHurtingProjectile, EntityAttributeCreationEvent, EntityHitResult, HitResult, ParticleOptions, RegisterSpawnPlacementsEvent, Builder, DeferredHolder (+22 more)

### Community 79 - "MeteorCrawler"
Cohesion: 0.10
Nodes (19): Spider, MeteorCrawlerModel, DelayedMeleeAttack, LivingEntity, Nullable, AnimatableInstanceCache, AnimationState, BlockPos (+11 more)

### Community 80 - "1. Four night mobs (GeckoLib models)"
Cohesion: 0.15
Nodes (12): 1.1 `fallen_watcher` — Fallen Watcher (humanoid, melee), 1.2 `stargazer_cultist` — Stargazer Cultist (humanoid, ranged caster), 1.3 `meteor_crawler` — Meteor Crawler (arthropod, wall climber), 1.4 `umbral_moth` — Umbral Moth (flyer), 1.5 Validation, 1. Four night mobs (GeckoLib models), 2. Two journal page items (16×16, same format as `TEXTURE_GUIDE.md §0`), 3. Fallen Shrine fix (`structures/shrine`) (+4 more)

### Community 81 - "Majestic — GeckoLib model guide: Astral Construct (Boss I minion)"
Cohesion: 0.18
Nodes (8): 0. Concept, 1. Output files (3), 2. Bone hierarchy (exact names), 3. Texture, 4. Animations (5 — exact names), 5. Validation (same tool as the Warden), 6. Done means, Majestic — GeckoLib model guide: Astral Construct (Boss I minion)

### Community 82 - "Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)"
Cohesion: 0.25
Nodes (8): 0. Concepto, 1. Herramientas, 2. Ficheros a exportar (3, rutas exactas), 3. Jerarquía de huesos (bones), 4. Animaciones (6 — nombres exactos), 5. Qué hace Claude con esto (no hace falta que te preocupes por ello), 6. Cuando termines, Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)

### Community 86 - "MajesticEvents"
Cohesion: 0.32
Nodes (4): NodeUnlockedEvent, PlayerLoggedInEvent, ResourceLocation, MajesticEvents

### Community 87 - "Majestic — Journey I, part 2: taller work orders (playtest feedback 2026-09-25)"
Cohesion: 0.29
Nodes (6): Done means, Majestic — Journey I, part 2: taller work orders (playtest feedback 2026-09-25), Task A — Warden of the Gate: hold the staff properly (model + animation touch-up), Task B — Fallen Shrine: a richer structure (`structures/shrine`), Task C — Observatory: richer entrance and pieces (`structures/observatory`), Task D — Guide-book images (after B and C)

### Community 88 - "MajesticEntityRenderers.java"
Cohesion: 0.38
Nodes (4): RegisterRenderers, EventBusSubscriber, SubscribeEvent, MajesticEntityRenderers

## Knowledge Gaps
- **206 isolated node(s):** `Workflow del mod`, `Recordatorios específicos`, `Prioridad de instrucciones`, `Added`, `Fixed` (+201 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **29 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `WardenOfTheGate` connect `WardenOfTheGateModel` to `WardenOfTheGateRenderer`, `Majestic — GeckoLib model guide: Astral Construct (Boss I minion)`, `WardenAttackGoal`, `.tickCharge`, `.sweepImpact`, `Starlight Ward`, `Astral Altar Entity`, `CompoundTag`, `StarlightBoltProjectile.java`, `Event Handling`, `.applyScaling`, `CompoundTag`, `Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)`?**
  _High betweenness centrality (0.125) - this node is a cross-community bridge._
- **Why does `AstralConstruct` connect `Astral Altar Entity` to `Item Interaction`, `WardenOfTheGateModel`, `Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)`, `StarlightBoltProjectile.java`?**
  _High betweenness centrality (0.053) - this node is a cross-community bridge._
- **Why does `FallenWatcher` connect `FallenWatcher` to `Item Interaction`, `Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)`, `StarlightBoltProjectile.java`, `MeteorCrawler`?**
  _High betweenness centrality (0.036) - this node is a cross-community bridge._
- **What connects `Workflow del mod`, `Recordatorios específicos`, `Prioridad de instrucciones` to the rest of the system?**
  _206 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Data Management` be split into smaller, more focused modules?**
  _Cohesion score 0.08985200845665962 - nodes in this community are weakly interconnected._
- **Should `Majestic Spells` be split into smaller, more focused modules?**
  _Cohesion score 0.06458635703918723 - nodes in this community are weakly interconnected._
- **Should `Engrave Ritual` be split into smaller, more focused modules?**
  _Cohesion score 0.13675213675213677 - nodes in this community are weakly interconnected._