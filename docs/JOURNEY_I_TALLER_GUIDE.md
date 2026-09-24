# Majestic — Journey I: taller work order (night mobs, journal pages, shrine fix)

> Work order for `taller_minecraft` for the first stretch of Majestic's guided journey
> (night mobs → *Page of the Beginning* → Chapter 1 → Fallen Shrine → *Shrine Page* → Chapter 2 → Observatory).
>
> **Delivery**: produce and validate everything inside `taller_minecraft` only, in the usual output folders
> (`entities/<id>/output/1.21.1/...`, `textures/majestic/output/1.21.1/...`, `structures/shrine/output/1.21.1/...`),
> each with its validation file, previews and README. Do not copy anything into majestic — majestic pulls the
> files from there. Majestic's code already references the exact ids and paths below.
>
> Target: Minecraft 1.21.1, GeckoLib 4.7.6, majestic's shared palette (see `TEXTURE_GUIDE.md §0`): cold grey
> stone / tuff, parchment beige, muted gold `#d4af6a`, pale astral blue `#a8c8e8`; "physical" parts muted, "magic"
> parts gold or pale blue.

---

## 1. Four night mobs (GeckoLib models)

They spawn at night on the Overworld surface like zombies and skeletons. They are the first thing a new player
meets from Majestic, so each must be readable at a glance and clearly "of this mod": remnants of the **Order of
Watchers** (the Warden of the Gate's order) and creatures warped by fallen starlight.

Same conventions as the Warden and the Astral Construct: one `.geo.json`, one `.animation.json`, one texture per
mob; identifier `geometry.<id>`; animation names `animation.<id>.<action>`; validation with `tools/geo_check.py`.

Output per mob: `entities/<id>/output/1.21.1/assets/majestic/{geo/<id>.geo.json, animations/<id>.animation.json,
textures/entity/<id>.png}`.

### 1.1 `fallen_watcher` — Fallen Watcher (humanoid, melee)
- An undead acolyte of the Order: gaunt humanoid (~1.95 blocks, player-like proportions, hitbox 0.6 × 1.95), torn
  night-blue robe with a faded gold hem, a hood, pale grey skin with faint pale-blue cracks, and a **dead lantern**
  hanging from its belt. It carried the Order's journal — it drops the *Page of the Beginning*.
- Bones: `root`, `body`, `head`, `hood` (child of head), `right_arm`, `left_arm`, `right_leg`, `left_leg`,
  `robe` (lower robe flap, child of body, sways), `lantern` (child of body).
- Texture 64×64.
- Animations: `idle` (loop, 2.5 s — slow sway, head tilts, lantern swings), `walk` (loop, 1.0 s — shambling,
  slightly hunched, arms half raised like a zombie but less stiff), `attack` (once, 0.6 s — two-handed claw swipe,
  impact at **0.3 s**), `death` (hold last frame, 1.2 s — kneels and collapses; the lantern falls).

### 1.2 `stargazer_cultist` — Stargazer Cultist (humanoid, ranged caster)
- A living cultist who stole the Order's star-lore: humanoid (~1.95 blocks, hitbox 0.6 × 1.95), dark robe with a
  **star-chart pattern** (pale-blue dots and thin gold lines), a tall pointed cowl hiding the face except two
  glowing pale-blue eyes, one hand wrapped in bandages that glow when casting. It is the "skeleton" of the group:
  keeps its distance and throws starlight bolts.
- Bones: `root`, `body`, `head`, `cowl` (child of head), `right_arm`, `left_arm`, `right_leg`, `left_leg`, `robe`.
- Texture 64×64.
- Animations: `idle` (loop, 2.5 s), `walk` (loop, 1.0 s — upright, measured steps), `cast` (once, 0.8 s — raises
  the right hand, draws it back and flings it forward; projectile released at **0.5 s**), `death` (hold last frame,
  1.2 s — staggers back and falls).

### 1.3 `meteor_crawler` — Meteor Crawler (arthropod, wall climber)
- A creature grown from a shard of fallen meteorite: low, wide, six-legged body (hitbox 1.2 × 0.7) of dark
  scorched rock (`#2b2b30`–`#4a4a52`) with **glowing pale-blue and gold veins** and a cluster of crystal spikes on
  its back. Plays the spider's role (climbs walls).
- Bones: `root`, `body`, `head`, `mandibles` (child of head), `spikes` (child of body), legs `leg_fr`, `leg_fl`,
  `leg_mr`, `leg_ml`, `leg_br`, `leg_bl` (front/middle/back, right/left).
- Texture 64×64.
- Animations: `idle` (loop, 2.0 s — spikes pulse, mandibles twitch), `walk` (loop, 0.6 s — fast alternating
  tripod gait), `attack` (once, 0.5 s — lunging bite, impact at **0.25 s**), `death` (hold last frame, 1.0 s — legs
  curl in, the vein glow fades).
- Climbing is done in code (no separate climbing animation needed; `walk` is reused).

### 1.4 `umbral_moth` — Umbral Moth (flyer)
- A large moth drawn to starlight: body ~1.0 block long, wingspan ~2.2 blocks (hitbox 0.9 × 0.5). Dusky
  violet-grey fuzzy body, wings dark with **pale-blue eye-spots** and a faint gold dust edge; feathery antennae.
  Plays the phantom's role (swoops down at players at night).
- Bones: `root`, `body`, `head`, `antennae` (child of head), `wing_left`, `wing_right`, `wing_left_tip`
  (child of wing_left), `wing_right_tip` (child of wing_right).
- Texture 64×64.
- Animations: `fly` (loop, 0.6 s — full wing beats), `glide` (loop, 1.5 s — wings mostly spread, slow flutter),
  `swoop` (once, 0.8 s — wings fold back for the dive, impact at **0.5 s**, then flare open), `death` (hold last
  frame, 1.0 s — wings crumple, spirals down).

### 1.5 Validation
For each mob, the usual check, e.g.:
```bash
python tools/geo_check.py entities/fallen_watcher/output/1.21.1/assets/majestic fallen_watcher \
  --bones root,body,head,hood,right_arm,left_arm,right_leg,left_leg,robe,lantern \
  --anims animation.fallen_watcher.idle,animation.fallen_watcher.walk,animation.fallen_watcher.attack,animation.fallen_watcher.death
```
Put each attack/cast/swoop "impact at" time in the README table (code syncs damage/projectile release to it).

---

## 2. Two journal page items (16×16, same format as `TEXTURE_GUIDE.md §0`)

Output: `textures/majestic/output/1.21.1/assets/majestic/textures/item/<id>.png` (+ validation file).

### `beginning_page.png` — *Page of the Beginning*
A single loose, torn journal page: aged parchment (`#d8c9a3` / `#b8a67e`), one ragged edge, a few faint dark
handwriting lines, and a small **8-point star seal** in muted gold at a corner (the Order's mark). Slightly curled.

### `shrine_page.png` — *Shrine Page*
Same page family (must read as "the next page of the same journal"), but with a **pale-blue sketch of a small
ruined shrine** (a few lines suggesting an arch/pillars) instead of plain writing, and the same gold star seal.
It should be distinguishable from `beginning_page` at a glance by the blue sketch.

---

## 3. Fallen Shrine fix (`structures/shrine`)

Current `shrine.nbt` (1.21.1) has **two** lootable chests (`3,2,10` east, `9,2,10` west, both
`majestic:chests/fallen_shrine`). In game one of them has a block directly on top of it, which looks wrong (and a
chest with a solid block above cannot be opened).

- Keep **exactly one** chest: the one **without** a block above it (expected: the archive chest at `3,2,10` beside
  the bookshelf). Remove the other one (expected: `9,2,10`, the one half hidden by fallen masonry) — replace it with
  masonry/air consistent with the surroundings, not a hole.
- Make sure the kept chest has **air directly above it**.
- Loot table stays `majestic:chests/fallen_shrine` (majestic will add the *Shrine Page* to it — one guaranteed per
  shrine, so one chest is exactly what the journey needs).
- Update the README (lootable chests table, the "2 fragments per shrine" note) and the previews; validate as usual.

---

## 4. Done means
`VALIDATION OK` for the 4 entities, the 2 textures and the shrine; previews rendered; READMEs updated with the
animation tables (including impact times). Majestic pulls the files from the taller's output folders.
