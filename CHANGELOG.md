# Changelog

All notable changes to this project will be documented in this file.

## [0.0.0-beta.52] - 2026-10-09

### Added

- **Comandos de desarrollo para probar el Acto I en creativo.** `/majestic lantern` materializa el Portador de la Linterna en el Portal Arcano más cercano (sin pasar por los gates de Capítulo III / Almanaque / hoja); `/majestic bible` entrega la Biblia del Bestiario directamente (sin pasar por su entrega única por jugador). Como la progresión del Acto I va por logros, antes no se podían forzar estos encuentros desde creativo para testearlos.

### Changed

- `LanternBearerTrigger` ahora registra en el log (debug) qué gate bloquea la aparición normal del farolero, para poder diagnosticarlo. El comportamiento en juego normal no cambia.

### Notes

- Libs: majestic_core 0.0.0-beta.15 (Altar de la construcción: el Almanaque se dibuja ahora en la mitad izquierda del jugador; iconos de arcane_bookshelf centrados). El arreglo del espejado del altar falta por confirmar visualmente en juego.
- El problema de vaciado del interior del Observatorio no entra en esta build.

## [0.0.0-beta.51] - 2026-10-09

### Added

- **The Celestial Swordsmen's Dungeon and the Demon Knight's Dungeon** (`majestic:celestial_dungeon`, `majestic:demon_dungeon`): two buried single-piece structures, lowest layer at Y=-45, from the workshop. Celestial: a quartz-and-gold cathedral with a glass beam well, two colonnaded wings with chambers and galleries, and the Guardian's hall. Demon: a blackstone fortress around a lava abyss with a cell block, a forge and the throne room. Bosses: `majestic_bestiary:celestial_guardian` and `majestic_bestiary:demon_king` (spawners). No shaft to the surface: they are found with `/locate` or by digging.
- **Inner arches of the Portal**: 18 new gaps (stage `depths`) inside the gate: per side 5 celestial stones up the inner edge, 3 curving in, 1 infernal at the peak. They are raised with the **Receptacle of the Depths** (16 celestial + 2 infernal).
- Loot tables for both dungeons. The celestial treasure chest holds 16 celestial stones and the demon treasure chest 4 infernal stones.
- Placing the Bible of the Beasts on the altar now also hands the player the Receptacle of the Depths.

### Changed

- `PortalGaps` / `PortalRebuild` are stage aware: the Order's Receptacle only fills the outer gaps and the Receptacle of the Depths only the inner arches; finishing the arches shows a message and does not drop the island page.
- `max_payload` in `majestic_portal_gaps.json` counts the outer stage only (the validator follows).
- Libs: majestic_core beta.14, majestic_bestiary beta.15.

## [0.0.0-beta.50] - 2026-10-09

### Changed

- The Almanac's page headers no longer repeat the chapter numbering (`strip_entry_numbering`): the index still shows "- II. Phase 2: ...", the page shows "Phase 2: ...". Long chapter names now take two lines in the index instead of running off the page.
- Libs: vellumli 1.0.2.

## [0.0.0-beta.49] - 2026-10-09

From the beta.48 playtest.

### Added

- **Chapters II "The Bible?" and III "In the Deepest Places" (parts 1 and 2)**: revealed by the Construction Altar when the Almanac (left) and the Bible of the Beasts (`majestic_terrain:bestiary_bible`, right) are set on it inside the Portal.
- **Third Carved Keystone** (`key_foot_left`) on the Portal's arch: 15 gaps in total.
- `SkyIslandWater`: schedules fluid ticks for the sky island's waterfalls on generation.

### Changed

- The Lantern-Bearer appears in the centre of the Portal's rune plaza.
- The Observatory and the Arcane Portal use `expedition_core:wide_biome_jigsaw` with a terrain check (height variation and liquid surface).
- The altar spot is next to the Portal's libraries; the lodge is gone from the Almanac, lang and code.
- The Order's furniture items no longer appear in the Majestic creative tab (they live in the Majestic Core tab).
- Libs: majestic_core beta.13, expedition_core beta.9.

## [0.0.0-beta.48]

From the beta.45 playtest (second pass).

### Fixed

- **Portal rebuild landed in the air**: the gap offsets were added to the piece's minimum corner without applying the piece's rotation, so a rotated Portal sent the blocks to the wrong cells. `PortalGaps` now maps offsets with the piece position and rotation (same transform as `StructureTemplate.placeInWorld`); the Lantern Bearer anchors use it too.
- **Observatory merged with the terrain**: `terrain_adaptation` is now `encapsulate`, which clears the terrain inside the structure box (including above the boss arena).
- **Sky island**: waterfalls are now a real source + spill + falling column instead of a still pillar of sources; the tower roofs and the central spire sit directly on their platforms (no one-block gap).

### Changed

- **Portal v3**: the arch is restored (clean masonry, no scaffolding or rubble) with a compass plaza, a dais and a stair. The Receptacle stands on a plinth to the right of the plaza and the Construction Altar niche is in a hall against the north edge.
- **Receptacle**: the 14 blocks now go to 14 rune cells on the front face of the arch (12 Arcane Brick + 2 Carved Keystone) instead of two 1x1x7 slots. The "Phase 1 Complete" chapter says so.
- **Sky island**: the Sky Stalker spawner is removed for now (Demon Knight and Celestial Swordsman stay).

