# Majestic — Boss I animation pass v2: Warden of the Gate

> Spec for `taller_minecraft`: a second animation pass on the existing Warden of the Gate model
> (`taller_minecraft/entities/warden_of_the_gate/`, v1 delivered and shipped in majestic beta.10).
> **Same geometry, same bones, same texture** — only `warden_of_the_gate.animation.json` changes
> (animations added and reworked).
>
> **Delivery**: regenerate and validate inside `taller_minecraft` only (same output folder as v1,
> `output/1.21.1/assets/majestic/animations/warden_of_the_gate.animation.json`, plus the validation
> file, previews and README). Do not copy anything into majestic — majestic pulls it from there.

---

## 0. Why (playtest feedback)

- The only attack reads as a **forward chop**: the right arm pitches −100° with just −25° of yaw, so
  there is no visible sweep. Players expect a staff boss to **swing across** in wide arcs.
- Walk and idle feel **stiff** for a 2.5-block stone guardian: little weight, the staff barely moves,
  the cloak barely reacts.

The code side will pick a different attack depending on the situation and deal damage **on the
impact keyframe**, so each attack needs a clear wind-up → impact → recovery and the impact time
listed below.

---

## 1. Animation list (v2)

Keep all v1 names working. New names are marked **NEW**; reworked ones **REWORK**.

| Exact name | Loop | Length | Impact at | Status |
|---|---|---|---|---|
| `animation.warden_of_the_gate.idle` | loop | 3.0 s | — | **REWORK** |
| `animation.warden_of_the_gate.walk` | loop | 1.2 s | — | **REWORK** |
| `animation.warden_of_the_gate.run` | loop | 0.8 s | — | **NEW** (phase 2 movement) |
| `animation.warden_of_the_gate.attack` | once | 0.9 s | 0.45 s | **REWORK** → horizontal sweep (see §2) |
| `animation.warden_of_the_gate.attack_slam` | once | 1.2 s | 0.70 s | **NEW** |
| `animation.warden_of_the_gate.attack_thrust` | once | 0.7 s | 0.35 s | **NEW** |
| `animation.warden_of_the_gate.charge_windup` | once (hold last frame) | 0.8 s | — | **NEW** |
| `animation.warden_of_the_gate.charge` | loop | 0.5 s | — | **NEW** |
| `animation.warden_of_the_gate.summon` | once | 1.4 s | 0.6 s (spawn) | keep |
| `animation.warden_of_the_gate.phase_transition` | once | 1.3 s | — | keep (may polish) |
| `animation.warden_of_the_gate.death` | hold_on_last_frame | 1.8 s | — | keep |

"Impact at" = the moment the staff visually connects. Please put it in the README table exactly, so the
code can sync damage to it. Tolerance ±0.05 s.

---

## 2. Attacks

### `attack` — horizontal sweep (REWORK, the most frequent attack)
- Wind-up (0–0.3 s): `body` twists ~35° to the right (Y), right arm draws the staff back and out to the
  right side, `left_arm` rises slightly for balance, head follows the body a little less.
- Sweep (0.3–0.55 s): fast, wide horizontal arc **from right to left across the front**, ~150° of
  total body+arm yaw. The staff stays roughly horizontal at chest height. Impact at **0.45 s**
  (staff crossing the centre line).
- Follow-through + recovery (0.55–0.9 s): the body overshoots ~25° to the left, then eases back to rest.
- The reading should be "wide arc that hits everything in front of it", not a chop.

### `attack_slam` — overhead slam (NEW, used when several players are close)
- Both arms raise the staff high overhead (0–0.55 s), slight backward lean, head up.
- Slam straight down in front (0.55–0.7 s), body bends forward, knees bend slightly (`right_leg`/`left_leg`
  pitch), staff tip hits the ground at **0.70 s**.
- Hold the crouch briefly (0.7–0.9 s), then rise (0.9–1.2 s). The code adds a ground shockwave at the impact.

### `attack_thrust` — quick staff thrust (NEW, used against a target slightly out of reach)
- Short pull-back (0–0.2 s), then a straight forward lunge: body leans forward, right arm extends fully,
  staff pointing at the target, one step forward feel (`right_leg` forward). Impact at **0.35 s**.
- Quick recovery (0.35–0.7 s).

---

## 3. Movement

### `idle` (REWORK)
Slow heavy breathing (body scale/pitch), the cloak ripples continuously, the head scans left/right, and
every cycle one gesture: the staff tip taps the ground once (right arm dips ~15°) around 2.0 s. No
pose should be fully static for more than ~0.5 s.

### `walk` (REWORK)
Heavy stride: stronger body bob (±1 px) and side-to-side roll (±4°), legs with more knee lift, the
staff swings forward/back in time with the left leg (±20° on `right_arm`), the free `left_arm` swings
opposite, the cloak trails behind (negative pitch that grows during the stride).

### `run` (NEW, phase 2)
Same language as `walk` but faster and leaning forward (~15° body pitch), longer strides, staff held
diagonally across the body with both hands (`left_arm` reaching toward the staff).

### `charge_windup` + `charge` (NEW, phase 2 telegraphed rush)
- `charge_windup` (0.8 s, hold last frame): the Warden plants its feet, crouches, lowers the staff
  pointing forward like a lance, head down — a clear "it's about to rush" pose.
- `charge` (loop, 0.5 s): the rush itself — fast running cycle with the staff kept in the lance pose,
  cloak streaming behind.

---

## 4. Validation (same tool as v1)

```bash
python tools/geo_check.py entities/warden_of_the_gate/output/1.21.1/assets/majestic warden_of_the_gate \
  --bones root,body,head,cloak,left_arm,right_arm,staff,left_leg,right_leg \
  --anims animation.warden_of_the_gate.idle,animation.warden_of_the_gate.walk,animation.warden_of_the_gate.run,animation.warden_of_the_gate.attack,animation.warden_of_the_gate.attack_slam,animation.warden_of_the_gate.attack_thrust,animation.warden_of_the_gate.charge_windup,animation.warden_of_the_gate.charge,animation.warden_of_the_gate.summon,animation.warden_of_the_gate.phase_transition,animation.warden_of_the_gate.death
```

Update the per-animation previews (`previews/1.21.1/animations.png`) and the README animation table
(including the "impact at" column).

## 5. Done means

`VALIDATION OK`, previews rendered, README updated with the v2 table. Majestic pulls the new
animation file and wires the new attacks in code.
