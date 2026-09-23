# Majestic — Guía de construcción: Observatory (Acto II)

> Especificación completa y autocontenida para construir y exportar las 4 piezas jigsaw del
> `observatory`. Pensada para pasarla tal cual a quien/lo que vaya a construir — no debería hacer
> falta volver a preguntar nada de diseño.

---

## 0. Resumen de la estructura

El Observatory es una torre/complejo en ruinas de la Orden de los Observadores, más grande y
elaborado que `fallen_shrine` (Acto I). Se genera en cadena **lineal** (no ramificada) de 4 piezas:

```
entrance → corridor (1 de 2 variantes al azar) → study_room → arena
```

Cada flecha es un conector jigsaw. La estructura crece siempre en línea recta, pieza tras pieza —
no hay bifurcaciones ni piezas opcionales que saltar.

**Paleta de materiales** (consistente con `fallen_shrine`, pero más "elaborada" — añade tuff/calcite
y un guiño astronómico):
- Piedra: piedra bruja/agrietada/musgosa (`stone_bricks`, `cracked_stone_bricks`,
  `mossy_stone_bricks`), andesita, **tuff y tuff cincelado** (`tuff`, `chiseled_tuff`), calcita.
- Vegetación de ruina: enredaderas, musgo (bloque y alfombra), helechos.
- Iluminación: `soul_lantern` (luz fría azulada, coherente con el tono "astral").
- Detalle opcional en el suelo de la sala de estudio o la arena: un mosaico simple de constelación
  usando `terracota` o `hormigón` en azul oscuro/gris claro incrustado en el suelo de piedra — no es
  obligatorio, pero refuerza el tema si hay tiempo.
- Techos parcialmente derruidos en todas las piezas excepto la arena (que puede estar más abierta,
  a cielo parcialmente descubierto, como un anfiteatro roto).

---

## 1. Mecánica técnica (repetir por cada una de las 4 piezas)

1. Construye la pieza dentro de un área con dimensiones conocidas.
2. Coloca los bloques Jigsaw que le correspondan (tabla exacta en la sección 2, por pieza) **antes**
   de guardar con el Structure Block.
3. Si la pieza lleva cofre, colócalo y asígnale su loot table por comando ANTES de guardar (ver
   sección 2 — igual que se hizo con `fallen_shrine`):
   ```
   /data merge block <x> <y> <z> {LootTable:"<id de la tabla, ver la pieza>"}
   ```
