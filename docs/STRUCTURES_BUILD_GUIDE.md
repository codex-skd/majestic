# Majestic — Guía de construcción de estructuras (Acto I–II)

> Para el usuario: cómo construir y exportar las piezas NBT de `fallen_shrine` (Acto I) y
> `observatory` (Acto II) para que Claude/OpenCode las cableen en código. Ninguna de las dos
> necesita un mod nuevo — Structure Block vanilla (+ WorldEdit opcional para construir más rápido)
> basta.

---

## 0. Herramientas

- **Structure Block vanilla**: `/give @s minecraft:structure_block`. Necesitas modo creativo y
  trucos activados (`/gamerule` no hace falta, pero el comando `/give` sí necesita permisos de
  operador — en un mundo/servidor propio, actívate como op).
- **WorldEdit (NeoForge)** — opcional, pero recomendado para construir rápido: copiar/pegar,
  deshacer, rellenar volúmenes. No exporta al formato correcto por sí solo — se usa para construir,
  y luego un Structure Block por encima exporta el `.nbt` final.
- Instancia recomendada: cualquier mundo creativo con `majestic` + sus 3 libs instaladas (para ver
  bloques/ítems del mod si los usas en la construcción — de momento no hay bloques decorativos
  propios de majestic, así que puedes construir con bloques vanilla).

## 1. Flujo de exportación (repetir por cada pieza)

1. Construye la pieza dentro de un área conocida (anota las dimensiones exactas: ancho × alto ×
   profundidad).
2. Coloca un Structure Block en la esquina inferior (Sur-Oeste, la convención habitual) del área.
3. Botón derecho → modo **SAVE**.
4. Rellena:
   - **Structure name**: `majestic:<carpeta>/<pieza>` (ver nombres exactos en las secciones de
     abajo — el namespace `majestic:` es importante, así se guarda en
     `generated/majestic/structures/<carpeta>/<pieza>.nbt` dentro del mundo).
   - **Offset / Size**: ajusta hasta que la caja roja cubra exactamente tu construcción (X = ancho,
     Y = alto, Z = profundidad).
   - Marca **Include entities** solo si has colocado alguna entidad a mano (normalmente no hace
     falta, los mobs se añaden por código).
5. Pulsa **SAVE**. Sale un mensaje de éxito.
6. El fichero queda en `<carpeta-del-mundo>/generated/majestic/structures/<carpeta>/<pieza>.nbt`.
   Cópialo a `majestic/neoforge/1.21.1/src/main/resources/data/majestic/structure/<carpeta>/<pieza>.nbt`
   en el repo (mantén la misma subcarpeta). **Ojo**: en el mundo la carpeta es `structures` (plural),
   pero dentro del mod/datapack es `structure` (singular desde 1.21) — si se copia a `structures/`
   el juego no encuentra la plantilla y la estructura no genera nunca.
7. Dime cuándo tengas los ficheros copiados — a partir de ahí yo delego el cableado en código
   (pools, structure set, loot tables, ítems nuevos).

### Bloques Jigsaw (solo para piezas de `observatory`, `fallen_shrine` no los necesita)

Un jigsaw structure conecta piezas por **bloques Jigsaw** (`/give @s minecraft:jigsaw`) colocados
en los puntos donde quieres que otra pieza se enganche (una puerta, un hueco de pasillo, etc.).
Botón derecho sobre el bloque jigsaw para configurarlo:

- **Name**: un id único para ese conector dentro de la pieza (p. ej. `majestic:observatory/entrance_exit`).
- **Target**: el id del conector de la OTRA pieza al que debe engancharse (p. ej.
  `majestic:observatory/corridor_entrance`) — en la práctica, para el primer pase, usa el mismo
  target en ambos lados de forma simétrica y yo ajusto el JSON del pool si hace falta.
- **Pool**: el `template_pool` del que se debe escoger la siguiente pieza (p. ej.
  `majestic:observatory/corridors`) — esto lo defino yo en el JSON, tú solo necesitas dejar el
  bloque jigsaw colocado y orientado hacia donde debe crecer la estructura (la flecha del bloque
  jigsaw indica la dirección de crecimiento).
- **Joint type**: `Aligned` es la opción segura por defecto (mantiene la pieza nueva alineada al
  eje de la anterior).
- **Placement priority** / **Selection priority**: déjalos en `0` salvo que quieras forzar un orden
  concreto — no es crítico para el primer pase.

No necesitas acertar los nombres exactos de `target`/`pool` — con que dejes el bloque Jigsaw
colocado, orientado, y me digas "aquí va el pasillo" / "aquí va la sala del jefe" al pasarme las
piezas, yo ajusto el JSON de los pools para que apunten donde toca.

---

## 2. `fallen_shrine` (Acto I) — ruina pequeña, sin jigsaw real

