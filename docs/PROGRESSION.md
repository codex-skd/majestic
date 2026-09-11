# Majestic — Progresión

> La espina del mod: qué desbloquea qué, en qué orden, y con qué curva. Referencia cruzada:
> [`CONTENT_MAGIC.md`](CONTENT_MAGIC.md), [`CONTENT_WORLD.md`](CONTENT_WORLD.md).
> **Estado**: borrador — números y nombres a rellenar (preguntas E1–E8 del ecosistema).

---

## 1. Bucle de juego

```
explorar estructura ──► reactivo/hallazgo ──► grabar NODO de constelación (investigación)
        ▲                                              │
        │                                              ▼
   nueva zona / acto  ◄── hechizo nuevo ◄──── RITUAL en altar (consume reactivos + esencia)
        │                                              │
        ▼                                              ▼
   JEFE de acto ──► material astral ──► RITUAL de forja ──► RELIQUIA ──► sinergias
```

Regla de oro: **cada capa tiene un gate que solo se cruza explorando** (una llave/material que
no se craftea, solo se encuentra).

---

## 2. Actos

| Acto | Ubicación | Gate de entrada | Contenido que abre | Jefe | Recompensa clave |
|---|---|---|---|---|---|
| **I — Despertar** | Overworld (estructura menor) | — (inicio: se obtiene el foco y el libro de guía) | Esencia básica, 1ª escuela, altar T1, 3–4 nodos iniciales | *(mini-jefe opcional)* | Fragmento estelar (llave del Acto II) |
| **II — Observatorio** | Overworld (estructura mayor) | Fragmento estelar | Altar T2, 2ª escuela, rituales de invocación, primeras reliquias T1 | Jefe I | Lente de éter (llave de la dimensión) |
| **III — Plano celeste** | Dimensión propia | Lente de éter (activa el portal/ritual de viaje) | Biomas celestes, estructuras de dimensión, altar T3, 3ª y 4ª escuela | Jefe II (+ jefe III intermedio) | Núcleo de constelación (llave del acto final) |
| **IV — Cénit** | Plano celeste (arena final) | Núcleo de constelación | Rituales de forja de reliquias T3, hechizos capstone | Jefe final | Reliquia(s) capstone + cierre de questline |

> Ajustar el nº de actos al alcance v1 (E5). Mínimo viable = Actos I–III.

---

## 3. Curva de esencia

| Hito | Capacidad de esencia | Fuentes de regen | Notas |
|---|---|---|---|
| Acto I | base | pasiva lenta | — |
| Acto II | +X | + altar cercano, + reliquia T1 | — |
| Acto III | +Y | + nodos de investigación, + bioma celeste | — |
| Acto IV | máx | + reliquia capstone | — |

Parámetros (config `majestic`): capacidad base, regen/seg, multiplicadores por fuente, coste
por tier de hechizo. Definir en `CONTENT_MAGIC.md §Esencia`.

---

## 4. Llaves de progresión (items-gate)

| Llave | Se obtiene en | Desbloquea | ¿Crafteable? |
|---|---|---|---|
| Fragmento estelar | Estructura Acto I (loot/cofre o mini-jefe) | Acto II | No |
| Lente de éter | Jefe I | Viaje al plano celeste | No |
| Núcleo de constelación | Jefe II / estructura Acto III | Acto IV | No |
| *(otras)* | | | |

---

## 5. Orden de implementación sugerido

1. **`astral_core`**: esencia + framework de casting (sin contenido).
2. **`almanac_core`**: códecs de `spell` + loader datapack + (stub) JEI.
3. **`majestic`**: 1ª escuela, 3–4 hechizos, foco, HUD de esencia. → *jugable mínimo*.
4. **`astral_core`**: motor de rituales + investigación.
5. **`almanac_core`**: códecs `ritual` / `research_node` + puente `vellumli`.
6. **`majestic`**: altar T1/T2, nodos Acto I–II, guía.
7. **`expedition_core`**: estructuras + `BossEncounter`.
8. **`majestic`**: estructuras Acto I–II, Jefe I.
9. **`expedition_core`**: bootstrap de dimensión.
10. **`majestic`**: Plano celeste, Actos III–IV, reliquias, jefes finales.

---

