# Majestic — Contenido: mundo y aventura

> Dimensión, biomas, estructuras, mobs, jefes, worldgen, botín y questline **concretos**.
> Frameworks de soporte: `expedition_core`. **Estado**: borrador — tablas para rellenar.

---

## 1. El Plano celeste (dimensión)

| Campo | Valor |
|---|---|
| id de dimensión | `majestic:celestial_plane` |
| Display | ES "Plano celeste" · EN "Celestial Plane" |
| Lore | *(ver `LORE.md`)* |
| Acceso | ritual de viaje T2 con la Lente de éter (no portal de bloques clásico) — helper de `expedition_core` |
| Retorno | ítem/ritual de vuelta; ancla de retorno al punto de entrada |
| Cielo / iluminación | sin sol/luna vanilla; cielo estrellado permanente, luz ambiental media |
| Física | *(¿gravedad reducida? ¿islas flotantes? decisión de diseño)* |
| Peligros ambientales | *(caída al vacío, zonas de "eclipse" que drenan esencia, etc.)* |
| Multiplayer (E7) | Dimensión **compartida** (un solo `Level`, no instanciada por jugador). Estructuras persistentes. Los jefes de arena escalan por nº de jugadores dentro (`expedition_core`). Sin instancing en v1. |

---

## 2. Biomas

| Bioma | Aspecto | Mobs | Recursos | Estructuras asociadas |
|---|---|---|---|---|
| `majestic:starfields` | llanuras de polvo estelar, flora luminiscente | | mineral astral menor | santuarios pequeños |
| `majestic:void_reaches` | islas fragmentadas sobre el vacío | | éter cristalizado | puentes/ruinas |
| `majestic:the_cenit` | cumbre, arena del jefe final | — | — | ciudadela final |

---

## 3. Estructuras

| id | Dimensión | Acto | Generación | Contenido / botín clave | Mobs |
|---|---|---|---|---|---|
| `majestic:fallen_shrine` | Overworld | I | dispersa, superficie | página en blanco, pistas de nodo, Fragmento estelar | — |
| `majestic:observatory` | Overworld | II | rara, jigsaw | reactivos de escuela, plano de altar T2, arena de Jefe I | cultistas/constructos |
| `majestic:astral_spire` | Plano celeste | III | jigsaw | núcleo de constelación, altar T3 parcial | constructos celestes |
| `majestic:cenit_citadel` | Plano celeste | IV | única/estructura ancla | arena Jefe final, reliquia capstone | guardianes |

Utilidades jigsaw y colocación: `expedition_core`. Inyección de loot en cofres vanilla: solo si
aporta pistas de progresión, nunca dependencias de terceros.

---

## 4. Mobs (no jefes)

| id | Dónde | Rol | Notas de IA / drops | Modelo |
|---|---|---|---|---|
| `majestic:star_moth` | biomas celestes | ambiente/pasivo | polvo estelar | JSON simple |
| `majestic:ether_wisp` | void_reaches | hostil ligero, vuela | éter cristalizado | GeckoLib ligero |
| `majestic:astral_construct` | estructuras | hostil medio, guardián | fragmentos | GeckoLib |

---

## 5. Jefes

Framework `BossEncounter` de `expedition_core` (barra, fases, bloqueo de arena, botín, música).
Render: **GeckoLib**.

| id | Acto | Arena | Fases | Mecánicas clave | Botín | Notas de modelo/anim |
|---|---|---|---|---|---|---|
| `majestic:warden_of_the_gate` | II | Observatory | 2 | invoca constructos; a <50% cubre la sala de luz | Lente de éter, mat. astral | GeckoLib, ~6 anim |
| `majestic:the_hollow_star` | III | Astral Spire | 3 | gravedad (atrae al jugador), lluvia de meteoros telegrafiada | núcleo parcial | GeckoLib |
| `majestic:the_first_constellation` | IV | Cenit Citadel | 3 + add-phase | reconfigura la arena por "signos"; requiere reliquias para mitigar | reliquia capstone, cierre questline | GeckoLib, set completo |

---

## 6. Worldgen técnico

- **Features**: flora luminiscente, cristales de éter, cráteres de meteorito (con mineral).
- **Minerales/nodos**: mineral astral (superficie plano celeste), éter cristalizado (void_reaches),
  nodo de constelación (solo en `astral_spire`).
- **Jigsaw pools**: por estructura, definidas por datapack en `majestic`.
- **Spawns**: listas de spawn por bioma celeste; sin spawns de Majestic en dimensiones vanilla
  salvo los mobs de estructura de Acto I–II.

---

## 7. Botín y questline

- **Advancements** (`majestic:...`): cadena que refleja los Actos (I→IV) + hitos (1er hechizo,
  1er ritual, 1ª reliquia, entrar al plano celeste, cada jefe).
- **Tablas de botín**: una por estructura + una por jefe. Sin referencias a items de terceros.
- **Questline v1** = la cadena de advancements + entradas de guía. Sin sistema de quests dedicado.