**Tono**: pequeño santuario en ruinas de la Orden de los Observadores, medio derruido y cubierto de
musgo/vegetación. Genera disperso por el Overworld en superficie (no jigsaw multi-pieza — una sola
pieza fija, aunque técnicamente se registra como un pool jigsaw de 1 elemento para reutilizar el
mismo framework).

| Campo | Valor sugerido |
|---|---|
| Tamaño aproximado | 7×5×7 a 9×6×9 (ancho × alto × profundo) |
| Materiales | piedra agrietada/musgosa, andesita, alguna columna partida, vegetación (enredaderas, ojo de araña opcional) |
| Techo | parcialmente derruido (huecos, no un techo completo) — da sensación de ruina |
| Mobiliario | 1 cofre visible pero semi-enterrado/escondido entre escombros |
| Jigsaw | **ninguno** — pieza única, sin bloques jigsaw |
| Nombre de la pieza | `majestic:fallen_shrine/shrine_01` |
| Mobs | ninguno (a propósito) |

**Botín del cofre** (yo genero la loot table `data/majestic/loot_table/chests/fallen_shrine.json`
una vez tengas la pieza — no hace falta que coloques ítems reales en el cofre del Structure Block,
basta con dejarlo vacío o con un item de relleno cualquiera, la loot table real se aplica por
código):
- `majestic:blank_page` ×1-3
- `majestic:star_fragment` ×1 **garantizado** (ítem nuevo, llave del Acto II — lo registro yo)

*(Opcional, si quieres variedad)*: puedes construir 2-3 variantes de tamaño/forma de la misma
ruina (`shrine_01`, `shrine_02`, `shrine_03`) y las meto todas en el mismo pool con el mismo peso —
no es obligatorio para el primer pase, una sola variante ya es una estructura funcional completa.

---

## 3. `observatory` (Acto II) — jigsaw real, con sala del jefe

**Tono**: torre/observatorio en ruinas de la Orden, más grande y elaborado que el santuario —
"rara", debe sentirse como un hito. Genera disperso pero con más espaciado que `fallen_shrine`
(menos frecuente).

Piezas sugeridas (puedes empezar solo con `entrance` + `arena` para la versión mínima, y añadir
`corridor_a`/`study_room` después si quieres más variedad):

| Pieza | Nombre | Descripción | Jigsaw |
|---|---|---|---|
| Entrada | `majestic:observatory/entrance` | Puerta/patio a nivel de suelo, primer punto de generación | 1 conector saliendo hacia el interior |
| Pasillo A | `majestic:observatory/corridor_a` | Sala/pasillo de conexión, variante 1 | 1 conector de entrada + 1 de salida (alineados en direcciones opuestas) |
| Pasillo B *(opcional)* | `majestic:observatory/corridor_b` | Variante 2 del pasillo, distinto layout, mismo tamaño de conectores | igual que Pasillo A |
| Sala de estudio | `majestic:observatory/study_room` | Sala con estanterías/mesa de estudio, aquí va el cofre de "plano de altar T2" + reactivos | 1 conector de entrada + 1 conector hacia la arena |
| Arena del jefe | `majestic:observatory/arena` | Sala grande y abierta (el Jefe I se añadirá en un hito posterior — de momento solo la sala física) | 1 conector de entrada (sin salida — pieza terminal) |

**Tamaños orientativos**: entrada ~9×6×9, pasillos ~7×5×11 (más largos que anchos), sala de estudio
~9×7×9, arena ~15×10×15 (bastante más grande, con espacio para moverse en un combate futuro).

**Botín**:
- `study_room`: cofre con `majestic:altar_blueprint_t2` (ítem nuevo, "plano de altar T2") ×1
  garantizado + reactivos de escuela (reutilizamos los 4 sigilos ya existentes o un ítem genérico
  "reactivo astral" nuevo — lo decido al cablear) + algo de material astral.
- `arena`: opcional, un cofre pequeño con material astral (el botín "de verdad" del jefe llega
  cuando implementemos el Jefe I).

**Mobs**: de momento **no** los coloques ni los peleés en esta pieza — cultistas/constructos son
contenido del siguiente hito (necesitan modelo/GeckoLib). Esta ronda es solo la estructura física.

**Colocación**: Overworld, superficie, spaciado mayor que `fallen_shrine` (más rara). No hace falta
que decidas los números exactos de `spacing`/`separation` — los fijo yo en el JSON según lo que
indique `CONTENT_WORLD.md`/`PROGRESSION.md`.

---

## 4. Cuando termines

Avísame con qué piezas tienes listas (puede ser solo `fallen_shrine` primero, o las dos a la vez).
Yo:
1. Reviso que los `.nbt` estén en la ruta correcta.
2. Escribo/delego el JSON de `template_pool` + `structure` + `structure_set` + tags de biomas.
3. Registro los ítems nuevos (`star_fragment`, `altar_blueprint_t2`) y las loot tables.
4. Verifico con `runServer` real que la estructura genera (el `runGameTestServer` no sirve para
   esto — no llega a generar mundo real).
