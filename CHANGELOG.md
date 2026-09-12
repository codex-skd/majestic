# Changelog

All notable changes to this project will be documented in this file.

## [0.0.0-beta.1]

First versioned build. **Minecraft 1.21.1 / NeoForge 21.1.249** (Java 21). All Rights Reserved.

### Added
- Initial project setup: build (`net.neoforged.moddev` 2.0.142), Parchment `2024.11.17`. Maven
  Central + GeckoLib + BlameJared repos declared for later dependencies.
- **Astral Core** + **Almanac Core** wired as real dependencies (external jars, never bundled).
- **Alpha content — the Starlight school**: `Schools.STARLIGHT`. Four playable spells registered
  as `astral_core` `SpellType`s — `starlight_bolt` (raycast hitscan, damage + Blindness),
  `starlight_ward` (self Absorption + Regeneration), `starlight_reveal` (AoE Glowing on nearby
  hostiles), `starlight_surge` (self Speed) — each reads its cost/cooldown/school from
  `almanac_core`'s loaded `SpellDefinition` when present, falling back to hardcoded defaults.
- `starlight_focus` item: casts Starlight Bolt on use (no engraving system yet — hardcoded),
  respects cooldown.
- Essence HUD overlay (top-left text, visible only while holding the focus).
- Debug command `/majestic status`.
- Datagen scaffolding (`DataGenerators`) + hand-written fallback JSON for the four spell
  definitions, the focus item model, and `en_us` lang (datagen's own `data()` run type was broken
  in all four ecosystem repos — see Fixed below — so this milestone's content was written by hand
  as an accepted fallback; `DataGenerators` is ready to take over once verified).

### Fixed
- `runs.data` used `clientData()`, which doesn't exist on `net.neoforged.moddev` 2.0.142 — replaced
  with `data()` (same fix applied across all four ecosystem repos).
- Spell JSON was initially written to the wrong path (`data/majestic/spells/`) — `almanac_core`'s
  loader reads `data/majestic/almanac/spell/`; moved to the correct path (it would otherwise have
  silently never loaded, with no error).
- `Majestic`'s constructor duplicated `astral_core`'s own `NewRegistryEvent` registration for the
  same custom registry key — removed (only `astral_core` should create it).
- `MajesticSpells` resolved (`.get()`) its `SpellType` `DeferredHolder`s eagerly in a static
  initializer, before `astral_core`'s registry was bound (`IllegalStateException: Registry not
  present`) — fields now stay unresolved holders, `.get()` only at actual usage time.
- Null-checked `Targeting.raycast()`'s result in `StarlightBoltSpell` (could NPE with no target in
  range).

### Notes
- No ritual/engraving system yet — the focus always casts Starlight Bolt. No dimension,
  structures or bosses (`expedition_core` not wired into `majestic` yet). No creative-mode recipe
  for the focus (`/give` or the creative inventory only).
- Implemented via OpenCode, independently verified by Claude: clean build + dedicated-server boot
  with `astral_core` + `almanac_core` + `majestic` loaded together. See
  `docs/DESIGN_MAJESTIC_1-21-1.md §6` (Historial) for the full account of bugs found and fixed.
