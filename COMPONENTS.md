# COMPONENTS.md — Component Library & Springboard Specification

> Every component derives ALL visual values from tokens (`LocalTokenSet` + `Tokens.*`).
> No duplicated constants, colors, or animation definitions. Components support future theming
> automatically (semantic tokens resolve per mode/glass intensity). Pure foundation — no material3 (D-P2.3).

## 1. Component inventory (v1, all implemented in `libs/design/components`)

| Component | Token sources | Notes |
|---|---|---|
| `Button` | accent, radius.control, type.Body, touchTarget, state.pressedOverlay, motion.spring.snappy, haptics.medium | filled/outline variant; press scale 0.97 on press |
| `Switch` | accent, radius, motion.spring.standard, haptics.selection | 51×31dp iOS proportions |
| `Slider` | accent, backgroundTertiary, touchTarget | iOS 4dp track |
| `Toggle` | accent, radius.small, haptics.selection | check/plus row item |
| `Card` | GlassPanel pipeline (ADR-0028), radius.card, elevation, type | glass card |
| `ListItem` | labelPrimary/Secondary, separators | settings-row shape |
| `Sheet` | radius.sheet (top), sheetBackground, grabber | token grabber bar |
| `Popover` | GlassPanel pipeline (ADR-0028), radius.card | scales from trigger (emil doctrine) |
| `NavigationBar` | GlassPanel pipeline (ADR-0028), accent, labelSecondary | iOS tab bar |
| `Dock` | GlassPanel pipeline (ADR-0028), radius.largeCard, grid.dock* | springboard dock |
| `QuickSettingsTile` | accent (active), glassFill, radius.card | CC grid tile |
| `ControlCenterCard` | GlassPanel pipeline (ADR-0028), radius.largeCard, labelSecondary | CC card w/ content |
| `Notification` | GlassPanel pipeline (ADR-0028), radius.card, labelPrimary/Secondary | banner |
| `ContextMenu` | GlassPanel pipeline (ADR-0028), radius.card, labelPrimary | action list |
| `AppIcon` | grid.iconSize, radius.squircleFactor, type.Caption1 | squircle per iOS ratio |
| `SearchField` | backgroundTertiary, radius.pill, labelTertiary | Spotlight field |
| `Folder` | GlassPanel pipeline (ADR-0028), radius.largeCard, grid.folder* | 3×3 grid |
| `WidgetFrame` | GlassPanel pipeline (ADR-0028), radius.largeCard | non-tinting widget framing (survey R8) |
| `LockScreenComponent` | type.LargeTitle/Footnote, labelPrimary/Secondary | clock+date scaffold |
| `Toggle`+`Slider`+… | — | interactive in Gallery |

## 2. Springboard specification

Derived from iOS SpringBoard, adapted to the reference device (1080×2400 @ ~446dpi, 20:9):

| Property | Token | Value |
|---|---|---|
| Columns | `grid.springboardColumns` | 6 (portrait) |
| Icon size | `grid.iconSize` | 60dp |
| Icon radius | `radius.squircleFactor` × iconSize | 13.4dp (iOS ≈ 22.4%) |
| Horizontal gutter | `grid.gutter` | 24dp |
| Page margin | `grid.margin` | 20dp |
| Rows | derived | floor((screenH − dock − margins) / (icon + iconGap)) |
| Dock | `grid.dockHeight` 88dp, `dockMargin` 12dp, `dockIconSize` 60dp | 4–6 icons, glass |
| Folders | `folderColumns/Rows` 3×3, `folderGap` 10dp | |
| Page dots | `pageDotsSize` 7dp, `pageDotsGap` 8dp | bottom, above dock |
| App Library | `appLibraryColumns` 5 | auto-categories (Phase 4) |

Grid math is a pure function of tokens — density/rotation changes recompute automatically.
Full springboard layout lands in Phase 3 (this phase defines the language, not the screen).

## 3. Theming guarantee

Because components read only semantic tokens, a new theme (iOS 27 token set, user accent,
dynamic wallpaper accent) is a `TokenSet` swap — zero component changes (ADR-0011/0022).
