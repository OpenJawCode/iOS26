# launcher/

The iOS-style home experience — a systemless priv-app (ADR-0008). Bounded contexts: Springboard, App Library, Spotlight, Widget Host, Control Center host.

| Module | Context | Responsibility |
|---|---|---|
| `springboard` | Springboard | Grid, pages, dock, folders, jiggle-edit, context menus |
| `app-library` | App Library | Auto-categorized app grid + category data layer |
| `spotlight` | Spotlight | Apps + contacts + actions search, offline |
| `widgets` | Widget Host | AppWidgetHost, slots, tinting/framing, add flow |
| `control-center` | Control Center (host) | Overlay window service + panel; consumes hook events (ADR-0005/0019) |

Owners: springboard-owner, app-library-owner, spotlight-owner, widgets-owner, control-center-owner (AGENTS.md §2). Conventions: docs/conventions.md §1–2. Tests: Tier 2 screenshots + flows for every screen.
