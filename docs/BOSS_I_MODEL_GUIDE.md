# Majestic — Guía de modelo GeckoLib: Jefe I (Warden of the Gate)

> Especificación completa para construir en Blockbench (plugin **GeckoLib**) el modelo, huesos y
> animaciones del primer jefe. Pensada para pasarla tal cual a quien vaya a modelarlo.

---

## 0. Concepto

**`majestic:warden_of_the_gate`** — Acto II, arena de `observatory`. Guardián humanoide de piedra y
luz astral de la Orden de los Observadores, con un báculo. 2 fases: invoca constructos menores;
por debajo del 50% de vida, cubre la sala de "luz" (mecánica de la fase 2, se implementa en código
más adelante — el modelo solo necesita una animación para el momento de la transición).

- **Recompensa**: Lente de éter (llave de la dimensión) + material astral.
- **Render**: GeckoLib. **~6 animaciones**.

---

## 1. Herramientas

- **Blockbench** (gratis, blockbench.net) con el **plugin GeckoLib Animation Utils** instalado
  (Blockbench → File → Plugins → buscar "GeckoLib Animation Utils").
- Al crear el proyecto nuevo en Blockbench: formato **"Bedrock Model"** o, si el plugin lo ofrece
  directamente, **"GeckoLib Model"** (mismo formato, el plugin añade la pestaña de animaciones).

---

## 2. Ficheros a exportar (3, rutas exactas)

| Fichero | Ruta en el repo | Qué exporta |
|---|---|---|
| Geometría | `src/main/resources/assets/majestic/geo/warden_of_the_gate.geo.json` | Blockbench → Export → "Export Bedrock Geometry" |
| Animaciones | `src/main/resources/assets/majestic/animations/warden_of_the_gate.animation.json` | Blockbench → pestaña Animate → Export → "Export Bedrock Animation" |
| Textura | `src/main/resources/assets/majestic/textures/entity/warden_of_the_gate.png` | Blockbench → Textures → guardar el PNG del proyecto |

(Estas 3 rutas son las que GeckoLib espera por convención — no hace falta que me las pases, cópialas
directamente ahí cuando termines.)

---

## 3. Jerarquía de huesos (bones)

Modelo bípedo con báculo, tamaño sugerido ~2.2-2.5 bloques de alto (ligeramente más grande que un
jugador — es un jefe). Jerarquía (indentado = hijo del anterior):

```
root
└── body                  (torso — punto de pivote para inclinarse/girar)
    ├── head               (cabeza — mira al objetivo)
    ├── cloak              (opcional: capa/manto, para un ligero balanceo pasivo — omitir si no
    │                       quieres animarla, no es obligatorio)
    ├── left_arm
    ├── right_arm
    │   └── staff          (el báculo, como hijo de la mano derecha — así se mueve con el brazo
    │                       en los ataques/invocaciones sin animarlo por separado)
    ├── left_leg
    └── right_leg
```

**Nombres de hueso exactos** (usa estos, en minúsculas con guion bajo, tal cual): `root`, `body`,
`head`, `cloak`, `left_arm`, `right_arm`, `staff`, `left_leg`, `right_leg`. El código Java referencia
las animaciones por NOMBRE (no por hueso directamente), así que los nombres de hueso en sí tienen
margen, pero usa estos para que coincidan con cualquier ejemplo que te dé más adelante.

**Textura**: no hay un tamaño de canvas obligatorio (a diferencia de los ítems/bloques a 16×16) —
Blockbench genera el UV automáticamente según el tamaño de los cubos; un canvas de 128×128 o 256×256
va sobrado para este nivel de detalle. Paleta: piedra gris/tuff (cuerpo), acentos dorados o azul
pálido brillante en las runas grabadas y los "ojos"/grietas de luz (mismo lenguaje visual que
`fallen_shrine`/`observatory`/el foco).

---

## 4. Animaciones (6 — nombres exactos)

Cada animación se exporta con el nombre EXACTO indicado (así el código Java las referencia sin
ambigüedad). Duración orientativa entre paréntesis — ajústala a lo que se vea bien, no es estricta.

| Nombre exacto | Bucle | Duración orient. | Descripción |
|---|---|---|---|
| `animation.warden_of_the_gate.idle` | sí (loop) | 2-3s | Respiración/balanceo sutil de pie, la capa ondea ligeramente si la modelaste, la cabeza gira despacio de un lado a otro. |
| `animation.warden_of_the_gate.walk` | sí (loop) | 1s | Ciclo de caminar — piernas alternando, brazos con balanceo mínimo (lleva el báculo, no lo agita al caminar). |
| `animation.warden_of_the_gate.attack` | no | 0.6-0.8s | Golpe cuerpo a cuerpo con el báculo — retroceso del brazo derecho, barrido hacia delante, vuelta a idle. |
| `animation.warden_of_the_gate.summon` | no | 1.2-1.5s | Invoca constructos — levanta el báculo con ambos brazos por encima de la cabeza, pausa breve (para sincronizar el efecto de invocación en código), baja los brazos. |
| `animation.warden_of_the_gate.phase_transition` | no | 1-1.5s | Se activa una vez, al cruzar el 50% de vida — golpe del báculo contra el suelo con los brazos extendidos, cabeza echada hacia atrás (momento dramático de "ahora cubre la sala de luz"). |
| `animation.warden_of_the_gate.death` | no (queda en el último frame) | 1.5-2s | Cae de rodillas y se desploma hacia delante o se desmorona (dado que es un guardián de piedra, puede "resquebrajarse" — rotar las piezas ligeramente hacia fuera en los últimos frames para sugerir que se rompe). |

**Convención de nombre**: `animation.<nombre_de_entidad>.<accion>` es el estándar GeckoLib/Bedrock
— Blockbench con el plugin ya sugiere este formato al crear una animación nueva, solo asegúrate de
que `<nombre_de_entidad>` sea exactamente `warden_of_the_gate`.

---

## 5. Qué hace Claude con esto (no hace falta que te preocupes por ello)

Una vez tengas los 3 ficheros en su sitio, la implementación en código (fuera del alcance de esta
guía) usará el mismo patrón ya establecido en `expedition_core` (`GeoBossEntity`/`GeoBossRenderer`):
una clase `WardenOfTheGateModel extends GeoModel<WardenOfTheGate>` que apunta a las 3 rutas de
arriba, y un `AnimationController` que reproduce cada animación según el estado del jefe (idle/walk
por movimiento, attack/summon/phase_transition/death disparadas por la lógica de combate). No
necesitas tocar nada de Java — solo los 3 ficheros de Blockbench en las rutas indicadas.

## 6. Cuando termines

Copia los 3 ficheros a sus rutas exactas (sección 2) y avísame. Si solo tienes tiempo para un
primer pase, prioriza en este orden: geometría + textura básica + `idle`/`walk`/`attack` (el jefe ya
sería jugable con esas 3 animaciones); `summon`/`phase_transition`/`death` se pueden añadir después
sin rehacer nada.