## [0.0.0-beta.47]

### Changed

- **Blocks moved to majestic_core**: Arcane Safe, Astral Pillar, Arcane Bookshelf (x3), Astral Altar, Vault Altar, Construction Altar (+ marker) and Receptacle. Old `majestic:*` ids resolve through registry aliases. What they do for the story stays here as `MajesticOrderHooks`.
- Needs majestic_core 0.0.0-beta.7 and astral_core.
- `altar_pillar` tag moved to the folder 1.21 actually reads: the T1 altar multiblock now recognises its pillars.

## [0.0.0-beta.46]

From the beta.45 playtest (`docs/FEEDBACK_TEST_2026-10-07.md`).

### Fixed

- **Guardian of the Gate loot**: it only registered players it was targeting, so creative mode or a fast kill meant no participants and no page. Hits now register the attacker, and on death every player in the arena is a fallback.
- **Lantern Bearer** now waits at a Portal anchor (`majestic_portal_anchors.json`, validated against `portal_01.nbt`), approaches slowly with line of sight, hands the page over at face height and fades away; state persists across reloads.
- **Construction Altar** refused the Almanac the player carries (`isSameItemSameComponents` against a fresh stack); `JournalPageItem.isAlmanac` compares only the canonical components. Added a window (menu + screen), direct placing with the book in hand and drop on break.
- Receptacle tooltips, creative tab coverage (27/27 items), the achievement title ("The First Phase"), a readable "Phase 1 Complete" chapter, and every mention of `locate` removed from the game and the docs.
- Chapter names use the new format in es and en.

### Changed

- **Receptacle rebuild**: light column, then one `FlyingStoneEntity` at a time on a bezier path with a particle trail; falls back to placing the block if the stone is lost. Full-cube shape.
- Ghost Construction Altar marker in the lodge niche (`construction_altar_marker`, no item); advancement + Almanac page + chat note after taking the altar.
- Art import from the taller: Receptacle, Arcane Safe, bookshelves, Portal and lodge, sky island v3.

### Added

- `arcane_bookshelf_wide` and `arcane_bookshelf_tall` (multiblock, `MultiblockFurnitureBlock`).

Needs majestic_core beta.6 and majestic_bestiary beta.14. Not tested in game.

## [0.0.0-beta.45]

From the beta.44 playtest (`docs/FEEDBACK_TEST_2026-10-06.md`).

### Fixed

- **The Receptacle could not be clicked, and Jade did not see it.** Its shape helper divided by 16 before `Block.box`, which divides again, so the block's shape was 256 times too small; the outline also left the jar's cavity out. It now has a full outline and sits in the creative tab.
- **The sky island never generated.** Its structure had `"biomes": []`, and an empty list means it can never start. It now spawns over the whole Overworld, much closer (spacing 48), with no isolation zone; `/locate structure majestic:sky_island` finds it. The biome validator now rejects an empty list.
- **The Vault's lights rendered magenta and black, and a lantern sat on top of the altar.** The Illumination Stone now floats over the altar the moment it is set, four lanterns stand on the floor at its cardinal points, and the room lights up in about 2 seconds instead of 18 (lantern textures in majestic_core beta.5).
- **The Vault had no creatures.** Gnomes, undead and an arcane mage now spawn in it.
- **The Portal pages looked like a blank sheet.** Both have their own icon now.

### Changed

- **The Receptacle has its own window.** Right-click opens it: one slot for Order's Stones and one for Carved Keystones, with how many are still needed. What you put in is kept; closing it with all fourteen sets them into the Portal, and when it is done it leaves the Page of Beyond the World on the platform.
- **New chapter tree for Chapter III:** "- I. Phase 1: The Search", "- - I. Begin the Rebuilding" (the Vault page, now readable), "- - II. Phase 1 Complete" (the page the Receptacle leaves), "- II. Phase 2: The First Search in the Skies" (on reaching the island) and "- - I. Down to Work". Entering the Vault only gives its achievement.

### Added

- **Rebuilding lodge** on the Portal: a small magic building with arcane bookshelves and a niche in one wall.
- **Arcane Safe**: a strongbox that works like a chest and opens its door. One sits in the sky island's great hall, holding the Construction Altar.
- **Construction Altar**: a two-block lectern. Set it in the lodge's niche and put the Almanac on it: the book lies open, and the next chapter, "Down to Work", opens. It has its own Objects of the Order entry.
- **Arcane Bookshelf**: a stone bookshelf of the Order.

Not tested in game.

## [0.0.0-beta.44]

### Fixed

- **The Receptacle never set a single stone, so Phase One of the Portal could not be finished.** It looked for an open gap that already held the block it was given, and every open gap is air, so it swallowed the first stone, kept it and refused everything after. It now picks the gap by the block that gap wants.
- **The Receptacle could vanish with gaps still open, or lose the stones inside it.** It took one block per click and evaporated about a second after it ran empty, so a player who paused between clicks lost the only way to fill the Portal; its payload was not saved either. Now one click takes as many stones as the Portal still needs, more can be added while it works, the payload is saved with the chunk, and it only consumes itself once all fourteen gaps are closed.
- **Reading a page only gave an achievement for the prologue.** Every chapter now shows a toast the first time its page is read.

### Changed

