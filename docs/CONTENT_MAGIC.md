# Majestic — Contenido: magia

> Escuelas, esencia, hechizos, rituales, nodos de investigación y reliquias **concretos**.
> Los frameworks que dan soporte a todo esto viven en `astral_core`; los formatos de datos en
> `almanac_core`. Aquí solo van las entradas concretas de Majestic.
> **Estado**: borrador — tablas para rellenar.

---

## 1. Esencia

| Parámetro | Valor (config) | Notas |
|---|---|---|
| ¿Recurso único o por-escuela? | **Único global — CONFIRMADO (2026-09-10)** | Un solo attachment a sincronizar, HUD simple, balance predecible en multiplayer. La identidad de escuela se da por coste/tempo distintos sobre la misma barra. |
| Capacidad base | *(pendiente)* | p. ej. 100 |
| Regen pasiva | *(pendiente)* | por segundo; menor en combate |
| Fuentes de regen/capacidad extra | altar cercano · bioma celeste · reliquia · nodo de investigación | multiplicadores aditivos vía `EssenceModifier` de `astral_core` |
| Penalización a 0 | *(pendiente)* | no se puede lanzar; opción: "sobrecarga" = lanzar en déficit inflige *backlash* (daño + fatiga) |
| Drenaje ambiental | zonas de "eclipse" en el Plano celeste | resta regen o capacidad temporal |

### HUD de esencia (propuesta para E2)

HUD propio de `majestic` (`client/hud/`), compacto, activable/posicionable por config CLIENT.
Elementos, por prioridad:

1. **Barra + valor numérico** de esencia actual / máx.
2. **Indicador de regen**: pequeña flecha/tick que muestra si está subiendo y a qué ritmo (o si está drenando, en rojo).
3. **Escuela activa / hechizo grabado** en el foco: icono + nombre corto.
4. **Cooldown** del hechizo activo: segmento o radial sobre el icono.
5. **Estado contextual** (iconos que aparecen solo cuando aplican):
   - "En rango de altar" (bonus de regen).
   - "Bioma celeste" (bonus).
   - "Eclipse / Vacío" (drenaje, aviso).
   - "Sobrecarga" (esencia en déficit, riesgo de backlash).
6. *(opcional v2)* mini-lista de reliquias equipadas con cooldown de su activo.

**Comportamiento (CONFIRMADO 2026-09-10):**
- **Formato**: widget propio en **una esquina** (ancla y escala configurables), con la barra +
  iconos 2–5 agrupados. No barra estilo vanilla sobre la hotbar.
- **Visibilidad**: solo cuando el jugador **empuña el foco** (main/off-hand). Fuera de eso, oculto.
  *(Config CLIENT podrá forzar "siempre" u "oculto", pero el defecto es "al empuñar".)*
- Elemento 6 (reliquias) → v2.

Multiplayer: el HUD solo lee estado sincronizado del cliente local; nada de polling al servidor.
Config CLIENT: modo de visibilidad (al empuñar / siempre / oculto), ancla (esquina), escala,
mostrar/ocultar elementos 2–5.

---

## 2. Escuelas

Cuatro escuelas celestes. Cada una: fantasía, rol mecánico, recurso/tono visual, hechizo capstone.

| Escuela | Fantasía | Rol | Paleta / VFX | Capstone |
|---|---|---|---|---|
| **Luz Estelar** | Rayos, faros, purificación | Daño directo / anti-no-muertos | Blanco-dorado | *(pendiente)* |
| **Éter** | Vacío entre estrellas, teletransporte, fase | Movilidad / utilidad / control | Violeta-índigo | *(pendiente)* |
| **Gravedad** | Atracción, colapso, peso de los astros | Control de masas / CC / AoE | Azul profundo | *(pendiente)* |
| **Augurio** | Leer el cielo, predecir, marcar | Buffs / detección / debuffs | Cian claro | *(pendiente)* |

> Ajustar nombres/número al alcance v1 (E5). Mínimo = 2 escuelas para la 1ª release jugable.

---

## 3. Hechizos

Formato de datos: `almanac_core` `spell` (esquema en `design/DESIGN_ALMANAC_CORE.md`).

