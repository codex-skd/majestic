# Majestic — Guía de modelo GeckoLib: Constructo astral (esbirro del Jefe I)

> Especificación para generar en `taller_minecraft` (o Blockbench con el plugin **GeckoLib**) el
> modelo, huesos, animaciones y textura del esbirro que invoca el Jefe I. Mismo formato y pipeline
> que [`BOSS_I_MODEL_GUIDE.md`](BOSS_I_MODEL_GUIDE.md) (Warden of the Gate).

---

## 0. Concepto

**`majestic:astral_construct`** — mob hostil medio, guardián de estructuras (Acto I–II). En el hito
del Jefe I aparece sobre todo **invocado por `warden_of_the_gate` en su fase 1** (2-3 a la vez), y
más adelante también como guardián suelto de estructuras (`CONTENT_WORLD.md §4`).

- Autómata de piedra/tuff animado por un **núcleo de luz astral** visible en el pecho — hermano
  menor del Warden: mismo lenguaje visual, pero más tosco, sin capa ni báculo, sin corona.
- Tamaño: **~1.4 bloques de alto** (más bajo que un jugador; el Warden mide ~2.5). Ancho ~0.8.
- Estilo de combate: cuerpo a cuerpo con los puños, lento y pesado.
- **Render**: GeckoLib 4.7.6. **5 animaciones**.

---

## 1. Ficheros a exportar (3, rutas exactas)

| Fichero | Ruta en el mod (`src/main/resources/`) |
|---|---|
| Geometría | `assets/majestic/geo/astral_construct.geo.json` |
| Animaciones | `assets/majestic/animations/astral_construct.animation.json` |
| Textura | `assets/majestic/textures/entity/astral_construct.png` |

Identificador de geometría: `geometry.astral_construct`. Salida del taller en
`taller_minecraft/entities/astral_construct/output/1.21.1/assets/majestic/` (espejo de la ruta del
mod, igual que el Warden) + `astral_construct.validation.txt`.

---

## 2. Jerarquía de huesos (nombres exactos)

```
root
├── body            torso ancho y achaparrado de piedra; placa de tuff en el pecho
│   ├── core        núcleo de luz astral encastrado en el pecho (cubo pequeño brillante,
│   │               hueso propio para que pueda "latir" en idle/spawn)
│   ├── head        bloque de piedra con dos ojos-ranura de luz, sin cuello visible
│   ├── right_arm   brazo grueso, puño grande (arma principal)
│   └── left_arm
├── right_leg       piernas cortas y robustas (cuelgan de root, como en el Warden)
└── left_leg
```

`root`, `body`, `core`, `head`, `right_arm`, `left_arm`, `right_leg`, `left_leg`. El código Java
solo referencia animaciones por nombre, pero mantén estos nombres para coherencia con el Warden.

---

## 3. Textura

- Canvas **64×64** (box UV). Sobra para este nivel de detalle.
- Paleta (la misma que el Warden y las estructuras):
  - Cuerpo: piedra gris fría + tuff (`#8a8a8a` → `#4a4a4a`), algún píxel de musgo (`#5c6b4a`) de desgaste.
  - Núcleo, ojos y grietas: **azul pálido** brillante (`#a8c8e8`) con puntos más claros; algún
    acento **dorado apagado** (`#d4af6a`) mínimo — el Warden lleva el oro, el constructo casi nada
    (así se distingue de un vistazo el jefe de sus esbirros).
  - Grietas de luz finas recorriendo brazos/torso desde el núcleo.
- Sin transparencias parciales (alfa 0 o 255).

---

## 4. Animaciones (5 — nombres exactos)

| Nombre exacto | Bucle | Duración orient. | Descripción |
|---|---|---|---|
| `animation.astral_construct.idle` | loop | 2 s | Balanceo pesado mínimo; el `core` late (escala 1.0↔1.1). |
| `animation.astral_construct.walk` | loop | 1 s | Paso lento y pesado, brazos colgando con balanceo corto, cuerpo que se inclina a cada paso. |
| `animation.astral_construct.attack` | una vez | 0.6 s | Puñetazo descendente con el brazo derecho (tipo golem de hierro, pero con un brazo). |
| `animation.astral_construct.spawn` | una vez | 1.0 s | Aparece invocado: emerge agachado/encogido desde el suelo (`root` sube desde −12 px), se endereza, el `core` se enciende (escala 0→1). |
| `animation.astral_construct.death` | hold_on_last_frame | 1.2 s | Se derrumba hacia atrás; cabeza y brazos se separan hacia fuera en el último tramo (se "desmorona"), el `core` se apaga (escala →0). |

---

## 5. Validación (igual que el Warden)

```bash
python tools/geo_check.py entities/astral_construct/output/1.21.1/assets/majestic astral_construct \
  --bones root,body,core,head,left_arm,right_arm,left_leg,right_leg \
  --anims animation.astral_construct.idle,animation.astral_construct.walk,animation.astral_construct.attack,animation.astral_construct.spawn,animation.astral_construct.death
```

Previews de reposo, animaciones y UV en `previews/1.21.1/` como en el Warden.

---

## 6. Qué hace majestic con esto

El código ya usa estas 3 rutas exactas (`AstralConstructModel`); mientras no existan, el constructo
se ve como textura ausente / sin modelo. Cuando el taller termine, se copian los 3 ficheros a su ruta
y se hace `build` + `runClient` para confirmar que carga.