- **The Lantern-Bearer also hands over the Illumination Stone**, together with his page. The Observatory arena chest keeps its copy.
- **Chapter III reopens when the player enters the Watchers' Vault**, instead of when the page in its deep chest is read.
- **Almanac texts follow the Act I story document:** the Receptacle is explained and no placement order is given, the island is "above the clouds", there is no ladder, and the Spanish text has its accents back.

### Added

- **Objects of the Order**, a new Almanac category with one entry for each item that has a job in Act I: Illumination Stone, Star Fragment, Order's Stone and Carved Keystone. Each one unlocks, with a toast, the first time the item is in the player's inventory, and says what it is, where it comes from and what to do with it.

Not tested in game.

## [0.0.0-beta.43]

### Changed

- **The Almanac cover finally looks like a book.** The previous cover passed every check but, in game, read as a round gold medallion rather than a book: earlier briefs asked each guide cover to have a silhouette of its own, distinct from a plain book, and the only way to get that was to move away from a book's shape. The new cover (`assets/majestic/textures/item/almanac.png`) uses the classic closed-book silhouette seen at 3/4 — tilted cover, light band of page edges, spine and lower board — and keeps the colours and motifs: brass-gold cover, the Order's 8-point star seal and a clasp with a pale blue gem.
- The three guide books in the pack (The Almanac, the Workhand Guide, The Alchemist's Codex) now share the book shape and are told apart by colour, emblem and accessory, the same way vanilla's book, written book and enchanted book are.

Texture only: no Java, no JSON, no new item.

## [0.0.0-beta.42]

### Changed

- **The sky island is a place now.** 41x32x41 became 61x40x61 and 7 959
blocks became 30 573. It had **no water at all** and **no trees** -- the 211 blocks
of "vegetation" were moss and low grass -- and its underside was a solid 41x41 square, which is the flat
platform the brief forbade. It now has a 221-block river that is a single body of water, 21 logs and
273 leaves, and an irregular underside at 2 653 solid cells of 3 721 columns. The waterfall falls from
Y=7 to Y=0 off the `z=58` edge, over air on all sides; the first attempt put it at `z=38`,
inside the foot of the island, so it landed on rock.
- **The three surface structures get a paved apron so a slope cannot swallow them.** The
Portal, the Fallen Shrine and the Observatory's entrance piece gained a flat pad inside the template.
This has to live *inside* the template: all three carry `terrain_adaptation: beard_thin`,
which trims terrain that pokes into a piece, so a pad placed outside gets eaten. The Shrine grew from
21x16x21 to 22x16x22 to fit its margin. The Vault gets none, because a vault is meant to
be buried in rock.
- **The Receptacle's support is now visible.** Seven `carved_keystone` ring the
socket one course above the floor, open on -Z, which is the side the atrium is entered from, so the
socket stays reachable. The cell `(20,1,22)`, its `chiseled_tuff_bricks` support and the
solid floor at Y=1 are all exactly where they were.
- **The wolf's tail is a chain of three bones.** `tail`, `tail_mid`,
`tail_tip`, with more cubes and a taper, so the silhouette holds up close. Measured: the root no
longer overlaps `leg_br` or `leg_bl` in X and Z, and the lowest tail cube sits at
`y=10` instead of `y=0.5`, where it used to reach the floor. The tail now shares the gait's
keyframe grid.
- **The Goliath's shield sits in its hand and its hammer reads at range.** The shield was
drawn by a single cube centred at `(20,30)`, five pixels right of the hand and six above it, which
is what "it is misaligned" was. It is now three cubes -- face, rim, boss -- centred on
`y=24`, the hand. The hammer's head went from 34.6 average luma to 131.3, which puts it 18 luma
above the arm holding it instead of behind it. This arrives with the workshop's rebuilt body, 48 cubes
instead of 71.
- **The Portal page has an illustration.** A 256x256 page showing the three rune rings,
the phase notches and the wax seal, wired into chapter 3 phase 1.

### Known issue, shipped on purpose

> The two Portal page items still draw as a blank sheet. The delivery was a
> 256x256 book illustration, which is a page illustration and not an item icon, so in the inventory
> portal_phase_page and portal_phase_two_page are still indistinguishable from a page
> with nothing on it. This is the report as it was filed, unchanged. validate_almanac.py fails on any
> item model pointing at blank_page and these two are waived by name, with the reason recorded in the
> script, so the gate re-arms by itself when the 16x16 icons arrive.

### Under the hood

- The Portal's expected masonry counts moved from 1419/138/6 to 1523/169/19 to account for the apron and
the collar. The fourteen gaps are checked separately and are still fourteen and still air.
- Two new validators, each checked by breaking the thing it guards:
`validate_structure_biomes.py` and `validate_block_models.py`.

## [0.0.0-beta.41]

### Changed
- **The Almanac has its own cover, drawn as an actual book.** `assets/majestic/textures/item/almanac.png`
  is new art (16×16, 146 opaque px, 9 colours) and `models/item/almanac.json` plus the `"model"` key in
  `data/majestic/vellumli_books/almanac/book.json` point the book at it. No new item, no new registry
  entry, no Java changes. The cover is a thick-spined leather tome with the Order's 8-point star seal
  in muted gold reaching the edges of the coverboard; its silhouette is a lozenge, which is what
  separates it at 1× from the Workhand Guide's page-edge profile and the Codex's gold corners.
- The previous cover was a rounded rectangle that read as a decorated slab rather than a book, so the
  three guide books of the pack were told apart by tint instead of by shape.

## [0.0.0-beta.40]

### Fixed
- **Chunks with the Arcane Portal never generated.** `Feature placement` /
  `IllegalArgumentException: No enum constant StructureMode.load`, naming `majestic:arcane_portal`.
  Two `minecraft:structure_block` markers sat in the middle of the missing voussoirs with
  `"mode": "load"` in their NBT; `SinglePoolElement.getDataMarkers` calls `StructureMode.valueOf` on
  that tag and vanilla writes it upper case (`LOAD`, `DATA`), because `Enum.valueOf` is case
  sensitive. Both slots are now plain air and the piece carries no block NBT.
- **The Portal could not have been completed anyway.** A placed structure block is a solid block,
  not air, and `PortalGaps.resolve` requires the gap cell to be air — two of the 14 gaps read as
  already filled. Both slots verified as open air across the full 7-deep ring.

### Changed
- The gap validator no longer accepts a `structure_block` placeholder in any mode; it is a hard
  failure with the reason written down. It used to treat a `load`-mode placeholder as empty, which is
  what let the broken piece through.
- `docs/WORKFLOW_MAJESTIC_1-21-1.md` and `docs/ARCANE_PORTAL_TALLER_GUIDE.md` no longer ask the
  workshop for gap markers, and explain why a marker cannot work: it crashes chunk generation and it
  is a solid block where the rebuild needs air.

## [0.0.0-beta.36]

### Fixed
- **Creating a new world crashed.** `worldgen/structure/observers_vault.json` declared
  `expedition_core:wide_biome_jigsaw` without a `spawn_overrides` field. On NeoForge that field is
  **required** on every structure type — vanilla ones included — so the whole datapack failed to load
  and the game died on `WorldLoader.load` the moment `CreateWorldScreen` ran. Audited every
  `worldgen/structure/*.json` in the workspace: the Vault was the only one missing it. The Vault's
  placement, piece and loot are unchanged.

## [0.0.0-beta.35]

### Added
- **The sky island.** A floating rock with a jetty, a castle of four rooms and a tower with a high
  chamber at Y 18, placed alone at Y 200 (spacing 320). Three of the new creatures have spawners on
  it — a Demon Knight in the armoury, a Celestial Swordsman in the great hall, a Sky Stalker in the
  tower's top room.
- Four loot tables for the island's five chests, which pointed at tables that did not exist and would
  all have been empty.

### Fixed
- **The Receptacle rendered with no model.** Its blockstate had six `facing` variants but the block
  has no `facing` property, so no variant ever matched. It is a shaped block, not a cube, and had no
  shape at all — a jar with an open mouth you could not put anything into.

### Under the hood
- The Portal's platform is in: X 18..21 by Z 19..25 at Y 1, with one open cell at (20, 1, 22) for the
  Receptacle. The fourteen gaps in the facade are untouched and now carry a `structure_block`
  placeholder at Z 31 in each run, which the piece replaces with air when it is placed.
- `scripts/validate_portal_gaps.py` learned that a placeholder is an empty cell, and still fails hard
  if one is ever saved in a mode that does not become air.

## [0.0.0-beta.34]

### Added
- **The Order's Receptacle.** It takes the stone the Portal still needs, hands one block to the
  facade every five ticks, and takes itself apart on a fuse once it is empty — the same beat as the
  Astral Altar blowing itself up, so the two read as the same ritual. It refuses anything the Portal
  is not waiting for, so a keystone offered after the facade keystone is in place is declined rather
  than swallowed and lost.
- Closing the last gap ends the Portal's first phase: achievement, a burst of portal particles, and a
  page that opens what is still Act I — the second phase.

### Changed
- "La Búsqueda" is now a subchapter of chapter III instead of sitting behind it, so the order reads
  I, II, III, then its three subchapters.
- **The Vault lights evenly** once the altar is lit, instead of ring by ring from one spot.
- **No mobs spawn in the Vault once it is lit.** The structure no longer overrides them, so a lit
  Vault stops generating.

### Fixed
- **The Vault's phase page could never be read.** It asked for chapter III, but reading the
  Lantern-Bearer's page revokes chapter III and grants the locked title in its place — so the page
  the Vault hands you demanded the chapter it had just taken away. It asks for the locked title now.

### Under the hood
- **The Portal's fourteen missing blocks are data, not guesswork.** Finding them at runtime meant a
  rule like "air with stone around it", and the facade is *stone bricks*, not the Order's masonry —
  so the rule matched the wrong cells. They now live in `data/majestic/majestic_portal_gaps.json`, and
  `scripts/validate_portal_gaps.py` checks every one of them against `portal_01.nbt`. If the Portal is
  regenerated and a gap moves, that script fails instead of the rebuild quietly breaking in front of a
  player who has already gathered the stone.

## [0.0.0-beta.33]

### Fixed
- **The Vault Altar rendered as a magenta cube.** The block and its texture were in place but no
  blockstate was ever generated for it, so there was nothing to draw. Added to the blockstate provider.
- **Placing the Illumination Stone did nothing.** The Vault piece places the altar as a plain block, so
  no block entity came with it and there was nothing to store the stone in. The altar now creates its own
  when a player uses it, which also means the workshop does not have to regenerate the whole structure
  just to embed an id in one block's NBT.
- **The Lantern-Bearer could materialise inside the Portal's masonry.** The Portal is a solid
  41-block structure and the player is usually standing within it, so a fixed eight-block offset often
  landed in stone — which is why it looked wrong and never seemed to come close. Candidate spots are
  now tried in a ring and the first one in open air wins, nearest first.
- Spanish players saw the sealed-structure warning in English: expedition_core shipped only `en_us`.
  Added its `es_es`.

## [0.0.0-beta.32]

### Fixed
- **The Almanac had the same cover as every other guide book in the pack.** The book is a single
  shared item (`vellumli:guide_book`) identified by a data component rather than by its own registry
  entry, and its `book.json` never declared a model, so Vellumli always fell back to its default
  `vellumli:book_brown` cover. The Almanac rendered with the exact same book as the Workhand Guide
  and the Alchemist's Codex, and the EMC value in its tooltip belonged to the shared item rather
  than to this book. It now declares its own model and ships its own cover: cold stone and
  parchment with the Order's eight-pointed star seal in muted gold and two pixels of astral blue.

### Added
- `assets/majestic/textures/item/almanac.png` and `models/item/almanac.json` — the Almanac cover,
  in the existing set's palette. No new item, no new registry entry, no Java.

## [0.0.0-beta.31]

### Added
- **The Goblin's Club** and **the Double Axe**, craftable, so the player can own a copy of what the
  bestiary carries. The creatures hold these as part of their models rather than as items, so these are
  the trophy copies: a dark-oak shaft with a banded head, and two heads on one haft.
- The Watchers' Vault piece now has the altar in it, at the exact centre, so the Stone of Illumination
  finally has something to be set into. Verified: the seven chests, the Order's stone counts and the
  solid floor at y=0 across all 1369 cells are unchanged.
- Textures for the Vault Altar and the Stone of Illumination, so neither shows as missing any more, and
  the Stone finally gets its generated item model.

## [0.0.0-beta.30]

### Added
- **A Structures chapter in the Almanac.** One entry per place — the Fallen Shrines, the Observatory, the
  Arcane Portal and the Watchers' Vault — each hidden until you actually set foot in it, so the chapter
  fills itself as you play instead of listing what you have not found yet. Gated on the structure
  advancements that already existed, so this needed no new trigger logic.

## [0.0.0-beta.29]

### Changed
- **The Vault's chests stay shut until its altar is lit.** Walking into the pitch-dark hall and
  stripping it before ever lighting it is no longer possible. Only chests inside a generated Vault are
  affected; every other chest in the world, including the one holding the Stone, is untouched.
  Refusing the interaction is the same policy the shrines use.

### Notes
- **This cannot deadlock.** The stone comes from the Observatory, not from the Vault, and the Vault's
  mouth is blocked with rubble rather than bedrock, so a player can always dig in, set the stone and
  open the chests.
- Still missing: the altar is not obtainable yet. It has to be placed in the vault piece by the
  workshop, which is why nothing here can be tested in game until then. Specified in
  `docs/ENCARGOS_PENDIENTES_ACTO_I.md`, top item.

## [0.0.0-beta.28]

### Added
- **The Vault Altar**, its own block rather than a reuse of the Astral Altar: the two share no
  behaviour, and piling unrelated uses onto one block is how it ends up with rules nobody documented.
- **The Stone of Illumination.** It sits in the Observatory arena chest, which until now was empty.
  Set into the altar it seals there permanently — you cannot take it back out, or you could light the
  Vault, take the reward and un-light it.
- **The Vault lights up.** The stone floats above the altar drawn as a held item, bobbing, spinning
  and tilted, and the hall is lit a column at a time outward from it. Lighting is done with real soul
  lanterns on top of each column, found by scanning for vertical runs of the Order's masonry, because
  vanilla light does not propagate from a source you move around.
- Almanac subchapters are now marked as such and ordered correctly.

### Fixed
- The Almanac's subchapter is now named and sorted consistently: chapters read `I.`, `II.`, `III.`,
  the subchapter is prefixed with a dash, and "The Search" sits between Chapter II and Chapter III
  instead of after Chapter III.

### Known
- The altar block and the Stone of Illumination have **no textures yet** — they are owed by workshop
  order `SEGUNDO_ENCARGO_ACTO_I.md` and will show as missing until it lands. The Stone's item model is
  not even generated, because datagen refuses to make one while its texture is absent; the line to add
  back is left in `DataGenerators` with a note.

## [0.0.0-beta.27]

### Fixed
- **Almanac chapter order.** `chapter_2_1` had `sortnum: 3` and `chapter_3` had `2`, so "The Search"
  sorted *after* Chapter III. It now sits between Chapter II and Chapter III.
- **The Lantern-Bearer kept returning.** The trigger checked the advancement but not the inventory,
  so a player still carrying the page it had handed over was visited again. It now stays away while
  the page is carried.
- **The Lantern-Bearer's name rendered as a translation key.** Now named, and the name is visible.
- **The Lantern-Bearer could snag on trees and rubble** and end up stuck facing a wall. It no longer
  collides with the world, which costs nothing for something that hovers and already steered its own
  height.
- **The Lantern-Bearer often arrived backwards**: the approach was nearly twice as fast and the look
  control lagged a tick, so it turned to face the player only after stopping. Slower, and it faces the
  player for the whole crossing.

### Added
- **Achievement when the Lantern-Bearer hands over the page**, granted at the handoff rather than on
  sight.
- **Achievements for entering the Arcane Portal and the Watchers' Vault**, via vanilla `minecraft:location`
  triggers with a structure predicate — no code needed, and the Vault never announced itself before.
  These are the basis for the Almanac's structures chapter planned for a later build.

### Known, not yet fixed
- Subchapter numbering is still inconsistent (Roman chapters, "2.1" subchapter).
- Mob work specified and waiting on the workshop: goblin and draconid wield weapons like staffs, the
  tauren's shield is misplaced, the elf's bow is straight with a poor draw animation, the dire wolf
  reads as a split body, and the giant/goliath need a general render pass.

## [0.0.0-beta.26]

### Fixed
- **The Almanac's opening text is legible.** The cover page is 116x156 px with 9 px lines and the
  engine auto-scales text to fit; the old prologue needed ~20 lines and was shrunk past readability
  with the lines overlapping. Now short enough to render at full size. The Spanish file also carried
  **two** definitions of that key, one a single line — duplicate removed.
- **The Prologue is a real entry, unlocked from the start.** It existed only as cover text, so it
  could never be listed or paginated. It is now the first Act I entry with no advancement attached,
  and the engine treats an entry without one as never locked. Since a category is locked only while
  every entry in it is locked, this also unlocks the Act I category, which was required to reach the
  Prologue. Chapters stay individually locked, so the act's strict order is unchanged.
- **An out-of-order page names the chapter it needs** instead of "something comes before it".

### Notes
- The three reported symptoms were one problem: unreadable instructions, then a dead end on a page
  found before the page that unlocks it.

## [0.0.0-beta.25]

### Changed
- **One place to declare structures.** `StructureKeys` holds every generated structure with its id and
  whether it starts sealed; `Majestic` makes a single call instead of a line per structure, and the
  Portal and the Vault are explicitly declared unsealed. The Vault had no entry at all before, so it
  was invisible to the code that manages the others.
- **One shared structure lookup.** Finding the generated instance containing a position — the registry
  lookup plus the walk over piece bounding boxes — was written out three times: the Warden's arena,
  the shrine's unseal and the Lantern-Bearer's trigger. Now one helper in `StructureKeys`.

### Notes
- No gameplay change. Protection behaviour verified unchanged: the Fallen Shrine and the Observatory
  start sealed, the Portal and the Vault do not.
- Scope stays Act I only until the act is signed off.

## [0.0.0-beta.24]

### Added
- **The Watchers' Vault**, the Order's underground warehouse and the last piece of Act I: a 37x21x37
  hall cut into living rock, 52 blocks down, entered by digging out a collapsed passage. Floor solid at
  local y=0 across the whole footprint, four layers of rock overhead, 945 walkable cells reachable in
  one BFS from the entrance. The Order's stone stands at the back as a stepped pile of 80 blocks —
  the same blocks that go back into the Portal.
- **Seven chests**, empty and sealed in niches. Three at the back hold the Order's stone, three by the
  entrance hold provisions, one to the side rewards exploring. `vault_deep_1` alone guarantees exactly
  the 14 blocks the Portal needs, so no player can be locked out of finishing the act.
- **The Lantern-Bearer, finished**: 12 bones, 46 cubes, `geometry.lantern_bearer`, five animations
  named to match the phase machine.
- Release notes at `docs/curseforge/versions/0.0.0-beta.24.md`.

### Changed
- **`MajesticGeoRenderer`**, mirroring what `majestic_bestiary` already did: an entity whose model or
  animation is not baked is skipped rather than crashing GeckoLib's asset lookup. Models are built in
  another repo and land separately, so "code present, art absent" is a normal in-between state. Applies
  to the Astral Construct and the Lantern-Bearer; the Warden is untouched, as it uses
  `expedition_core`'s `GeoBossRenderer`. Shadow radius stays optional, so adopting it changes nothing
  about how an entity already looks.
- The Lantern-Bearer casts a 0.6-radius shadow, matching a person-sized figure.

### Scope
- Work is confined to Act I until it is signed off. The Celestial Plane and the rest of v1 are parked.

## [0.0.0-beta.23]

### Added
- **The Arcane Portal**: a ruined structure generated in the world with isolated spacing, spawning
  the bestiary's creatures day and night. Its facade carries two 1x1x7 gaps, left empty.
- **Three blocks of the Order** — Arcane Brick, Rune Block, Carved Keystone — with textures, and with
  no crafting recipe by design. Nothing in the Portal drops them.
- **The Lantern-Bearer**: the hooded spirit that waits at the Portal, floats over when a player walks
  in, hands over the page and dissolves. No AI goals, cannot be hurt or pushed, drops nothing, once
  per Portal per player, recorded when the page actually changes hands.
- **The Watchers' Vault**: worldgen for the Order's underground warehouse — `underground_structures`,
  pinned to absolute y=0, `terrain_adaptation: bury`, bestiary spawns that ignore light. The structure
  piece itself is still being built, so it will start appearing once it lands.
- Release notes at `docs/curseforge/versions/0.0.0-beta.23.md`.

### Fixed
- **Chapter III no longer disappears from the Almanac.** Reading the Lantern-Bearer's page locks that
  chapter by ungranting the old entry and granting its twin, the one titled
  "III. The Sleeping Portal (Locked)". Only the ungranting was firing, so the chapter vanished instead
  of being replaced. `JournalPageItem` now grants the additional chapters a swap needs, before the
  revoke, so a partial failure cannot leave the player with neither entry.
- **A stale page burns in Creative too**, instead of remaining in the inventory as an inert item.
- **Structures now honour absolute depth.** The Vault sits at y=0 and stays buried rather than being
  projected to the surface, achieved by omitting `project_start_to_heightmap` — no change needed in
  `expedition_core`, which corrects an earlier assumption of mine.

### Changed
- Almanac Act I illustrations now match the generated world (Portal and Vault).
- Workshop orders for the Lantern-Bearer and the Vault, with their reference art, copied into
  `taller_minecraft` so both jobs can proceed without cross-repo paths.

## [0.0.0-beta.22]

### Changed
- **The Almanac now reveals Act I in strict order.** Each chapter is hidden until the page that
  unlocks it is read, and the Act I category itself only appears once its first chapter does. You
  cannot jump to Chapter II without having found Chapter I.
- **The Prologue is no longer a chapter**: it is now the Almanac's opening page, so it is always there
  to read but never listed in the index.
- **Chapter III now changes its title once you know enough.** After the lantern-bearer's page, the
  entry you have been reading is replaced by its locked counterpart, titled "III. The Sleeping Portal
  (Locked)" / "III. El Portal dormido (Bloqueado)" — the literal is part of the book, not a
  placeholder, and 2.1 — The Search appears beside it.
- **New: The Warden's Page** (dropped by the Warden of the Gate, guaranteed for everyone in the
  fight) and **the Lantern-Bearer's Page**. Together they open the two new Act I chapters.

