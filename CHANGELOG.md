# Changelog

All notable changes to this project will be documented in this file.

## [0.0.0]

### Added
- Initial project setup for **Minecraft 1.21.1 / NeoForge 21.1.249** (Java 21): build
  (`net.neoforged.moddev` 2.0.142), Parchment `2024.11.17`. Maven Central + GeckoLib + BlameJared
  repos declared for later dependencies.
- Empty `@Mod` entry point (`com.skd.majestic.Majestic`). No content wired yet.
- Full design documentation under `docs/` (`DESIGN_ECOSYSTEM.md`, `PROGRESSION.md`,
  `CONTENT_MAGIC.md`, `CONTENT_WORLD.md`, `INTEGRATIONS.md`, `LORE.md`,
  `DESIGN_MAJESTIC_1-21-1.md`).

### Notes
- Majestic is **All Rights Reserved**; the repo is private and has no public `main` mirror.
- Content (dimension, biomes, structures, bosses, spells, rituals, research, relics, questline,
  guide) is added milestone by milestone, all via datagen. See `docs/PROGRESSION.md`.
- Depends on `astral_core`, `expedition_core`, `almanac_core` and GeckoLib — wired in later
  milestones, always as external jars, never bundled.