| id | Escuela | Tier | Coste esencia | Tipo (proyectil/toque/área/self/canal) | Efecto | Cooldown | Desbloqueo (nodo/ritual) |
|---|---|---|---|---|---|---|---|
| `majestic:starlight_bolt` | Luz Estelar | 1 | | proyectil | daño + ceguera breve | | nodo inicial |
| `majestic:blink` | Éter | 1 | | self | dash corto | | nodo inicial |
| `majestic:...` | | | | | | | |

Reglas de balance: coste ∝ tier; los T3 requieren estar en el plano celeste o cerca de altar T3.

**Foco (J2):** un único ítem-foco. El ritual `engrave_spell` fija qué hechizo desbloqueado está
"grabado" y activo; se cambia repitiendo el ritual. El HUD muestra el hechizo grabado actual.
Guardar varias grabaciones y rotar entre ellas = posible mejora v1.x.

---

## 4. Rituales

Altar multibloque de `astral_core`. Tiers de altar → tiers de ritual.

### Altares

| Tier | Multibloque (tamaño) | Bloques clave | Dónde se construye | Rituales que habilita |
|---|---|---|---|---|
| T1 | | pedestal + anillo | Overworld | grabado de hechizos T1, transmutaciones básicas |
| T2 | | + pilares astrales | Overworld (Observatorio) | invocación, hechizos T2, reliquias T1 |
| T3 | | + núcleo de constelación | Plano celeste | hechizos T3, forja de reliquias T2–T3 |

### Rituales

| id | Tier | Reactivos (items en pedestales) | Coste esencia | Resultado | Duración | Riesgo de fallo |
|---|---|---|---|---|---|---|
| `majestic:engrave_spell` | 1 | página en blanco + reactivo de escuela | | graba un hechizo desbloqueado en el foco | | bajo |
| `majestic:summon_...` | 2 | | | invoca entidad aliada temporal | | medio |
| `majestic:forge_relic_...` | 3 | material astral + hechizo-componente | | crea reliquia (**esto es la "forja": un `RitualType`, no un `RecipeType` — E4**) | | alto (consume reactivos al fallar) |

---

## 5. Investigación — nodos de constelación

Grafo de `astral_core`. Cada nodo: requisito (hallazgo/estructura/acción) → desbloquea hechizos/rituales.

| Nodo | Constelación | Requisito para desbloquear | Desbloquea |
|---|---|---|---|
| `majestic:first_light` | — | obtener el foco | escuela Luz Estelar T1, altar T1 |
| `majestic:the_void_path` | Éter | visitar bioma X / usar lente | escuela Éter T1 |
| `majestic:...` | | | |

Persistencia: **por jugador**, estado en el servidor y sincronizado al cliente (attachment de
`astral_core`). Full multiplayer (E7): cada jugador tiene su propio grafo desbloqueado; los altares
y la dimensión son compartidos, pero grabar un hechizo o desbloquear un nodo solo afecta a quien
ejecuta la acción. Sin estado de "party" en v1.

---

## 6. Reliquias

Equipables vía `regalia_slots_api` (reimplementación de Curios, LGPL-3.0). **E3 resuelto:**

- **Reliquias generales** → slot `charm` (ya existe en `regalia_slots_api`).
- **Reliquia(s) capstone** → slot propio **`majestic:astral`**, registrado por
  `data/majestic/curios/slots/astral.json` (`order`, `icon`, `validators: ["regalia_slots_api:tag"]`).
- Cada ítem-reliquia implementa `ICurioItem` (lo cablea `astral_core` a partir de la
  `RelicDefinition`) y se asigna a su slot con los tags `#regalia_slots_api:<slot>` **y**
  `#curios:<slot>` (paridad de validador, ver beta.13 de `regalia_slots_api`).
- No se registran más slots nuevos en v1.

| id | Slot | Pasivo | Activo (si tiene) | Sinergias | Cómo se obtiene |
|---|---|---|---|---|---|
| `majestic:astrolabe` | amuleto | +regen esencia | marcar objetivo (Augurio) | +daño con hechizos de Augurio | forja T2 |
| `majestic:gravity_anchor` | anillo | reduce knockback | pulso de gravedad | con `astrolabe`: pulso marca | forja T3 |
| `majestic:...` | | | | | |

Sinergias: efecto adicional cuando N reliquias de un set están equipadas a la vez (API de
sinergias en `astral_core`).