### Added
- **Chapter III — The Sleeping Portal** and **2.1 — The Search**, in English and Spanish, plus a
  locked counterpart of Chapter III carrying the Order's last entry.
- A new top-level **Act I — Awakening** section in the Almanac.
- `expedition_core`: `AdvancementHooks.revoke(...)` — the counterpart of `grant`, for mods that need
  to take a hidden chapter back once its replacement is unlocked.

### Fixed
- Chapters I and II are no longer marked as read before you have read them.
- **A journal page you arrive too late for now burns.** If its chapter is already behind your
  progress, the page catches fire in your hand, the screen says so, and what is left behind is a
  **Residue of Ash** hanging around you for a minute. It currently does nothing at all — that is
  deliberate, and it is there to be given a purpose later. The page is always consumed. A page for
  the chapter you are about to unlock still reads normally.

### Added
- **New Almanac art**: the Warden's Page and the Lantern-Bearer's Page, plus two new illustrations —
  the sleeping portal and the Watchers' vault.
- A new status effect, **Residue of Ash**, left in the air by a burned page.
- **Requires Expedition Core 0.0.0-beta.6 or newer** (new `AdvancementHooks.revoke`), now enforced with
  a version range so an older library is reported at start-up instead of failing later.

## [0.0.0-beta.21]

