# Majestic — Guía de texturas (ítems y bloques pendientes)

> Especificación de las texturas del mod: tanda 1 (9 ítems + 2 bloques, hecha en beta.9) y tanda 2 (§4, hito Jefe I) para que el contenido
> actual deje de verse como el "bloque de textura ausente" morado/negro en el juego. Pensada para
> pasarla tal cual a quien vaya a dibujarlas (tú, un artista, o como referencia para generar con IA
> y retocar a mano).

---

## 0. Formato técnico (aplica a TODOS los ítems)

- **Tamaño**: 16×16 píxeles.
- **Formato**: PNG con canal alfa (transparencia real donde no hay dibujo — no blanco ni negro de
  fondo).
- **Estilo**: pixel art plano, consistente con el estilo vanilla de Minecraft — sin degradados
  suaves ni antialiasing "de foto", pixels definidos, contorno oscuro de 1px en los bordes del
  dibujo para que se lea bien sobre cualquier fondo de inventario.
- **Vista**: frontal/icono de inventario (igual que cualquier ítem vanilla — no isométrica, no 3D).
- **Programa**: cualquier editor de pixel art (Aseprite, Piskel —gratis, en el navegador—, GIMP con
  zoom a píxel, o el propio editor de texturas de Blockbench). No hace falta nada especializado.
- **Ruta de guardado**: `majestic/neoforge/1.21.1/src/main/resources/assets/majestic/textures/item/<nombre>.png`
  (para ítems) o `.../textures/block/<nombre>.png` (para bloques) — nombres exactos en las tablas
  de abajo.

### Paleta compartida (coherencia visual con el logo/estructuras ya hechas)

| Uso | Color aprox. | Cuándo usarlo |
|---|---|---|
| Piedra/base | grises fríos (`#8a8a8a` a `#4a4a4a`) | fondo de sigilos, altar, pilar |
| Piedra musgosa/vieja | verde apagado (`#5c6b4a`) | acentos de desgaste |
| Pergamino/papel | beige cálido (`#d8c9a3` a `#b8a67e`) | página en blanco, plano |
| Brillo mágico/astral | dorado apagado (`#d4af6a`) o azul pálido (`#a8c8e8`) | runas, cristal del foco, polvo |
| Fragmento estelar | blanco-dorado luminoso (`#f5e6b8`) | fragmento estelar |

**Regla simple**: todo lo "físico" (piedra, madera, pergamino) usa colores apagados/terrosos; todo
lo "mágico" (grabados, cristales, brillos) usa el dorado o el azul pálido de la tabla — así se
distingue de un vistazo qué parte del objeto es mundana y cuál es arcana.

---

## 1. Ítems (9)

### `starlight_focus.png`
El ítem principal — el foco que canaliza hechizos.
- Vara/bastón corto y fino, mango de madera oscura o metal envejecido con vetas doradas.
- Punta rematada en un pequeño cristal en forma de estrella de 4-6 puntas, brillo azul pálido o
  dorado (usa el color de "brillo mágico").
- Ya existe un modelo JSON (`assets/majestic/models/item/starlight_focus.json`) apuntando a esta
  textura — no hace falta tocar el JSON, solo crear el PNG.

### `blank_page.png`
Reactivo universal de rituales.
- Una hoja de pergamino/papel envejecido, simple, doblada o con una esquina curvada — como una
  página de libro vieja, sin escritura (está en blanco).
- Color base pergamino, sombra sutil en los bordes.

### `starlight_bolt_sigil.png`
Sigilo para grabar el hechizo de daño directo.
- Base compartida con los otros 3 sigilos (ver "Base de sigilos" abajo).
- Símbolo grabado: una punta/rayo afilado que irradia desde el centro — motivo agresivo, angular.

### `starlight_ward_sigil.png`
Sigilo para grabar el hechizo defensivo.
- Misma base.
- Símbolo: un anillo/círculo cerrado — motivo protector, redondeado.

### `starlight_reveal_sigil.png`
Sigilo para grabar el hechizo de detección/AoE.
- Misma base.
- Símbolo: un ojo estilizado o un estallido radial de líneas cortas — motivo de "revelar/iluminar".

### `starlight_surge_sigil.png`
Sigilo para grabar el hechizo de velocidad.
- Misma base.
- Símbolo: una flecha o par de alas estilizadas — motivo de movimiento/impulso.

**Base de sigilos (común a los 4)**: una pequeña tableta/medallón de piedra clara o pergamino
rígido, forma redondeada u octogonal, con el símbolo específico grabado en dorado/azul pálido en el
centro. Los 4 deben reconocerse a simple vista como "de la misma familia" (misma forma base, mismo
tamaño de símbolo) pero distinguirse claramente entre sí por el símbolo.

### `star_fragment.png`
Llave del Acto II, botín garantizado de `fallen_shrine`.
- Un fragmento/esquirla de cristal irregular (no una forma geométrica perfecta — bordes afilados,
  como un trozo roto), color blanco-dorado luminoso con un resplandor sutil (unos pocos píxeles más
  claros en el borde exterior para sugerir brillo).

