# Testing / Тестування / Testen

## English

### Running the tests

```bash
clj -M:test -e "(require 'clojure.test) (require 'nightcode.utils-test 'nightcode.builders-test 'nightcode.git-test 'nightcode.lein-test) (clojure.test/run-tests 'nightcode.utils-test 'nightcode.builders-test 'nightcode.git-test 'nightcode.lein-test)"
```

### Test suite

| Namespace | What it covers |
|---|---|
| `nightcode.utils-test` | `get-extension`, `sanitize-name`, `normalize-text-size`, `parent-path?`, `build-systems`, `remove-returns`, `get-relative-path`, `get-project-path`, and completeness of the `menu-translations` / `menu-item-translations` / `menu-prompt-translations` maps (every entry must have both `:en` and `:uk` text) |
| `nightcode.builders-test` | `create-pipes` structure and types |
| `nightcode.git-test` | `address->name` URL parsing |
| `nightcode.lein-test` | `get-project-clj-path` path construction |

### Latest results (2026-08-07)

```
Ran 12 tests containing 49 assertions.
0 failures, 0 errors.
```

### Known coverage gaps

Most of the codebase (`projects.clj`, `controller.clj`, `editors.clj`, `shortcuts.clj`, `state.clj`, `core.clj`) is JavaFX UI wiring, which is impractical to unit test without a running JavaFX toolkit. Test coverage is therefore focused on pure, side-effect-free helper functions across the `nightcode.*` namespaces.

---

## Українська

### Запуск тестів

```bash
clj -M:test -e "(require 'clojure.test) (require 'nightcode.utils-test 'nightcode.builders-test 'nightcode.git-test 'nightcode.lein-test) (clojure.test/run-tests 'nightcode.utils-test 'nightcode.builders-test 'nightcode.git-test 'nightcode.lein-test)"
```

### Набір тестів

| Неймспейс | Що покриває |
|---|---|
| `nightcode.utils-test` | `get-extension`, `sanitize-name`, `normalize-text-size`, `parent-path?`, `build-systems`, `remove-returns`, `get-relative-path`, `get-project-path`, а також повноту словників `menu-translations` / `menu-item-translations` / `menu-prompt-translations` (кожен запис має мати текст і `:en`, і `:uk`) |
| `nightcode.builders-test` | Структура та типи `create-pipes` |
| `nightcode.git-test` | Парсинг URL у `address->name` |
| `nightcode.lein-test` | Побудова шляху в `get-project-clj-path` |

### Останні результати (2026-08-07)

```
Ran 12 tests containing 49 assertions.
0 failures, 0 errors.
```

### Відомі прогалини в покритті

Більшість кодової бази (`projects.clj`, `controller.clj`, `editors.clj`, `shortcuts.clj`, `state.clj`, `core.clj`) — це JavaFX UI-логіка, яку непрактично юніт-тестувати без запущеного JavaFX-тулкіту. Тому тести зосереджені на чистих допоміжних функціях без побічних ефектів у неймспейсах `nightcode.*`.

---

## Deutsch

### Tests ausführen

```bash
clj -M:test -e "(require 'clojure.test) (require 'nightcode.utils-test 'nightcode.builders-test 'nightcode.git-test 'nightcode.lein-test) (clojure.test/run-tests 'nightcode.utils-test 'nightcode.builders-test 'nightcode.git-test 'nightcode.lein-test)"
```

### Test-Suite

| Namespace | Was abgedeckt wird |
|---|---|
| `nightcode.utils-test` | `get-extension`, `sanitize-name`, `normalize-text-size`, `parent-path?`, `build-systems`, `remove-returns`, `get-relative-path`, `get-project-path` sowie die Vollständigkeit der Maps `menu-translations` / `menu-item-translations` / `menu-prompt-translations` (jeder Eintrag muss sowohl `:en`- als auch `:uk`-Text haben) |
| `nightcode.builders-test` | Struktur und Typen von `create-pipes` |
| `nightcode.git-test` | URL-Parsing in `address->name` |
| `nightcode.lein-test` | Pfadaufbau in `get-project-clj-path` |

### Letzte Ergebnisse (2026-08-07)

```
Ran 12 tests containing 49 assertions.
0 failures, 0 errors.
```

### Bekannte Abdeckungslücken

Der Großteil der Codebasis (`projects.clj`, `controller.clj`, `editors.clj`, `shortcuts.clj`, `state.clj`, `core.clj`) besteht aus JavaFX-UI-Verdrahtung, die sich ohne laufendes JavaFX-Toolkit kaum sinnvoll unit-testen lässt. Die Testabdeckung konzentriert sich daher auf reine, nebenwirkungsfreie Hilfsfunktionen in den `nightcode.*`-Namespaces.