### Changed
- **El Altar astral se rompe al caer el Guardián**: al morir, el altar que lo invocó emite humo y fuego
  de alma durante 5 segundos (aviso a pantalla completa con cuenta atrás) y estalla en una esfera de
  **4 bloques de radio**. Daña y empuja a quien se quede (9 de daño en el centro, menor cuanto más lejos).
- **La derrota del Guardián es definitiva**: el altar **no dropea**, así que ya no se puede volver a
  invocar reaplicando un fragmento estelar. El resto de la estructura sí suelta escombro.

### Fixed
- **Un hechizo fallado ya no bloquea el foco**: si el disparo falla (por ejemplo, sin objetivo), el
  hechizo entraba en recarga igualmente. Ahora solo recarga si el lanzamiento se resuelve.
- La **Primera Luz** ya no se concede al intentar usar el foco, sino al lanzar un hechizo con éxito.

### Added
- Aviso a pantalla completa (`EL ALTAR SE ROMPE` / `Get away!`) durante la cuenta atrás de la explosión.
- Mensajes de error de hechizo **traducibles** (`No target`, `Not enough essence`, ticks de recarga).
  Los motivos que no son claves de traducción se muestran tal cual, para que los hechizos de terceros
  sigan funcionando.

### Changed (technical)
- El **multiblock del altar** está definido en un único sitio (`MajesticBlocks`) en lugar de estar
  duplicado en tres clases.
