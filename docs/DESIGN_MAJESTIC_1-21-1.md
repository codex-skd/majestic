# Majestic — Diseño técnico (mod principal)

> El mod de **contenido**. Consume `astral_core`, `expedition_core`, `almanac_core` y GeckoLib.
> No expone API pública. Contenido concreto en [`PROGRESSION.md`](PROGRESSION.md),
> [`CONTENT_MAGIC.md`](CONTENT_MAGIC.md), [`CONTENT_WORLD.md`](CONTENT_WORLD.md).
> **Estado**: borrador de arquitectura.

| Dato | Valor |
|---|---|
| Mod ID | `majestic` |
| Paquete | `com.skd.majestic` |
| Clase principal | `Majestic` |
| Display name | Majestic |
| MC / NeoForge | `1.21.1` / `21.1.249` |
| Licencia | **All Rights Reserved** (repo privado, sin bundling de deps) |
| Repo | `stalking-dragons/minecraft/majestic` |
| Rama | `minecraft/1.21.1/neoforge-21.1.249/production` |
| Deps reales | `astral_core`, `expedition_core`, `almanac_core`, `geckolib` |
| Deps soft | `jei`, `vellumli` (transitivas vía `almanac_core`) |

---

## 1. Alcance

Majestic aporta **todo lo concreto**:
- Ítems: foco(s), páginas, reactivos por escuela, materiales astrales, llaves de acto, reliquias.
- Bloques: piezas de altar T1–T3, minerales/nodos, bloques decorativos celestes, ancla de retorno.
- Entidades: mobs de estructura y bioma + jefes (GeckoLib).
- Magia: 2–4 escuelas, hechizos, rituales, nodos de investigación, reliquias — **definidos como
  datos** (JSON `almanac_core`) + datagen.
- Mundo: dimensión "plano celeste", biomas, estructuras jigsaw, features, spawns, loot.
- Progresión: advancements (questline), guía `vellumli`.
- Config, i18n (`en_us` + `es_es`), assets.

---

## 2. Paquetes

```
com.skd.majestic
├── Majestic                    (entrypoint)
├── registry                    (DeferredRegister: ITEMS, BLOCKS, BLOCK_ENTITIES, ENTITIES,
│                                SOUNDS, CREATIVE_TABS, DATA_COMPONENTS, PARTICLES)
├── content
│   ├── item/                   (FocusItem, ReagentItem, KeyItem, RelicItem base...)
│   ├── block/                  (AltarBlock T1-3 + AltarBlockEntity extendiendo astral_core,
│   │                            OreBlock, ConstellationNodeBlock, ReturnAnchorBlock, deco)
│   └── entity/
│       ├── mob/                (StarMoth, EtherWisp, AstralConstruct — GeckoLib donde aplique)
│       └── boss/               (WardenOfTheGate, TheHollowStar, TheFirstConstellation:
│                                GeoBossEntity + GeoBossRenderer de expedition_core)
├── magic
│   ├── schools/                (constantes de id de escuela; NO lógica — la lógica es datos)
│   ├── effect/                 (efectos complejos referenciados por SpellDefinition cuando M2=híbrido)
│   ├── ritual/                 (rituales con lógica especial que no cabe en JSON, si los hay)
│   └── relic/                  (comportamientos de reliquia complejos; los simples van por datos)
├── world
│   ├── dimension/              (registro del LevelStem/DimensionType por datapack + viaje vía expedition_core)
│   ├── biome/                  (claves + modificadores)
│   ├── structure/              (tipos + jigsaw pools, apoyado en expedition_core.JigsawUtils)
│   ├── feature/                (flora, cristales, cráteres)
│   └── portal/                 (ritual/mecánica de acceso con la Lente de éter)
├── progression
│   ├── triggers/               (engancha astral_core.research: al obtener llave, al entrar en estructura)
│   ├── advancement/            (datagen de la questline)
│   └── boss/                   (config de cada BossEncounter: fases, arena, loot, música)
├── datagen                     (DataGenerators: recipes, loot, tags, advancements, models,
│                                lang, + JSON de almanac: spell/ritual/research_node/relic)
├── client
│   ├── hud/                    (barra de esencia si E2 = HUD propio)
│   ├── render/                 (renderers de BE de altar, VFX de nodos)
│   ├── screen/                 (pantalla de investigación / árbol de constelaciones, si aplica)
│   └── particle/
├── compat                      (soft; cada sub-paquete gated con ModList.isLoaded)
│   ├── jei/                    (extensiones específicas de majestic sobre almanac_core.jei)
│   └── vellumli/               (contenido del libro: textos, orden de entradas)
└── config                      (ModConfigSpec: SERVER = balance; CLIENT = HUD/VFX)
```

