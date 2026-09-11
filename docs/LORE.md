# Majestic — Ambientación (lore)

> Marco narrativo para dar coherencia a nombres de bloques, hechizos, estructuras y textos de
> guía. No es canon rígido: sirve para que el contenido "suene" a lo mismo.
> **Estado**: borrador.

---

## 1. Premisa

El cielo nocturno no es decoración: es un **mapa de poder**. Las constelaciones son estructuras
reales de un plano superior — el **plano celeste** — y su luz, al ser "leída" y grabada mediante
rituales, concede a quien la estudia el dominio de una **escuela** de magia astral.

Hace mucho, una orden de observadores construyó torres y santuarios para cartografiar ese cielo.
La orden desapareció; quedaron sus ruinas, sus instrumentos rotos y un camino de vuelta al plano
celeste que solo se reabre a quien reúne las tres llaves: **fragmento estelar**, **lente de éter**
y **núcleo de constelación**.

En el plano celeste algo consume las estrellas desde dentro — el **Vacío** — y apaga
constelaciones una a una. El acto final enfrenta a **la Primera Constelación**, guardiana o
verdugo según se la lea.

---

## 2. Cosmología

| Concepto | Descripción |
|---|---|
| **Esencia** | Fluido de luz astral que el mago acumula y gasta al lanzar. Se regenera bajo cielo abierto y cerca de altares. |
| **Constelación** | Patrón del plano celeste. En juego: nodo de investigación + fuente de una escuela. |
| **Altar** | Instrumento de la orden para "grabar" luz en un foco. Tres tiers = tres grados de la orden. |
| **Plano celeste (Empyrea / *nombre E1*)** | Dimensión donde las constelaciones son terreno físico. Islas sobre el Vacío. |
| **El Vacío** | Amenaza: ausencia que devora luz. Mecánicamente: zonas que drenan esencia, el enemigo final. |
| **Reliquia** | Instrumento de la orden condensado en objeto portable. Sinergias = piezas de un mismo juego de instrumentos. |

---

## 3. Escuelas (tono narrativo)

| Escuela | Voz / tema |
|---|---|
| **Luz Estelar** | Faro, juicio, revelar lo oculto. Textos solemnes. |
| **Éter** | El espacio entre estrellas; viaje, ausencia, silencio. Textos crípticos y breves. |
| **Gravedad** | El peso de los astros; inevitabilidad, atracción, colapso. Textos graves. |
| **Augurio** | Leer antes de que ocurra; marca, presagio, certeza. Textos en condicional. |

---

## 4. Glosario de nombres (coherencia)

Usar estos términos de forma consistente en assets y guía (ES / EN):

| ES | EN | Uso |
|---|---|---|
| esencia | essence | recurso |
| constelación | constellation | nodo / escuela |
| altar astral | astral altar | multibloque de ritual |
| foco | focus | ítem que canaliza hechizos |
| plano celeste | celestial plane / Empyrea | dimensión |
| Vacío | the Void (Hollow) | amenaza |
| reliquia | relic | equipable |
| fragmento estelar / lente de éter / núcleo de constelación | star fragment / ether lens / constellation core | llaves de acto |
| la Orden de los Observadores | the Order of Watchers | facción histórica (desaparecida) |

---

## 5. Textos de guía (estructura)

**Libro-guía principal — "Almanaque celeste"** (`majestic:almanac`, motor `vellumli`): estructurado
por Actos, con una sub-sección por escuela. Cada entrada: 1–2 párrafos de lore + la mecánica
concreta + receta/ritual asociado. Las entradas se **revelan al avanzar** (nodo desbloqueado,
ritual hecho, jefe caído). Tono: cuaderno de campo de un miembro tardío de la Orden de los
Observadores.

**Tomos de avance** (`majestic:tome_*`): libros separados que se ganan completando misiones
concretas. Cada uno mezcla lore "profundo" (voz de un Observador distinto, más antiguo o más roto)
con el desbloqueo de contenido opcional. No son parte de la progresión crítica: son la recompensa
de leer el cielo más de cerca de lo prudente. Ver `PROGRESSION.md §7`.
