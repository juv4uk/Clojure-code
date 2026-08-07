<div align="center">
  <img src="graphics/banner.svg" alt="Clojure-code Banner" width="800">
</div>

[![Build Status](https://github.com/juv4uk/Clojure-code/actions/workflows/release.yml/badge.svg)](https://github.com/juv4uk/Clojure-code/actions/workflows/release.yml)
[![GitHub release](https://img.shields.io/github/release/juv4uk/Clojure-code.svg)](https://github.com/juv4uk/Clojure-code/releases/latest)
![screenshot](screenshot.png)

**A modernized Clojure/ClojureScript IDE · Модернізована IDE для Clojure/ClojureScript · Eine modernisierte Clojure/ClojureScript-IDE**

[English](#english) · [Українська](#українська) · [Deutsch](#deutsch)

`Clojure-code` is a modernized fork of [Nightcode](https://github.com/oakes/Nightcode), updated with modern dependencies, modern JavaFX, and Java 21 for significantly faster startup and better performance.

`Clojure-code` — це модернізований форк [Nightcode](https://github.com/oakes/Nightcode), з оновленими залежностями, сучасним JavaFX і Java 21 для значно швидшого запуску та кращої продуктивності.

`Clojure-code` ist ein modernisierter Fork von [Nightcode](https://github.com/oakes/Nightcode), mit aktualisierten Abhängigkeiten, modernem JavaFX und Java 21 für deutlich schnelleren Start und bessere Performance.

---

## English

### Installation

Go to the [Releases](https://github.com/juv4uk/Clojure-code/releases) page and download the appropriate `.jar` file for your operating system.
Run it with:
```bash
java -jar clojure-code-<os>.jar
```

### Development

* Install **JDK 21** or above
* Install [the Clojure CLI tool](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools)
* To develop: `clj -M:dev`
* To build the ClojureScript files: `clj -M:cljs`
* To run the tests: see [TESTING.md](TESTING.md)
* To build the uberjar for each OS locally:
  * `clj -M:prod uberjar windows`
  * `clj -M:prod uberjar macos`
  * `clj -M:prod uberjar linux`

### Automated Releases

This project is configured with GitHub Actions. To publish a new release for all platforms:
1. Create a new tag (e.g. `v1.0.1`): `git tag -a v1.0.1 -m "Release 1.0.1"`
2. Push the tag: `git push origin v1.0.1`
3. GitHub Actions will automatically build the `.jar` files and attach them to a new GitHub Release.

### Licensing

All files that originate from this project are dedicated to the public domain. I would love pull requests, and will assume that they are also dedicated to the public domain.

---

## Українська

### Встановлення

Перейдіть на сторінку [Releases](https://github.com/juv4uk/Clojure-code/releases) та завантажте потрібний `.jar` файл для вашої операційної системи.
Запустіть його командою:
```bash
java -jar clojure-code-<os>.jar
```

### Розробка

* Встановіть **JDK 21** або новіше
* Встановіть [Clojure CLI tool](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools)
* Для розробки: `clj -M:dev`
* Для збірки файлів ClojureScript: `clj -M:cljs`
* Для запуску тестів: див. [TESTING.md](TESTING.md)
* Для локальної збірки uberjar під кожну ОС:
  * `clj -M:prod uberjar windows`
  * `clj -M:prod uberjar macos`
  * `clj -M:prod uberjar linux`

### Автоматичні релізи

Проєкт налаштований на GitHub Actions. Щоб опублікувати новий реліз для всіх платформ:
1. Створіть новий тег (напр. `v1.0.1`): `git tag -a v1.0.1 -m "Release 1.0.1"`
2. Запуште тег: `git push origin v1.0.1`
3. GitHub Actions автоматично збере `.jar` файли та прикріпить їх до нового GitHub Release.

### Ліцензія

Усі файли, що походять з цього проєкту, передані в суспільне надбання (public domain). Буду радий pull request'ам і вважатиму, що вони також передані в суспільне надбання.

---

## Deutsch

### Installation

Gehe zur Seite [Releases](https://github.com/juv4uk/Clojure-code/releases) und lade die passende `.jar`-Datei für dein Betriebssystem herunter.
Starte sie mit:
```bash
java -jar clojure-code-<os>.jar
```

### Entwicklung

* **JDK 21** oder höher installieren
* [Das Clojure-CLI-Tool](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools) installieren
* Zum Entwickeln: `clj -M:dev`
* Um die ClojureScript-Dateien zu bauen: `clj -M:cljs`
* Um die Tests auszuführen: siehe [TESTING.md](TESTING.md)
* Um das Uberjar lokal für jedes Betriebssystem zu bauen:
  * `clj -M:prod uberjar windows`
  * `clj -M:prod uberjar macos`
  * `clj -M:prod uberjar linux`

### Automatisierte Releases

Dieses Projekt ist mit GitHub Actions konfiguriert. So veröffentlichst du ein neues Release für alle Plattformen:
1. Neuen Tag erstellen (z. B. `v1.0.1`): `git tag -a v1.0.1 -m "Release 1.0.1"`
2. Tag pushen: `git push origin v1.0.1`
3. GitHub Actions baut automatisch die `.jar`-Dateien und hängt sie an ein neues GitHub Release an.

### Lizenz

Alle Dateien, die aus diesem Projekt stammen, sind der Public Domain gewidmet. Ich freue mich über Pull Requests und gehe davon aus, dass auch diese der Public Domain gewidmet sind.