---

## 3. Estrategia de datos

> **E8 CONFIRMADO (2026-09-10): datagen para TODO.** JSON de `almanac_core` (spell/ritual/
> research_node/relic) + recetas + loot tables + tags + advancements + block/item models + lang
> `en_us`, todo generado por `DataGenerators`. JSON a mano **solo** cuando un `Codec` no lo exprese
> bien (efecto de hechizo complejo, pieza jigsaw puntual). `es_es` se escribe a mano (contenido
> propio, no traducción automática).

- **Todo lo que pueda ser dato, es dato.** Hechizos, rituales, nodos y reliquias "simples" se
  generan por **datagen** a JSON de `almanac_core` (no se escriben a mano).
- Solo baja a Java lo que un `Codec` no puede expresar razonablemente (efectos con estado,
  IA de invocaciones, reliquias con lógica de tick compleja) — vía el modelo híbrido M2 de
  `almanac_core` (`type` que apunta a una clase registrada de `majestic`).
- Dimensión, biomas y estructuras: JSON de datapack generado por datagen + `expedition_core` para
  el viaje/arena.

---

## 4. Distribución (licencia ARR)

- Repo GitLab **privado**, **solo rama `production`** (sin espejo `main` público, a diferencia de
  las librerías). CI limitada a build/test.
- `mod_license = All Rights Reserved`.
- Proyecto **CurseForge público, descarga libre** (Setup-2): cualquiera lo descarga y juega; ARR
  solo impide redistribuir/reutilizar el código.
- **Sin jar-in-jar**: `astral_core`, `expedition_core`, `almanac_core`, GeckoLib y (opcional)
  `vellumli`/JEI se declaran como dependencias en `neoforge.mods.toml` y las resuelve el usuario
  (o la página de CurseForge como *required dependencies*). Ver E9.

---

## 5. Preguntas abiertas (además de E1–E9 del ecosistema)

| # | Pregunta |
|---|---|
| J3 | ¿Creative tab única o una por categoría (magia / mundo / reliquias)? |
| J4 | ¿`es_es` se mantiene a mano o entra en el flujo de traducción con OpenCode como otros mods? |

### Cerrado en esta iteración (2026-09-10 / 2026-09-11)

- Dimensión = `majestic:celestial_plane` ("Plano celeste").
- **Foco (J2)**: **único ítem-foco** con hechizo grabado intercambiable (el ritual de grabado
  cambia el hechizo activo). No un foco por escuela. *(Ranuras múltiples de hechizo en el foco =
  posible mejora v1.x, no v1.0.)*
- **Árbol de investigación (J1)**: en v1.0 el progreso de nodos **solo se ve en la guía**
  (`vellumli`). Pantalla propia de constelaciones = v1.x.
- Esencia única global; HUD widget en esquina, visible solo al empuñar el foco (E2).
- 3 tomos de avance, recompensas siempre opcionales (E6).
- Datagen para todo (E8).
- Slot de reliquia capstone propio `majestic:astral` (JSON en `data/majestic/curios/slots/`);
  reliquias generales en `charm`. Ítems `ICurioItem` con tags `#regalia_slots_api:<slot>` + `#curios:<slot>`.