- El JSON de hechizos, rituales y nodos del Almanaque se genera con los **codecs de almanac_core**, así
  que el esquema lo define la librería y no puede desviarse en silencio.
- `night_mobs.json` → `overworld_mobs.json`: el nombre era engañoso, el biome modifier no controla la
  hora del día (lo hace cada criatura por su propia regla de aparición).

### Removed
- Código sin uso: bases de render de GeckoLib sin entidades asociadas, el `SavedData` de arenas que ya
  no se usaba, la carpeta vacía de mobs nocturna y un tag de entidad vacío.
- Documentado: los **mobs de apoyo no sueltan botín** a propósito (el Guardián y el Constructo son
  narrativa, no economía).

## [0.0.0-beta.20]

### Added
- **Iconos en Xaero's Minimap** para el Guardián de la Puerta y el Constructo astral (cara en vez de punto).

### Changed
- Majestic Bestiary 0.0.0-beta.5: arte nuevo de las 12 criaturas (pase 2 del taller) e iconos de minimapa.

## [0.0.0-beta.19]

### Fixed
- **El Guardián revivía al llegar a 0 de vida** (temblaba y volvía con más fuerza): el combate seguía activo
  durante la animación de muerte y volvía a engancharse al jugador, reponiendo la vida. Ahora muere de verdad.
