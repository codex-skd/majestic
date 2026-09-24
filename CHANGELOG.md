# Changelog

All notable changes to this project will be documented in this file.

## [0.0.0-beta.14]

### Added
- **El Viaje (Journey I)**, la guía como viaje real:
  - Nueva categoría del Almanaque **El Viaje** con un **Prólogo** siempre visible (un acertijo que apunta a los
    Vigías caídos) y dos capítulos que se desbloquean con hojas del diario de la Orden.
  - **Hoja del comienzo**: la suelta el Vigía caído (garantizada mientras el jugador no haya leído el
    Capítulo 1). Usarla con el Almanaque en el inventario desbloquea **Capítulo 1 — Los Santuarios caídos**.
  - **Hoja del santuario**: garantizada en el cofre del Santuario caído. Con el Capítulo 1 leído, desbloquea
    **Capítulo 2 — El Observatorio** (y su guardián). Solo afecta a quien la usa.
- **4 mobs nocturnos** que aparecen de noche en el Overworld: **Vigía caído** (cuerpo a cuerpo, no-muerto, arde
  al sol), **Cultista astrólogo** (lanza rayos de luz estelar a distancia), **Reptador meteórico** (trepa paredes)
  y **Polilla umbría** (vuela y se lanza en picado). Con huevos de spawn y botín propio.
- Modelos de los 4 mobs y texturas de las hojas pendientes del taller (`docs/JOURNEY_I_TALLER_GUIDE.md`): hasta
  entonces los mobs son invisibles y las hojas se ven sin textura.



### Changed
- **Guardián de la Puerta, combate v2** con las animaciones v2 del taller (`taller_minecraft` `961ea2d`):
  - Tres ataques elegidos según la situación: **barrido** horizontal (arco de 140°, golpea a todos los de
    delante), **golpe descendente** con onda en área (con 2+ jugadores cerca) y **estocada** (objetivo a
    3–4,8 bloques). El daño llega en el fotograma del impacto (ticks 9 / 14 / 7), no al empezar el golpe.
  - Idle y caminata más animados; en fase 2 **corre** (+30 % velocidad).
  - Fase 2: **embestida avisada** cada 10 s (preparación de 0,8 s con línea de partículas en el suelo,
    carga en línea recta, aturdido 1 s si choca contra una pared). Nunca coincide con los pulsos de luz.
- El golpe cuerpo a cuerpo vanilla del jefe se sustituye por la nueva lógica de ataques.



### Added
- **Traducción al español** (`es_es`): todos los ítems, bloques, entidades, pestañas, mensajes, HUD, comando y
  el contenido del libro guía.
- **Pestañas de creativo** "Majestic: Magia" y "Majestic: Mundo" (la de Reliquias llegará con las reliquias).
  Los huevos de spawn también aparecen en la pestaña vanilla de huevos.
- **Ítems del Altar astral y el Pilar astral**: nunca se habían registrado, no se podían tener en el inventario.
- **Libro guía "El Almanaque"**: se entrega la primera vez que el jugador entra al mundo (una sola vez) y se
  fabrica con un libro rodeado de 8 lapislázulis.

### Fixed
- El libro guía no existía en el juego: estaba en `data/majestic/patchouli_books/` (Vellumli lee
  `vellumli_books/`) y sin `use_resource_pack`, que Vellumli exige (lanzaba excepción y lo descartaba).
  Ahora `book.json` en `data/majestic/vellumli_books/almanac/` y el contenido en `assets/`.
- Desbloquear el nodo `first_light` sin Vellumli instalado provocaba `NoClassDefFoundError`.
- Textos fijos en inglés (mensajes del foco, comando `/majestic status`, HUD de esencia) pasados a claves de
  traducción.

### Changed
- **Vellumli pasa a ser dependencia obligatoria** (antes no estaba declarada).
- El libro ya no se entrega al desbloquear `first_light` (se da al entrar).



### Added
- Modelo, animaciones y textura GeckoLib del **Constructo astral** (del taller): deja de ser invisible.
- Textura de la **Lente de éter** (del taller).

### Changed
- Quitados los apaños para assets pendientes: el modelo de `ether_lens` vuelve a `basicItem` (datagen
  valida la textura) y el renderer del constructo ya no se salta el dibujado.