- Forja de reliquias = ritual T3 (`RitualType`), sin `RecipeType` dedicado.
- Full multiplayer: dimensión y altares compartidos, progreso por jugador, jefes escalados por nº de jugadores en arena.
- Libro-guía principal (crafteable + entregado, revelado progresivo) + 2–3 tomos de avance por misión.
- Deps requeridas en CurseForge: `astral_core`, `expedition_core`, `almanac_core`, `geckolib`.

---

## 5b. Hito M2 — Altar T1, nodo de investigación inicial, guía (2026-09-22)

Antes de las estructuras del Acto I–II (`PROGRESSION.md §5` paso 8), toca el paso 6: **altar T1,
nodos Acto I, guía**. Alcance decidido para que sea autocontenido y no dependa de contenido futuro:

- **Solo altar T1.** El T2 (Observatorio) necesita reactivos/mobs que solo existirán con la
  estructura del Acto II — se deja para ese hito, no se inventa contenido a medias.
- **Multiblock T1**: `majestic:astral_altar` (bloque + `AstralAltarBlockEntity extends
  astral_core.ritual.AltarBlockEntity`) en el centro + 4 `majestic:astral_pillar` (bloque simple,
  sin BlockEntity) en offsets (±2,0,0)/(0,0,±2), tageados `#majestic:altar_pillar`.
- **Limitación real de `astral_core` (M2, TODO documentado)**: `RitualContext.inputs()` siempre
  llega vacío — el escaneo de pedestales está diferido, no implementado. En vez de bloquear este
  hito en un cambio cruzado a `astral_core`, el ritual de grabado **lee/consume reactivos del
  inventario del jugador directamente** (`context.player()`), no de pedestales físicos. Los
  pilares del multibloque son solo el requisito estructural (hay que construir el altar), no
  contenedores de items. Revisar si merece la pena implementar el escaneo real de pedestales
  cuando el Acto II necesite rituales con más de un tipo de reactivo simultáneo.
- **Ritual `majestic:engrave_spell`** (`RitualType`/`Ritual` de `astral_core`): un único tipo de
  ritual, parametrizado por qué **sigilo** (`majestic:starlight_{bolt,ward,reveal,surge}_sigil`)
  lleva el jugador en la mano secundaria — determina qué hechizo se graba. Requiere
  `majestic:blank_page` en el inventario + el nodo `majestic:first_light` desbloqueado + coste de
  esencia (de `RitualDefinition` si hay datapack cargado, si no fallback `20.0`). Al completarse:
  consume `blank_page` + el sigilo usado, y fija el componente `majestic:recorded_spell` (ver
  abajo) en el foco que el jugador lleva en la mano principal.
- **Foco reescrito**: el hechizo lanzado deja de estar hardcodeado a `starlight_bolt`. Nuevo
  `DataComponentType<ResourceLocation>` `majestic:recorded_spell` (persistente + sincronizado),
  con `starlight_bolt` como valor por defecto si el componente no está presente (compatibilidad
  con focos ya existentes/dados por comando). `FocusItem.use()` lee este componente para decidir
  qué `SpellType` lanzar.
- **Nodo de investigación**: solo `majestic:first_light` en este hito (sin requisitos, se
  desbloquea automáticamente la primera vez que el jugador usa el foco con éxito —
  `ResearchApi.unlock`). Coincide literalmente con la fila de `CONTENT_MAGIC.md §5` ("obtener el
  foco" → "escuela Luz Estelar T1, altar T1"). Los 2–3 nodos adicionales de "Acto I" (tabla
  `PROGRESSION.md §4`) se añaden en el hito de estructuras, cuando haya algo real que los gatee
  (hallazgo en Fallen Shrine) — no se inventan ahora.