- **Orden del Almanaque**: los JSON usaban `sort_num` y Vellumli lee `sortnum`; el Prólogo salía el último.
  Corregido en las 48 entradas/categorías.
- Retratos del Bestiario del lobo, el dracónido y el gnomo recortados de nuevo (se cortaba la cara).
- **Dracónidos en el Observatorio**: apenas aparecían; peso 1→3 y zona de aparición en toda la estructura.

### Changed
- **Santuario y Observatorio aislados**: colocación `expedition_core:isolated_spread` (5 y 6 chunks), ya no
  aparecen pegados a otras estructuras.
- Dependencias: Expedition Core 0.0.0-beta.5, Majestic Bestiary 0.0.0-beta.4.

## [0.0.0-beta.18]

### Added
- **Majestic Bestiary** 0.0.0-beta.3 como dependencia obligatoria (nueva librería de criaturas, CF 1716117).
- **Nuevos mobs nocturnos** (sustituyen a los 4 anteriores): No muerto, Goblin, Lobo terrible y Sátiro. El No
  muerto hereda la *Página del Comienzo* garantizada para quien no ha leído el capítulo I.
- **Habitantes de estructuras** (`spawn_overrides`): Gnomo y Elfo en el Santuario caído; Mago arcano y Dracónido
  en el Observatorio.
