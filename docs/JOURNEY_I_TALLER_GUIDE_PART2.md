# Majestic — Journey I, part 2: taller work orders (playtest feedback 2026-09-25)

> Four independent tasks for `taller_minecraft`, from the first playtest of the Journey. Do them in the order
> **A → B → C → D** (D needs the final structures from B and C).
>
> **Delivery** (all tasks): produce and validate inside `taller_minecraft` only, in the usual output folders,
> with validation file, previews and README. Never copy anything into majestic — majestic pulls from there.
> Target Minecraft 1.21.1 (DataVersion 3955), GeckoLib 4.7.6, majestic palette (`TEXTURE_GUIDE.md §0`).

---

## Task A — Warden of the Gate: hold the staff properly (model + animation touch-up)

**Feedback**: in game the staff looks *glued* to the arm, like an extension of the hand, instead of being gripped;
and the animations read as weak — the Warden "moves like an iron golem".

- **Grip**: model a closed hand (a small `right_hand` cube or a fist shape at the end of `right_arm`) wrapped
  around the shaft. The staff must pass *through* the fist: roughly one third of the shaft below the hand, two
  thirds above, crystal on top. In idle the staff stands **vertical** beside the body (tip near the ground, not
  aligned with the forearm). Keep `staff` as a child of `right_arm` with its pivot **at the grip point** so every
  arm rotation swings it naturally.
- Optional but welcome: let the left hand join the staff in two-handed moves (slam, run, charge) — pose only.
- **Amplitude**: re-check v2 with the new grip; exaggerate what reads small at game scale (at 10–15 blocks):
  bigger body twist on the sweep (the staff must visibly cross in front of the body), deeper crouch on the slam,
  longer reach on the thrust, stronger bob and staff sway on walk/run. Keep every animation **name, length and
  impact time unchanged** (code is synced to them: attack 0.45 s, attack_slam 0.70 s, attack_thrust 0.35 s,
  summon spawn 0.6 s).
- Bone names unchanged; `geo_check.py` as in v2. Previews: rest pose (front/side/back) + each animation.

## Task B — Fallen Shrine: a richer structure (`structures/shrine`)

**Feedback**: the shrine is too simple to be a memorable first landmark.

- Keep it a **single template** (`shrine.nbt`, same id `majestic:fallen_shrine/shrine_01`, surface ruin, same
  palette: weathered/mossy/cracked stone brick, andesite, calcite, soul lanterns, chiseled tuff accents).
- Grow it to roughly **15×12×15** (today ~13×8×13): a small ruined chapel of the Order — broken arches, a partly
  collapsed roof or dome ring, an altar niche with the Order's **8-point star** in calcite/tuff on the floor, a
  low outer wall with a gate, fallen masonry and overgrowth. It must read as "the Order was here" from a distance
  (a tall broken arch or bell-less belfry as silhouette).
- **Exactly one lootable chest** (`majestic:chests/fallen_shrine`), in the altar niche, **air above it**,
  reachable, facing the entrance. No other chests/barrels.
- No mobs, no spawners, no command blocks. Structure void where terrain should show.
- Validate and update README + previews (front, top, and a close-up of the chest niche).

## Task C — Observatory: richer entrance and pieces (`structures/observatory`)

**Feedback**: the Observatory also feels too simple; the entrance is the image players will look for.

- Keep the jigsaw chain and **all piece ids, jigsaw names/targets and pools** exactly as today
  (`entrance` → `corridor_a|corridor_b` → `study_room` → `arena`), chest loot tables unchanged
  (`observatory_study`, `observatory_arena`). The arena must stay open to the sky and keep its central altar
  area clear for the boss (spawn is at the arena floor centre; tuff columns are gameplay cover for the light
  pulses — keep **at least four** full-height columns).
- **Entrance**: make it a landmark — a monumental gate with two tuff towers or buttresses, a broken
  telescope/armillary sphere on top (iron bars/chains/lightning rods/copper as brass), steps, the Order's star
  above the door, braziers (soul campfires). Recognisable silhouette from ~60 blocks.
- **Corridors/study/arena**: more detail within the same footprint where possible (star maps on the floor,
  bookshelves, lecterns, broken instruments, rubble). Size may grow moderately; keep the chain generating on
  typical plains/forest terrain (`beard_thin`).
- Validate each piece; update README + previews.

## Task D — Guide-book images (after B and C)

The Almanac shows images of what the player must find. Vellumli image pages use a **256×256 PNG** of which the
**top-left 200×200** is displayed (the rest transparent).

| File (output: `textures/majestic/output/1.21.1/assets/majestic/textures/gui/book/`) | Content |
|---|---|
| `fallen_shrine.png` | the **new** Fallen Shrine (Task B), 3/4 front view, daytime, a bit of grass around, clean background |
| `observatory_entrance.png` | the **new** Observatory entrance (Task C), 3/4 front view, same framing style |

- Style: the same renderer as your structure previews, but framed for a book page: soft parchment-toned
  background (`#e9dfc4`) or a light vignette, a thin dark 1px frame is optional (the page can also draw a border).
- 200×200 visible area, readable at book scale (the structure should fill ~80 % of it).

---

## Done means
Each task: `VALIDATION OK`, previews rendered, README updated. Tell majestic which commit to pull (per task is fine).
