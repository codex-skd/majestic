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