- **Bestiario** en el Almanaque: categoría nueva con 10 entradas (8 criaturas con retrato de su hoja conceptual +
  Guardián + Constructo astral), cada una se desbloquea al matar la criatura por primera vez.
- **Invocación del Guardián** con un *fragmento estelar* sobre el altar del centro de la arena (ya no aparece al
  entrar). Si muere un jugador del combate o todos abandonan la arena, se retira entre carcajadas y devuelve el
  fragmento al altar.
- **Estructuras selladas** (Expedition Core beta.3): el Santuario no se puede picar ni construir hasta abrir su
  cofre; el Observatorio, hasta derrotar al Guardián.
- Restos del suelo al golpear con el báculo (golpe fuerte + marcas `staff_tap` de la animación).

### Changed
- **Santuario caído v2** (taller): complejo de 21×16×21, cofre en nicho sellado (ya no se abre desde fuera).
- **Observatorio v2** (taller): piezas más monumentales y arena de 33×20×33 con el altar en el centro.
- Imágenes del Almanaque del Santuario y del Observatorio regeneradas con las estructuras v2 (taller beta.28).
- **Altar y pilar astral** con modelo propio (pedestal-mesa y columna) y forma de colisión real.

### Removed
- Mobs `fallen_watcher`, `stargazer_cultist`, `meteor_crawler`, `umbral_moth` (y su proyectil).
- Ítems sin uso: `altar_blueprint_t2`, `astral_dust`, `ether_lens` (el botín del Guardián da diamantes y
  frascos de experiencia en su lugar).

## [0.0.0-beta.17]

### Changed
- **Guardián de la Puerta**: nuevo modelo con el báculo agarrado con el puño (vertical, cristal arriba) y
  animaciones más marcadas (barrido, golpe, estocada, caminar y correr). Mismos nombres y tiempos de impacto.
- **Santuario caído** rediseñado: capilla en ruinas de 15×12×15 con arcos rotos, nicho del altar con la estrella
  de la Orden y un único cofre.
- **Observatorio** más elaborado: entrada monumental de 13×18×11 (dos torres de toba, estrella sobre la puerta,
  instrumento roto en lo alto) y más detalle en pasillos, estudio y arena. Mismas piezas, conectores y botín.

### Added
- **Imágenes en el Almanaque**: el Capítulo I muestra un Santuario caído y el Capítulo II la puerta del
  Observatorio, para saber qué buscar.



### Added
- Árbol de logros **"El viaje arcano"** visible en la pantalla de logros: se abre al entrar al mundo por primera
  vez; "Ruinas de la Orden" al entrar en un Santuario caído; "Donde se leían las estrellas" al llegar al
  Observatorio; el logro del Guardián de la Puerta cuelga ahora de este árbol (antes quedaba oculto).

### Fixed
- El **Altar astral** era invisible al colocarlo (`BaseEntityBlock` se dibuja invisible por defecto en 1.21.1).
- Los capítulos del Viaje se desordenaban (Vellumli pone primero las entradas no leídas): ahora se ordenan
  siempre por número (`read_by_default`).



### Added
- Modelos, animaciones y texturas del taller (`taller_minecraft` `e1d5454`) para los 4 mobs nocturnos: ya no son
  invisibles.
- Texturas de la **Hoja del comienzo** y la **Hoja del santuario**.

### Changed
- **Santuario caído** con un solo cofre (el que tenía un bloque encima se ha quitado): una Hoja del santuario por
  estructura.
- El daño del Vigía caído y del Reptador meteórico llega en el fotograma del impacto (0,3 s / 0,25 s); la Polilla
  umbría anima el picado al empezar a bajar, no al tocar.
- Retirado el apaño de textura pendiente de las hojas (datagen vuelve a validarlas).



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
