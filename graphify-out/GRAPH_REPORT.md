# Graph Report - .  (2026-09-22)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 260 nodes · 463 edges · 22 communities (21 shown, 1 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 1,020 input · 214 output

## Graph Freshness
- Built from commit: `866836cf`
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

## God Nodes (most connected - your core abstractions)
1. `EngraveSpellRitual` - 13 edges
2. `Majestic` - 13 edges
3. `MajesticBlocks` - 11 edges
4. `MajesticSpells` - 10 edges
5. `StarlightBoltSpell` - 9 edges
6. `StarlightRevealSpell` - 9 edges
7. `StarlightSurgeSpell` - 9 edges
8. `StarlightWardSpell` - 9 edges
9. `AstralAltarBlock` - 8 edges
10. `AstralAltarBlockEntity` - 8 edges

## Surprising Connections (you probably didn't know these)
- `Project Description` ----> `Majestic`  [EXTRACTED]
  docs/curseforge/project_description.md → README.md
- `Project Variables` ----> `Majestic`  [EXTRACTED]
  docs/curseforge/project_vars.md → README.md
- `Majestic - Content: Magic` ----> `Astral Core`  [EXTRACTED]
  docs/CONTENT_MAGIC.md → README.md
- `v0.0.0-beta.2 - Expedition Core & GeckoLib wired in` --references--> `Astral Core`  [EXTRACTED]
  docs/curseforge/versions/0.0.0-beta.2.md → README.md
- `Majestic - Content: Magic` ----> `Almanac Core`  [EXTRACTED]
  docs/CONTENT_MAGIC.md → README.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **** —  [EXTRACTED 1.00]
- **Mod Dependency** — astral_core, almanac_core, expedition_core, geckolib [EXTRACTED 1.00]

## Communities (22 total, 1 thin omitted)

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
Cohesion: 0.18
Nodes (10): DeferredItem, Items, RegisterGuiLayersEvent, EssenceHudOverlay, EventBusSubscriber, ResourceLocation, SubscribeEvent, IEventBus (+2 more)

### Community 5 - "Starlight Spells"
Cohesion: 0.21
Nodes (8): ResourceLocation, Schools, CastContext, CastResult, Override, ResourceLocation, SpellType, StarlightRevealSpell

### Community 6 - "Project Overview"
Cohesion: 0.26
Nodes (13): Almanac Core, Astral Core, Majestic - Content: Magic, Project Description, Project Variables, v0.0.0-beta.2 - Expedition Core & GeckoLib wired in, Expedition Core, GeckoLib (+5 more)

### Community 7 - "Starlight Bolt"
Cohesion: 0.28
Nodes (7): Spell, CastContext, CastResult, Override, ResourceLocation, SpellType, StarlightBoltSpell

### Community 8 - "Majestic Blocks"
Cohesion: 0.29
Nodes (9): Blocks, DeferredBlock, Block, BlockEntityType, DeferredHolder, DeferredRegister, IEventBus, TagKey (+1 more)

### Community 9 - "Item Interaction"
Cohesion: 0.27
Nodes (9): InteractionHand, InteractionResultHolder, Item, FocusItem, ItemStack, Level, Override, Player (+1 more)

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
Cohesion: 0.36
Nodes (6): DataComponentType, DeferredHolder, DeferredRegister, IEventBus, ResourceLocation, MajesticDataComponents

### Community 14 - "Ritual Registration"
Cohesion: 0.39
Nodes (5): DeferredHolder, DeferredRegister, IEventBus, RitualType, MajesticRituals

### Community 15 - "Almanac Guide"
Cohesion: 0.33
Nodes (6): Almanac Guide Book, Astral Altar, Blank Page, v0.0.0-beta.3 - Astral Altar, research and the Almanac guide book, First Light, Spell Sigils

### Community 16 - "Starlight School"
Cohesion: 0.33
Nodes (6): v0.0.0-beta.1 - Alpha: the Starlight school, Starlight Bolt, Starlight Focus, Starlight Reveal, Starlight Surge, Starlight Ward

### Community 17 - "Event Handling"
Cohesion: 0.47
Nodes (3): NodeUnlockedEvent, ResourceLocation, MajesticEvents

### Community 18 - "Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **5 isolated node(s):** `Changelog`, `JEI`, `Vellumli`, `Project Description`, `Project Variables`
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Majestic` connect `Project Overview` to `Data Management`, `Majestic Spells`, `Item Registration`, `Majestic Blocks`, `Data Components`?**
  _High betweenness centrality (0.263) - this node is a cross-community bridge._
- **Why does `MajesticSpells` connect `Majestic Spells` to `Starlight Surge`, `Starlight Ward`, `Starlight Spells`, `Starlight Bolt`?**
  _High betweenness centrality (0.156) - this node is a cross-community bridge._
- **Why does `MajesticBlocks` connect `Majestic Blocks` to `Astral Altar Entity`?**
  _High betweenness centrality (0.086) - this node is a cross-community bridge._
- **What connects `Changelog`, `JEI`, `Vellumli` to the rest of the system?**
  _5 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Data Management` be split into smaller, more focused modules?**
  _Cohesion score 0.10960960960960961 - nodes in this community are weakly interconnected._
- **Should `Majestic Spells` be split into smaller, more focused modules?**
  _Cohesion score 0.13846153846153847 - nodes in this community are weakly interconnected._