### `altar_blueprint_t2.png`
Hallazgo principal de `observatory` — el plano del altar de Tier 2.
- Un rollo de pergamino parcialmente desenrollado, con líneas de diagrama finas y esquemáticas
  dibujadas encima (círculos concéntricos, alguna marca angular) — sugiere un plano arquitectónico,
  no necesita ser legible en detalle a 16px, solo insinuar "esto es un diagrama".
- Atado con un cordel o cinta oscura.

### `astral_dust.png`
Material de relleno, ambos cofres de `observatory`.
- Un pequeño montón de polvo/partículas brillantes, azul pálido con destellos dorados — similar en
  composición a cómo vanilla dibuja el polvo de blaze o glowstone (montoncito con puntos de brillo
  dispersos), pero en la paleta azul/dorado del mod.

---

## 2. Bloques (2)

Los bloques usan **una sola textura de 16×16 para las 6 caras** (`cubeAll`) — no hace falta
distinguir top/side/bottom para este hito. Guarda en
`.../textures/block/<nombre>.png`. Esa misma textura se usa automáticamente también como icono de
inventario del bloque (ya está cableado así en datagen) — no hace falta dibujar una versión aparte
para el ítem.

### `astral_altar.png`
El bloque del altar T1. Como es una sola textura para las 6 caras, diseña un patrón de piedra
tallada que funcione igual de bien repetido en cualquier cara: piedra gris con un motivo de runas/
líneas astrales grabadas de forma sutil (no un círculo centrado que solo tendría sentido como
"tapa"), acentos de tuff, coherente con la piedra de `fallen_shrine`/`observatory`.

### `astral_pillar.png`
El bloque de pilar decorativo (parte del multibloque del altar).
- Piedra con estriado vertical (como una columna clásica) — usa el mismo tono de piedra/tuff que
  las estructuras ya construidas para que el altar no desentone al lado de `fallen_shrine`/
  `observatory`.

---

## 3. Opción: generar referencia con IA antes de pixelar a mano

Igual que con la imagen de referencia del santuario, puedes generar una imagen de concepto con un
generador de IA y luego pixelarla/recrearla a mano en 16×16 (la IA casi nunca da un pixel art limpio
y usable directamente a ese tamaño, pero sirve de referencia de forma/color). Prompt genérico que
puedes adaptar por ítem (sustituye la descripción entre corchetes por la de la sección
correspondiente arriba):

> Minecraft-style pixel art game item icon, 16x16 pixel grid, flat colors, no gradients, dark 1px outline, transparent background, front-facing inventory icon view. [descripción del ítem]. Color palette: muted stone grays and warm parchment beige for the mundane parts, pale gold or pale blue-white glow for the magical/arcane parts. Clean, simple, iconic silhouette readable at small size.

---

## 4. Tanda 2 — hito Jefe I (pendiente del taller)

Las 11 texturas de arriba ya están hechas (beta.9). Esta tanda añade las del hito del Jefe I. Mismo
formato técnico (§0) y misma paleta.

### `ether_lens.png` (ítem, `textures/item/`)
**Lente de éter** — botín garantizado de `warden_of_the_gate` y **llave de la dimensión** (Acto III).
Tiene que leerse como objeto importante, un escalón por encima del Fragmento estelar.
- Una lente/monóculo circular de cristal en un aro de metal envejecido, con una pequeña asa o
  cadena corta a un lado (como una lupa antigua o un astrolabio de bolsillo).
- Aro: dorado apagado (`#d4af6a`) con sombra más oscura; 1px de contorno oscuro.
- Cristal: **azul-violeta etéreo** — introduce un tono nuevo para la escuela Éter (sugerido
  `#9a8cd8` → `#c8c0f0`), con 2-3 píxeles casi blancos de reflejo en diagonal y un punto de brillo
  en el centro. Debe distinguirse claramente del azul pálido "astral" de los sigilos.
- Un único ítem, sin variante animada (`.mcmeta`) en esta tanda.

### Modelo de entidad: `astral_construct`
Modelo GeckoLib completo (geo + animaciones + textura 64×64) — especificación aparte en
[`ASTRAL_CONSTRUCT_MODEL_GUIDE.md`](ASTRAL_CONSTRUCT_MODEL_GUIDE.md).

### Lo que NO hace falta
- Huevos de spawn (`warden_of_the_gate_spawn_egg`, `astral_construct_spawn_egg`): usan la plantilla
  vanilla tintada, sin textura propia.
- Partículas del pulso de luz de la fase 2: se usan partículas vanilla (`END_ROD`, `FLASH`).

## 5. Cuando termines

Copia cada PNG directamente a su ruta (`textures/item/` o `textures/block/`) — no hace falta que
me pases nada ni que yo las cablee, los modelos ya están generados por datagen apuntando a esos
nombres exactos. Solo avísame para hacer un build + arranque de cliente y confirmar visualmente que
cargan bien (sin el rombo morado/negro).