- **Guía** (`majestic:almanac`, `vellumli`): libro con categorías propias
  `majestic:{spells,rituals,research}` (registradas vía `GuideStructure.register`, no las
  compartidas de `almanac_core`) y 6 entradas generadas con `EntryGenerator` (4 hechizos + ritual
  `engrave_spell` + nodo `first_light`). Cada entrada lleva su `advancement` =
  `EntryGate.advancementIdFor(entryId)`; el advancement correspondiente (criterio `"trigger"`, ver
  convención de `expedition_core.AdvancementHooks`) se concede vía `expedition_core.AdvancementHooks
  .grantAll` cuando se dispara `NodeUnlockedEvent` para `first_light` — mismo evento que entrega el
  libro al jugador (`VellumliBridge.giveBookStack`).
- **Datos**: `SpellDefinition`/`RitualDefinition`/`ResearchNodeDefinition` JSON generados por
  datagen real (Codec-based `DataProvider`), sustituyendo el JSON de hechizos escrito a mano en
  beta.1 (cierra esa nota de la beta.1, ver E8). El book/categorías/entradas de `vellumli` se
  escriben a mano — no hay `Codec`/provider para ese formato todavía, excepción aceptada igual que
  el resto del ecosistema con formatos ajenos a un `Codec`.

## 5c. Hito M3 — Fallen Shrine (Acto I) (2026-09-23)

Primera estructura real del mod. Bloqueo de partida: una estructura jigsaw "de verdad" (piezas
NBT) no se puede generar por texto — el usuario construye en el juego con un Structure Block
(vanilla, sin mod adicional necesario) y exporta el `.nbt`; ver
[`STRUCTURES_BUILD_GUIDE.md`](STRUCTURES_BUILD_GUIDE.md) para el flujo completo.

**Alcance decidido**: `fallen_shrine` es una pieza única (sin conectores jigsaw) — jigsaw se usa
solo como framework de registro (reutiliza el mismo `template_pool`/`structure`/`structure_set`
que usaría una estructura multi-pieza), no porque haga falta variedad todavía. `observatory`
(Acto II, jigsaw real con múltiples piezas + arena del jefe) queda para el siguiente hito.

**Implementado por Claude directamente** (sin delegar en OpenCode — es casi 100% JSON de datapack
más una línea de registro de ítem, no código Java sustancial):
- Pieza `data/majestic/structures/fallen_shrine/shrine_01.nbt` (aportada por el usuario, construida
  con Structure Block).
- `worldgen/template_pool/fallen_shrine/start_pool.json` (1 elemento, `single_pool_element`,
  `fallback: minecraft:empty`).
- `worldgen/structure/fallen_shrine.json` (`type: minecraft:jigsaw`, `size: 1`, sin conectores).
- `worldgen/structure_set/fallen_shrine.json` (`random_spread`, spacing 24/separation 10, salt
  `20260923`).
- Tag `tags/worldgen/biome/has_structure/fallen_shrine.json` (bosque/sabana/taiga/llanuras/prado —
  no existe un tag vanilla genérico "overworld terrestre", se sigue la convención de vanilla de
  listar biomas explícitos por estructura).
- Loot table `loot_table/chests/fallen_shrine.json`: `majestic:star_fragment` garantizado (nuevo
  ítem, llave del Acto II) + `majestic:blank_page` ×1-3.
- Ítem `majestic:star_fragment` (`MajesticItems`), modelo + lang vía datagen.

**Bloqueo real encontrado por Claude (resuelto)**: la primera exportación del usuario no tenía
cofre; la segunda tenía un cofre pero con la loot table equivocada (`minecraft:chests/stronghold_library`,
copiada de un ejemplo sin editar) — dos rondas de verificación (`gzip -dc` + búsqueda de la etiqueta
`LootTable` en el NBT crudo) hasta confirmar `LootTable: "majestic:chests/fallen_shrine"` en la
tercera exportación. Publicado en beta.4.