### Added
- **Jefe I — Warden of the Gate** (`majestic:warden_of_the_gate`), Acto II. Aparece la primera vez que un
  jugador entra en la arena de un Observatory (uno por estructura). Dos fases: invoca Constructos
  astrales; por debajo del 50 % de vida lanza pulsos de luz telegrafiados que dañan y ciegan a quien
  esté a la vista (las columnas cubren). Barra de jefe, vida/daño escalados por jugadores en la arena,
  reinicio si todos salen, botín por participante y 200 XP. Modelo y animaciones GeckoLib del taller.
- **Constructo astral** (`majestic:astral_construct`), esbirro del jefe. Modelo pendiente del taller
  (hasta entonces es invisible).
- **Lente de éter** (`majestic:ether_lens`), botín garantizado del jefe y llave del Acto III. Textura
  pendiente del taller.
- Huevos de spawn de ambas entidades y logro "Beyond the Gate".



### Added
- Texturas propias para los 9 ítems (`starlight_focus`, `blank_page`, los 4 sigilos, `star_fragment`,
  `altar_blueprint_t2`, `astral_dust`) y los 2 bloques (`astral_altar`, `astral_pillar`), generadas
  y validadas con el pipeline de `taller_minecraft` según `docs/TEXTURE_GUIDE.md`. Se acaba el
  tablero morado/negro de textura ausente.

### Fixed
- El datagen de cliente (blockstates, modelos de ítem/bloque, `en_us`) nunca se había llegado a
  commitear en `src/generated/resources`: ahora se genera y versiona. Los ítems/bloques que no
  tenían nombre traducido (sigilos, fragmento estelar, plano, polvo astral, altar, pilar) ya lo
  tienen.
- Eliminadas las copias a mano de `lang/en_us.json` y `models/item/starlight_focus.json`, que
  duplicaban la salida de datagen y hacían fallar `processResources` (mismo caso que beta.8).

## [0.0.0-beta.8]

### Fixed
- `runData` failed outright: the 4 spell JSON and the `first_light` research node JSON existed both
  as hand-written files (leftover from the alpha, before the datagen path was fixed in beta.3) and
  as datagen output at the same destination path, with no duplicate-handling strategy set. Removed
  the hand-written copies; datagen output (`src/generated/resources`, committed) is now the only
  source.
- `astral_altar`/`astral_pillar` item icons used a flat `basicItem` texture instead of inheriting
  the block's own 3D model — switched to `simpleBlockWithItem`, so one block texture now serves
  both the world model and the inventory icon.

### Notes
- Running `runData` for the first time surfaced a real gap: no PNG texture exists anywhere under
  `assets/majestic/` yet — all 9 items and 2 blocks currently render as the missing-texture
  checkerboard in game. `./gradlew build`/`runServer` never catch this since neither runs datagen's
  texture validation. Full guide for the 11 missing textures in `docs/TEXTURE_GUIDE.md`.

## [0.0.0-beta.7]

### Notes
- No gameplay changes. Expedition Core's CurseForge project was approved after being pending
  review — this release just adds it to the declared CurseForge dependencies, so installing
  Majestic through the app/launcher now installs all four required mods (Astral Core, Almanac
  Core, Expedition Core, GeckoLib) automatically.

## [0.0.0-beta.6]

### Added
- **Observatory** (Act II): a real 5-piece jigsaw structure — entrance, one of two corridor
  variants, a study room, and a boss arena (no boss yet). Generated with the `taller_minecraft`
  pipeline and validated piece-by-piece before being wired in.
- The study room's chest guarantees a **Tier 2 Altar Blueprint** plus spell sigils and Astral Dust;
  the arena's chest holds a small amount of Astral Dust. Two new items: `altar_blueprint_t2` and
  `astral_dust`.

### Notes
- Rarer than Fallen Shrine (larger spacing/separation) — this is meant to feel like a real
  milestone find.
- No mobs or boss yet — the arena is just the physical room for now; Boss I needs GeckoLib work
  that's out of scope for this structures milestone.
- Verified with a real `runServer` boot: clean "Done", no datapack loading errors.

## [0.0.0-beta.5]

### Fixed
- **Fallen Shrine never generated**: its template was shipped in `data/majestic/structures/`, but since
  1.21 templates are loaded from `data/<ns>/structure/` (singular), so `majestic:fallen_shrine/shrine_01`
  was never found. Moved to `data/majestic/structure/fallen_shrine/shrine_01.nbt`.
- The template was a Minecraft 26.2 export (DataVersion 4903); replaced with the 1.21.1 export
  (DataVersion 3955, validated against the 1.21.1 block report) from the `taller_minecraft` repo.
  Same design: ruined shrine, 13×9×14, two chests rolling `majestic:chests/fallen_shrine`.