## 6. Alcance v1 (baseline aceptado — E5)

Objetivo para la **primera release estable**. Números orientativos, ajustables a la baja si hace
falta cerrar antes, nunca al alza sin acordarlo.

| Elemento | v1 | Notas |
|---|---|---|
| Escuelas | **3** (Luz Estelar, Éter, Gravedad) | Augurio queda para v1.x |
| Hechizos | **12–16** (~4–5 por escuela, tiers 1–3) | |
| Altares | **3 tiers** | T1/T2 en Overworld, T3 en Plano celeste |
| Rituales | **8–10** | grabado, transmutación, 2 invocaciones, forja de reliquia, viaje a dimensión, +2 |
| Nodos de investigación | **15–20** | cubren el desbloqueo de escuelas + hechizos + rituales |
| Reliquias | **6–8** | 2 sets con sinergia + piezas sueltas |
| Dimensión | **1** (`majestic:celestial_plane`) | 3 biomas |
| Estructuras | **4** | 1 Acto I, 1 Acto II (con arena), 1 Acto III, 1 Acto IV (ciudadela) |
| Jefes | **3** | Jefe I (Acto II), Jefe II (Acto III), Jefe final (Acto IV) |
| Mobs no-jefe | **4–5** | ambiente + guardianes de estructura |
| Guía | 1 libro-guía + **2–3 tomos de avance** | ver §7 |

Fuera de v1 (backlog): escuela Augurio, party/estado compartido, instancing de arena, integraciones
extra, jefe intermedio del Acto III.

---

## 7. Guía y tomos de avance (E6)

### Libro-guía principal — `majestic:almanac` (working title "Almanaque celeste")
- **Se obtiene de dos formas**: crafteable (receta barata, papel + algo temático) **y** entregado
  automáticamente la primera vez que el jugador coge el foco / entra al mundo con el mod.
- **Contenido progresivo**: las entradas empiezan bloqueadas y se **revelan al avanzar**
  (desbloquear el nodo, completar el ritual, matar al jefe, entrar a la dimensión). Implementación:
  `almanac_core` genera las entradas desde las `*Definition`; el gating de visibilidad se ata a
  `astral_core.research` / advancements. Motor de render: `vellumli` (soft).
- Estructura: capítulo por Acto + sub-sección por escuela (ver `LORE.md §5`).

### Tomos de avance — libros separados por misión
- Ítems propios (`majestic:tome_*`), **no** se craftean: se consiguen completando **misiones**
  concretas (cadena de advancements con objetivo explícito: "derrota al Guardián sin recibir daño
  de luz", "activa los 3 santuarios de un bioma", "forja tu primera reliquia", etc.).
- Cada tomo aporta: entradas de lore extra + **desbloquea un nodo/ritual/hechizo que la guía
  principal no da** (contenido opt-in, no bloquea la progresión crítica).
- v1: **3 tomos — CONFIRMADO (2026-09-10)**. Misiones aceptadas. **Todas las recompensas son
  contenido opcional**: ningún tomo es necesario para completar la progresión crítica ni el 100%
  de los Actos.

  | Tomo | id | Misión | Recompensa (opcional) |
  |---|---|---|---|
  | Tomo de los Observadores | `majestic:tome_of_the_watchers` | Encontrar y leer las 4 estelas de la Orden repartidas por estructuras del Acto I–II | Nodo oculto: pasiva de regen de esencia extra bajo cielo estrellado |
  | Tomo del Vacío | `majestic:tome_of_the_hollow` | Sobrevivir X segundos seguidos en una zona de eclipse del Plano celeste | Hechizo de Éter T3 alternativo (variante de teletransporte/fase) |
  | Tomo de la Primera Luz | `majestic:tome_of_the_first_light` | Completar todos los rituales de invocación del mod | Ritual de forja de una reliquia capstone **alternativa** (la capstone base sale por la ruta normal) |

  Parámetros a afinar en implementación: nº de estelas y su reparto, X segundos de eclipse,
  stats exactos de la pasiva / hechizo / reliquia alternativa.

### Multiplayer
Guía y tomos son **por jugador**: cada uno revela sus entradas según su propio progreso; los tomos
son ítems y se pueden dar/intercambiar, pero el desbloqueo que otorgan es por jugador al leerlos.
