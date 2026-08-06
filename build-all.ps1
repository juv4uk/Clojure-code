$ErrorActionPreference = "Stop"

$proj = Get-Content project.clj | Out-String
if ($proj -match '\(defproject\s+\S+\s+"([^"]+)"') {
    $version = $matches[1]
} else {
    $version = "1.0.0"
}
Write-Host "Detected version: $version"

$distDir = "dist"
if (-not (Test-Path $distDir)) {
    New-Item -ItemType Directory -Path $distDir | Out-Null
}

# Each `clj -M:prod uberjar` invocation runs `lein clean` first, which wipes
# the entire target/ directory. Artifacts must be copied out to $distDir
# right after each build or the next platform's build deletes them.

Write-Host "Building for Windows..."
clj -M:prod uberjar windows
Move-Item -Path "target\clojure-code-$version-standalone.jar" -Destination "target\clojure-code-windows-v$version.jar" -Force
if (Get-Command jpackage -ErrorAction SilentlyContinue) {
    Write-Host "Creating Windows executable with jpackage..."
    if (Test-Path "Clojure-code-$version") {
        Remove-Item -Recurse -Force "Clojure-code-$version"
    }
    jpackage --type app-image --name "Clojure-code-$version" --app-version $version --input target/ --main-jar clojure-code-windows-v$version.jar --main-class nightcode.start --icon graphics/icon.ico
}
Copy-Item -Path "target\clojure-code-windows-v$version.jar" -Destination "$distDir\clojure-code-windows-v$version.jar" -Force

Write-Host "Building for macOS..."
clj -M:prod uberjar macos
Move-Item -Path "target\clojure-code-$version-standalone.jar" -Destination "target\clojure-code-macos-v$version.jar" -Force
Copy-Item -Path "target\clojure-code-macos-v$version.jar" -Destination "$distDir\clojure-code-macos-v$version.jar" -Force

Write-Host "Building for Linux..."
clj -M:prod uberjar linux
Move-Item -Path "target\clojure-code-$version-standalone.jar" -Destination "target\clojure-code-linux-v$version.jar" -Force
Copy-Item -Path "target\clojure-code-linux-v$version.jar" -Destination "$distDir\clojure-code-linux-v$version.jar" -Force

Write-Host "Build complete! Artifacts in .\$distDir"