### Docs
- Structure build guide: correct in-mod path (`structure/`) vs world export path (`generated/<ns>/structures/`).

## [0.0.0-beta.4]

### Added
- **Fallen Shrine** (Act I): the mod's first real structure — a small ruined shrine of the Order
  of Watchers, generated dispersed across forest/savanna/taiga/plains/meadow biomes. Built in-game
  by hand with a Structure Block (see `docs/STRUCTURES_BUILD_GUIDE.md`).
- Its chest drops `star_fragment` (guaranteed — a new item, the Act II key) plus 1-3 blank pages.

### Notes
- Single-piece structure for now — a real multi-piece jigsaw structure (`observatory`, Act II)
  needs hand-built NBT pieces too and is the next structures milestone.
- Implemented directly (JSON datapack content + one item registration, not substantial Java code —
  no OpenCode delegation needed for this one). Verified with a real `runServer` boot (not
  `runGameTestServer`, which never reaches world generation): clean boot, no datapack loading
  errors. Actually finding the structure in an explored chunk still needs in-game play to confirm.

## [0.0.0-beta.3]

### Added
- **Astral Altar (Tier 1)**: a multiblock (altar + 4 astral pillars) that runs the new
  `engrave_spell` ritual. Hold your focus in your main hand and one of the 4 new spell sigils in
  your off-hand, then interact with the altar to engrave that spell onto your focus — requires the
  `first_light` research node and a blank page, and consumes essence.
- **Research**: the `first_light` node, auto-unlocked the first time you successfully cast with
  your focus.
- **Guide book**: `majestic:almanac` (Vellumli), with categories for spells/rituals/research and
  6 entries (the 4 spells, the engrave ritual, and the first_light node). Delivered to the player,
  along with its unlock advancement, when `first_light` is researched.
- New items: `blank_page` and 4 spell sigils (`starlight_{bolt,ward,reveal,surge}_sigil`).
- The focus's cast spell is no longer hardcoded — it now reads a `recorded_spell` data component,
  set by the engrave ritual (defaults to Starlight Bolt for focuses that predate this update).

### Fixed
- `DataGenerators`' spell JSON provider wrote to `data/majestic/spells/`, a path `almanac_core`'s
  loader never reads — fixed to `data/majestic/almanac/spell/` (a bug present since beta.1 alpha,
  masked because the hand-written fallback JSON already lived at the correct path).
- `majestic` was still depending on `almanac_core` beta.1 in `libs/`, predating that library's
  guide bridge (beta.2) — updated, and the guide/advancement wiring now uses the real bridge
  (`EntryGate`, `VellumliBridge`) instead of a hand-rolled workaround.

### Notes
- Tier-1 altar only — Tier 2 (in the future Observatory structure) needs Act II content that
  doesn't exist yet, so it's deferred rather than built half-finished.
- Reagents are checked/consumed from the player's inventory, not from physical pedestals —
  `astral_core`'s pedestal-item scanning isn't implemented yet (a known, documented gap).
- Delegated to OpenCode and independently verified by Claude, who found and fixed the stale
  `almanac_core` dependency above. See `docs/DESIGN_MAJESTIC_1-21-1.md §5b`/Historial for the
  full account, including a recursive-delegation issue in the first two OpenCode attempts.

## [0.0.0-beta.2]

### Added
- **Expedition Core** wired as a real dependency (external jar, never bundled) — now that its
  Milestone 1 (structures + `BossEncounter` framework) is published, Majestic can start building
  Act I–II structures and bosses on top of it.
- **GeckoLib** wired as a real dependency (`implementation`, external — never jar-in-jar), needed
  for Expedition Core's `GeoBossEntity`/`GeoBossRenderer` base once real bosses are authored.
- Both declared `required` in `neoforge.mods.toml`, alongside Astral Core and Almanac Core.
- **CurseForge dependency relations**: the uploaded file now declares Astral Core, Almanac Core,
  Expedition Core and GeckoLib as required dependencies, so installing Majestic through the
  CurseForge app/launcher installs all four automatically.

### Notes
- No new gameplay content in this release — this is the dependency-wiring step ahead of the
  Act I–II structures/bosses milestone. Alpha content is unchanged from beta.1 (Starlight school,
  4 spells, hardcoded focus).
- Verified: clean build + `runGameTestServer` boot with all four ecosystem mods, GeckoLib and
  Regalia Slots API loaded together (mixins from both apply without conflict).

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
