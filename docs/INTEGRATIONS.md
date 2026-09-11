# Majestic — Integraciones

> Matriz de dependencias e integraciones de todos los módulos del ecosistema.
> Política de autonomía en [`DESIGN_ECOSYSTEM.md §4`](DESIGN_ECOSYSTEM.md).

---

## 1. Matriz

| Mod | Tipo | Módulo(s) | Qué aporta | Gate en código | Licencia |
|---|---|---|---|---|---|
| `common_toolkit` | **real** | `astral_core`, `expedition_core` | config, red, registro dinámico, utilidades | dependencia dura (`neoforge.mods.toml` + gradle) | **MIT** (fork de Placebo) |
| `regalia_slots_api` | **real** | `astral_core` (reliquias) | ranuras equipables para reliquias (reimplementación de Curios) | dependencia dura; consumir `com.skd.regaliaslotsapi.api`, no el shim `top.theillusivec4`; **required dep** en CurseForge | **LGPL-3.0** |
| `vellumli` | **soft, solo datos** | `almanac_core` (puente de guía) | libro de guía in-game (fork propio de Patchouli) | `ModList.get().isLoaded("vellumli")` + libro por JSON; **cero código derivado** | CC BY-NC-SA 3.0 |
| **GeckoLib** | **real** (excepción aceptada) | `expedition_core`, `majestic` | modelos y animaciones de jefes y mobs complejos | dependencia dura | verificar (permite uso en mods cerrados) |
| **JEI** | **soft** (excepción aceptada) | `almanac_core` | categorías de receta (ritual, investigación, forja) | plugin `@JeiPlugin` cargado solo si JEI presente | MIT |

---

## 2. Prohibido integrar (lista negra)

No diseñar contra, no hacer `compat`, no gate, no receta cruzada con:

- Iron's Spellbooks
- Apotheosis / Apothic Enchanting / Apothic Attributes / Apothic Spawners
- Occultism
- Reliquary
- equivalent_legacy (EMC)
- ascendant_enchanting, ascendant_attributes
- Patchouli upstream (se usa `vellumli` en su lugar)

Cualquier añadido a la lista de integraciones **requiere aprobación explícita del propietario**
y se anota aquí con fecha.

---

## 3. Notas de licencia relevantes para integración

- **`vellumli` (CC BY-NC-SA 3.0)**: la cláusula *ShareAlike* solo obliga a obras **derivadas** de
  `vellumli`. `almanac_core` lo **consume** (API pública + JSON de libro), no lo deriva → `almanac_core`
  sigue MIT y `majestic` sigue ARR. Requisitos para que esto se mantenga:
  1. No copiar ni adaptar clases, assets ni estructuras de datos internas de `vellumli`.
  2. No empaquetar `vellumli` dentro de ningún jar (sin jar-in-jar). El usuario lo instala aparte.
  3. La integración debe degradar con elegancia si `vellumli` no está (la guía simplemente no se registra).
- **`majestic` ARR**: no se empaqueta **ninguna** dependencia. Todas se declaran como `required`/`optional`
  en `neoforge.mods.toml` y se resuelven en la instalación del usuario (ver E9 del ecosistema).
- **`regalia_slots_api` (LGPL-3.0)**: LGPL permite el enlace desde `majestic` (ARR) y desde
  `astral_core` (MIT) siempre que la librería vaya como **jar independiente** (nunca jar-in-jar),
  **sin modificar** dentro de nuestros repos, y con su fuente disponible (repo SKD). No copiar su
  código a `astral_core`; usar solo su API pública `com.skd.regaliaslotsapi.api`.

---

## 4. Historial de decisiones

| Fecha | Decisión |
|---|---|
| 2026-09-08 | Autonomía total: fuera Mystical Realms y todos los mods de contenido de terceros. Dependencias = `common_toolkit`, `regalia_slots_api`, `vellumli` + excepciones GeckoLib (real) y JEI (soft). |
| 2026-09-08 | `majestic` = All Rights Reserved; librerías = MIT. Sin bundling de dependencias. |
| 2026-09-10 | Licencias confirmadas: `common_toolkit` MIT, `regalia_slots_api` LGPL-3.0 (uso como jar aparte, sin modificar). `astral_core`, `expedition_core`, `almanac_core` = required deps en CurseForge; `jei`/`vellumli` = optional. |
