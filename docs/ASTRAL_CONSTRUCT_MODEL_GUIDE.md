# Majestic — GeckoLib model guide: Astral Construct (Boss I minion)

> Spec for `taller_minecraft` to build the model, bones, animations and texture of the minion summoned
> by Boss I. Same format and pipeline as [`BOSS_I_MODEL_GUIDE.md`](BOSS_I_MODEL_GUIDE.md) (Warden of the
> Gate, already delivered in `taller_minecraft/entities/warden_of_the_gate/`).
>
> **Delivery**: build and validate everything inside the `taller_minecraft` repo only. Do not copy
> anything into majestic or any other project — majestic pulls the files from the taller's output
> folder when they are ready.

---

## 0. Concept

**`majestic:astral_construct`** — medium hostile mob, structure guardian (Acts I–II). In the Boss I
milestone it mostly appears **summoned by `warden_of_the_gate` during phase 1** (2–3 at a time); later
it will also guard structures on its own (`CONTENT_WORLD.md §4`).

- Stone/tuff automaton animated by a **visible astral-light core** in its chest — the Warden's lesser
  sibling: same visual language, but cruder, no cloak, no staff, no crown.
- Size: **~1.4 blocks tall** (shorter than a player; the Warden is ~2.5). Width ~0.8. The entity
  hitbox in code is `0.8 × 1.4`.
- Combat style: slow, heavy melee with its fists.
- **Render**: GeckoLib 4.7.6, Minecraft 1.21.1. **5 animations**.

---

## 1. Output files (3)

Output layout mirrors the mod's resource layout, exactly like the Warden:
`taller_minecraft/entities/astral_construct/output/1.21.1/assets/majestic/`

| File | Path inside the output folder |
|---|---|
| Geometry | `assets/majestic/geo/astral_construct.geo.json` |
| Animations | `assets/majestic/animations/astral_construct.animation.json` |
| Texture | `assets/majestic/textures/entity/astral_construct.png` |

Geometry identifier: `geometry.astral_construct`. Plus
`output/1.21.1/astral_construct.validation.txt`, previews in `previews/1.21.1/` and a `README.md`, as
for the Warden.

These file names are fixed: majestic's code already points to these exact resource paths.

---

## 2. Bone hierarchy (exact names)

```
root
├── body            broad, squat stone torso; tuff plate on the chest
│   ├── core        astral-light core set into the chest (small glowing cube, its own bone
│   │               so it can "pulse" in idle/spawn)
│   ├── head        stone block with two glowing eye slits, no visible neck
│   ├── right_arm   thick arm, oversized fist (main weapon)
│   └── left_arm
├── right_leg       short, sturdy legs (hanging from root, as in the Warden)
└── left_leg
```

`root`, `body`, `core`, `head`, `right_arm`, `left_arm`, `right_leg`, `left_leg`. Java only references
animations by name, but keep these bone names for consistency with the Warden.

---

## 3. Texture

- **64×64** canvas, box UV. Enough for this level of detail.
- Palette (same as the Warden and the structures):
  - Body: cold grey stone + tuff (`#8a8a8a` → `#4a4a4a`), a few moss pixels (`#5c6b4a`) for wear.
  - Core, eyes and cracks: **pale blue** glow (`#a8c8e8`) with lighter highlight pixels; at most a
    minimal touch of **muted gold** (`#d4af6a`). The Warden wears the gold, the construct almost none,
    so the boss and its minions are told apart at a glance.
  - Thin light cracks running from the core along the torso and arms.
- No partial transparency (alpha 0 or 255 only).

---

## 4. Animations (5 — exact names)

| Exact name | Loop | Approx. length | Description |
|---|---|---|---|
| `animation.astral_construct.idle` | loop | 2 s | Minimal heavy sway; `core` pulses (scale 1.0 ↔ 1.1). |
| `animation.astral_construct.walk` | loop | 1 s | Slow, heavy step; arms hanging with a short swing; body leans into each step. |
| `animation.astral_construct.attack` | play once | 0.6 s | Downward punch with the right arm (iron-golem-like, one arm). |
| `animation.astral_construct.spawn` | play once | 1.0 s | Summoned in: rises crouched from the ground (`root` starts at −12 px), straightens up, `core` lights up (scale 0 → 1). |
| `animation.astral_construct.death` | hold_on_last_frame | 1.2 s | Topples backwards; head and arms drift outward at the end (it crumbles); `core` goes dark (scale → 0). |

---

## 5. Validation (same tool as the Warden)

```bash
python tools/geo_check.py entities/astral_construct/output/1.21.1/assets/majestic astral_construct \
  --bones root,body,core,head,left_arm,right_arm,left_leg,right_leg \
  --anims animation.astral_construct.idle,animation.astral_construct.walk,animation.astral_construct.attack,animation.astral_construct.spawn,animation.astral_construct.death
```

Rest-pose, per-animation and UV previews in `previews/1.21.1/`, as for the Warden.

---

## 6. Done means

`VALIDATION OK` in `astral_construct.validation.txt`, previews rendered, README updated. That's all —
majestic takes it from there.