4. Coloca un **Structure Block** (`/give @s minecraft:structure_block`) en la esquina inferior
   Sur-Oeste del área, modo **SAVE**:
   - **Structure name**: exactamente el que indica cada pieza en la sección 2 (columna "Nombre de
     exportación").
   - **Size**: ajusta hasta cubrir toda la pieza (incluye el volumen de aire por encima si el techo
     está derruido con huecos — mejor pasarse un poco de alto que cortar el techo).
   - **Include entities**: no hace falta.
5. Pulsa **SAVE**.
6. Copia el `.nbt` resultante (sale en `<mundo>/generated/majestic/structures/...`) — mantenlo con
   el nombre exacto que se indica, y pásamelo cuando tengas las 4.

### Bloques Jigsaw — cómo rellenarlos

Botón derecho sobre un bloque Jigsaw (`/give @s minecraft:jigsaw`) abre su configuración. Rellena
estos 3 campos exactamente como se indica por pieza en la sección 2:

- **Name**: el identificador de ESTE conector.
- **Target**: el identificador del conector de la pieza siguiente al que debe engancharse.
- **Pool**: el pool del que se debe escoger la pieza siguiente.

Además, para TODOS los bloques jigsaw de esta estructura:
- **Joint type**: `Aligned`.
- **Placement priority**: `0`.
- **Selection priority**: `0`.
- **Orientación (la flecha del bloque)**: apunta hacia donde debe crecer la estructura desde ese
  punto (hacia fuera de la sala, hacia el hueco de conexión). No te preocupes por hacer coincidir la
  dirección exacta entre dos piezas distintas — el juego rota automáticamente la pieza que llega
  para que encaje (`Aligned` ya hace ese trabajo).
- Dónde colocarlo físicamente: en el hueco/puerta/arco por el que debería conectarse la siguiente
  sala — normalmente ras de suelo, en el centro del vano de paso.

Un conector que **recibe** (el lado "entrada" de una pieza) solo necesita que su campo **Name**
sea correcto — `Target` y `Pool` en ese bloque concreto no importan, pero rellénalos igualmente con
`minecraft:empty` y `minecraft:empty` para no dejarlos en blanco.

---

## 2. Piezas (especificación exacta)

### Pieza 1 — `entrance`

| Campo | Valor |
|---|---|
| Nombre de exportación | `majestic:observatory/entrance` |
| Tamaño aproximado | 9 × 6 × 9 (ancho × alto × profundo) |
| Descripción | Patio/puerta de entrada a nivel de suelo. Arco de entrada semi-derruido, un par de columnas de tuff, suelo de piedra con musgo. Punto de generación inicial — es lo primero que ve el jugador. |
| Cofre | ninguno |
| Bloques Jigsaw | **1**, saliente, colocado en el vano/arco que lleva hacia el interior |

Jigsaw de esta pieza (el único):
- Name: `majestic:observatory/entrance_exit`
- Target: `majestic:observatory/corridor_entrance`
- Pool: `majestic:observatory/corridors`

---

### Pieza 2 — `corridor_a` (variante 1 del pasillo)

| Campo | Valor |
|---|---|
| Nombre de exportación | `majestic:observatory/corridor_a` |
| Tamaño aproximado | 7 × 5 × 11 (más largo que ancho — es un pasillo recto) |
| Descripción | Pasillo/sala de paso alargada, techo parcialmente derruido, un par de estanterías o restos de mobiliario a los lados, sensación de tránsito. |
| Cofre | ninguno |
| Bloques Jigsaw | **2**: uno de entrada en un extremo, uno de salida en el extremo opuesto (a lo largo del eje largo de la sala) |

Jigsaw "entrada" (extremo por el que se llega desde `entrance`):
- Name: `majestic:observatory/corridor_entrance`
- Target: `minecraft:empty`
- Pool: `minecraft:empty`

Jigsaw "salida" (extremo opuesto, hacia la sala de estudio):
- Name: `majestic:observatory/corridor_exit`
- Target: `majestic:observatory/study_entrance`
- Pool: `majestic:observatory/study_pool`

---

### Pieza 3 — `corridor_b` (variante 2 del pasillo — mismo rol que `corridor_a`, distinto aspecto)

| Campo | Valor |
|---|---|
| Nombre de exportación | `majestic:observatory/corridor_b` |
| Tamaño aproximado | 7 × 5 × 11 (mismo tamaño de referencia que `corridor_a` — el layout interior puede variar, p. ej. un recodo en vez de recto, siempre que los dos conectores sigan en extremos opuestos) |
| Descripción | Variante visual del pasillo — cambia el layout interior (una curva, una sala más ancha en el medio, escombros distintos) para que la generación no se sienta repetitiva, pero cumple el mismo rol de conexión que `corridor_a`. |
| Cofre | ninguno |
| Bloques Jigsaw | **2**, mismos nombres exactos que `corridor_a` (es una variante alternativa del mismo rol, no una pieza distinta en la cadena) |

Jigsaw "entrada":
- Name: `majestic:observatory/corridor_entrance`
- Target: `minecraft:empty`
- Pool: `minecraft:empty`

Jigsaw "salida":
- Name: `majestic:observatory/corridor_exit`
- Target: `majestic:observatory/study_entrance`
- Pool: `majestic:observatory/study_pool`

*(Nota: si construir 2 variantes lleva demasiado tiempo, se puede empezar solo con `corridor_a` y
añadir `corridor_b` más adelante — el pool de pasillos funciona igual con 1 o con 2 elementos, yo
ajusto el peso en el JSON.)*

---

### Pieza 4 — `study_room`

| Campo | Valor |
|---|---|
| Nombre de exportación | `majestic:observatory/study_room` |
| Tamaño aproximado | 9 × 7 × 9 |
| Descripción | Sala de estudio de la Orden — estanterías (`bookshelf`), una mesa/atril central, restos de instrumental astronómico (puedes improvisar con `lightning_rod`, `amethyst` roto, o simplemente piedra tallada si prefieres no usar bloques "modernos"). Aquí vive el cofre con el hallazgo principal del Acto II. |
| Cofre | **sí, obligatorio** — ver abajo |
| Bloques Jigsaw | **2**: entrada (desde el pasillo) y salida (hacia la arena) |

Cofre de esta pieza:
```
/data merge block <x> <y> <z> {LootTable:"majestic:chests/observatory_study"}
```

Jigsaw "entrada":
- Name: `majestic:observatory/study_entrance`
- Target: `minecraft:empty`
- Pool: `minecraft:empty`

Jigsaw "salida" (hacia la arena):
- Name: `majestic:observatory/study_exit`
- Target: `majestic:observatory/arena_entrance`
- Pool: `majestic:observatory/arena_pool`

---

### Pieza 5 — `arena`

| Campo | Valor |
|---|---|
| Nombre de exportación | `majestic:observatory/arena` |
| Tamaño aproximado | 15 × 10 × 15 (bastante más grande que el resto — sala abierta para un combate futuro) |
| Descripción | Sala/anfiteatro circular o cuadrado abierto, techo muy derruido o inexistente en el centro (cielo visible), quizás un pedestal/altar roto en el centro como punto focal. **No coloques mobs ni jefe** — es solo la sala física, el contenido del combate llega en un hito posterior. |
| Cofre | opcional — ver abajo |
| Bloques Jigsaw | **1**, solo de entrada (es la pieza final de la cadena, no tiene salida) |

Jigsaw "entrada" (el único, y el último de toda la cadena):
- Name: `majestic:observatory/arena_entrance`
- Target: `minecraft:empty`
- Pool: `minecraft:empty`

Cofre opcional de esta pieza (si lo añades):
```
/data merge block <x> <y> <z> {LootTable:"majestic:chests/observatory_arena"}
```

---

## 3. Resumen de exportación (tabla rápida)

| # | Nombre de exportación | Jigsaw | Cofre |
|---|---|---|---|
| 1 | `majestic:observatory/entrance` | 1 (salida) | no |
| 2 | `majestic:observatory/corridor_a` | 2 (entrada+salida) | no |
| 3 | `majestic:observatory/corridor_b` | 2 (entrada+salida, igual que `corridor_a`) | no |
| 4 | `majestic:observatory/study_room` | 2 (entrada+salida) | sí — `majestic:chests/observatory_study` |
| 5 | `majestic:observatory/arena` | 1 (solo entrada) | opcional — `majestic:chests/observatory_arena` |

Mínimo viable si quieres ir por partes: **1 → 2 → 4 → 5** (sin `corridor_b`) ya es una cadena
completa y funcional; `corridor_b` se puede añadir después sin tocar nada de lo ya construido.

---

## 4. Botín (referencia — no hace falta que construyas los ítems, ya existen o los creo yo)

- `majestic:chests/observatory_study`: `majestic:altar_blueprint_t2` (nuevo ítem, garantizado ×1) +
  1-2 de los 4 sigilos de hechizo ya existentes (`starlight_*_sigil`) + `majestic:astral_dust`
  (nuevo ítem, material) ×2-4.
- `majestic:chests/observatory_arena`: `majestic:astral_dust` ×2-4 (botín menor — el botín "de
  verdad" del jefe llega con el hito del Jefe I).

## 5. Cuando termines

Pásame las piezas que tengas (aunque sea solo 1 → 2 → 4 → 5 sin `corridor_b`, o incluso pieza a
pieza según las vayas terminando). Reviso cada `.nbt` igual que hice con `fallen_shrine` (tamaño,
presencia de jigsaw/cofre, loot table correcta) antes de aceptarla, para no repetir las rondas de
corrección de la vez anterior.