Verificado: `./gradlew build` limpio + arranque de **servidor dedicado real** (`runServer`, no
`runGameTestServer` — este último nunca llega a generar mundo/recargar datapacks) hasta "Done" sin
errores de carga de datapack, con el `.nbt` final (cofre + loot table correcta). **Pendiente**:
verificar en el juego que la estructura genera realmente en un chunk explorado y que el cofre suelta
el botín esperado (requiere jugar/explorar, no automatizable).

## 6. Historial

| Fecha | Cambio |
|---|---|
| 2026-09-11 | `astral_core` + `almanac_core` cableados como deps reales (jars en `libs/`, `compileOnly`+`localRuntime`, `required` en `neoforge.mods.toml`). **Alpha jugable implementada** (delegado a OpenCode `mimo-v2.5`, con 3 bugs reales encontrados y corregidos por Claude tras smoke-test): escuela Luz Estelar (`magic/school/Schools`), 4 hechizos concretos (`StarlightBolt/Ward/Reveal/Surge`, registrados como `SpellType` de `astral_core`), foco `majestic:starlight_focus` (`content/item/FocusItem`), HUD de esencia (`client/hud/EssenceHudOverlay`), comando `/majestic status`, datagen de los 4 `SpellDefinition` JSON + modelo/lang del foco. |
| 2026-09-11 | **Fix Claude**: `build.gradle` de los 4 repos del ecosistema usaba `clientData()` para el run `data`, inexistente en moddev 2.0.142 (`runData` fallaba) — corregido a `data()`. |
| 2026-09-11 | **Fix Claude**: JSON de hechizos generados en `data/majestic/spells/` en vez de `data/majestic/almanac/spell/` (ruta que espera el `SpellLoader` de `almanac_core`) — nunca se habrían cargado. Movidos a la ruta correcta. |
| 2026-09-11 | **Fix Claude**: `Majestic.java` llamaba `AstralRegistries.register(modEventBus)` además de `astral_core`, duplicando el listener de `NewRegistryEvent` sobre el mismo registro custom — eliminado (solo `astral_core` debe crearlo). |
| 2026-09-11 | **Fix Claude**: `MajesticSpells` resolvía (`.get()`) los `DeferredHolder` de `SpellType` en el inicializador estático, antes de que el registro de `astral_core` estuviera enlazado (`IllegalStateException: Registry not present`) — los campos pasan a ser `DeferredHolder` sin resolver, `.get()` solo en el momento de uso (foco/comando). |
| 2026-09-11 | **Fix Claude**: NPE potencial en `StarlightBoltSpell.cast()` si `Targeting.raycast()` devuelve `null` (sin objetivo en rango) — añadido null-check. |
| 2026-09-11 | `./gradlew build` + `runGameTestServer` verificados por Claude de forma independiente tras cada fix; arranque limpio final con `astral_core`+`almanac_core`+`majestic` cargados juntos. |
| 2026-09-22 | **`expedition_core` + `geckolib` cableados como deps reales** (Claude, mecánico): jar de `expedition_core` beta.2 copiado a `libs/`, `compileOnly`+`localRuntime` en `build.gradle`, `geckolib` vía `implementation` (maven Cloudsmith, ya declarado), ambos `required` en `neoforge.mods.toml`. Motivo: `expedition_core` M1 (estructuras + `BossEncounter`) ya está publicado y es la base sobre la que se construirán las estructuras/jefes del Acto I–II. Verificado: `./gradlew build` + `runGameTestServer` con los 4 mods (`astral_core`+`almanac_core`+`expedition_core`+`majestic`) y GeckoLib cargando juntos, arranque limpio (mixins de `geckolib`/`regalia_slots_api` aplicados sin conflicto). **Proyecto CurseForge de `expedition_core` creado** (id `1707718`, slug `expedition-core`) y `majestic`'s `project_vars.md` actualizado con `relations` declarando los 4 requisitos (`astral-core`, `almanac-core`, `expedition-core`, `geckolib`) como `requiredDependency`, para que el launcher de CurseForge los instale automáticamente al instalar Majestic. |
| 2026-09-22 | **M2 implementado** (delegado a OpenCode `mimo-v2.5`, tras 3 intentos fallidos del mismo modelo por un problema nuevo — ver nota abajo —, verificado por Claude con 1 dependencia desactualizada corregida): `content/block/` (`AstralAltarBlock`+`AstralAltarBlockEntity extends astral_core.AltarBlockEntity`, `astral_pillar`), `content/component/MajesticDataComponents` (`recorded_spell`), `magic/ritual/` (`MajesticRituals`+`EngraveSpellRitual` — determina el hechizo a grabar por el sigilo en la mano secundaria, exige nodo `first_light` + página en blanco, consume esencia+items solo en la vía que ya no puede fallar), `content/event/MajesticEvents` (en `NodeUnlockedEvent` de `first_light`: concede el advancement de guía + entrega el libro), 4 sigilos + página en blanco (`MajesticItems`), datos: tag `#majestic:altar_pillar`, nodo `first_light` (JSON a mano + provider de datagen — quedó el JSON a mano sin usar, `ResearchNodeJsonProvider` es el que realmente corre), ritual `engrave_spell`, libro `majestic:almanac` (book+3 categorías+6 entradas+6 advancements, todo a mano). `FocusItem` reescrito: lee `recorded_spell` (default `starlight_bolt`), auto-desbloquea `first_light` en cada cast. `DataGenerators` arreglado (bug real de beta.1: JSON de hechizos se generaba en `data/majestic/spells/`, camino que `SpellLoader` de `almanac_core` nunca lee — corregido a `data/majestic/almanac/spell/`), + providers nuevos de ritual/nodo/bloques. **Bug real encontrado por Claude tras el build de OpenCode**: `majestic` seguía dependiendo del jar `almanac_core-...beta.1.jar` en `libs/` — beta.1 es anterior al paquete `guide/` (añadido en almanac_core beta.2, M3, esta misma sesión) — nunca se actualizó al cablear `almanac_core` en `majestic`. OpenCode se adaptó razonablemente a la ausencia (advancement con id construido a mano, sin entrega de libro), pero la causa real era una dependencia desactualizada, no una limitación real de `almanac_core`. Arreglado: jar actualizado a beta.2 en `libs/`, `almanac_core_version` en `gradle.properties`, y `MajesticEvents` reescrito para usar `EntryGate.advancementIdFor` + `VellumliBridge.giveBookStack` de verdad. Verificado: `./gradlew clean build` + `runGameTestServer` limpio con las 3 libs + GeckoLib. **Alcance**: solo altar T1 (T2 diferido a la estructura Observatorio); reagentes se leen del inventario del jugador, no de pedestales físicos (limitación real de `astral_core` M2, documentada en `§5b`); solo el nodo `first_light` (los 2-3 nodos adicionales de Acto I se añaden cuando exista la estructura Fallen Shrine que los justifique). Pendiente menor (cosmético, no bloqueante): el ítem de los 2 bloques nuevos usa `basicItem` (icono plano) en vez de heredar el modelo 3D del bloque. **Nota operativa sobre la delegación**: los 2 primeros intentos con `mimo-v2.5` fallaron por un problema nuevo — el modelo leyó los `CLAUDE.md` del repo (incluida la política "delega en OpenCode") e intentó sub-delegar recursivamente, atascándose contra el sandbox; el 3º intento con instrucciones anti-recursión parciales también se atascó intentando "verificar" firmas de API con `jar tf`/`mkdir /tmp` pese a que el prompt ya las daba exactas; el 4º intento (anti-recursión + "no verifiques nada externamente, las firmas ya son hechos") completó el hito con éxito. Detalle completo en memoria `opencode_run_hangs_zero_output.md` (Caso 10). |
