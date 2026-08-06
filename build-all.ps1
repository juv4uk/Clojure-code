$ErrorActionPreference = "Stop"

$proj = Get-Content project.clj | Out-String
if ($proj -match '\(defproject\s+\S+\s+"([^"]+)"') {
    $version = $matches[1]
} else {
    $version = "1.0.0"
}
Write-Host "Detected version: $version"

Write-Host "Building for Windows..."
clj -M:prod uberjar windows
Move-Item -Path "target\clojure-code-$version-standalone.jar" -Destination "target\clojure-code-windows.jar" -Force
if (Get-Command jpackage -ErrorAction SilentlyContinue) {
    Write-Host "Creating Windows executable with jpackage..."
    jpackage --type app-image --name "Clojure-code-$version" --app-version $version --input target/ --main-jar clojure-code-windows.jar --main-class nightcode.start --icon graphics/icon.ico
}

Write-Host "Building for macOS..."
clj -M:prod uberjar macos
Move-Item -Path "target\clojure-code-$version-standalone.jar" -Destination "target\clojure-code-macos.jar" -Force

Write-Host "Building for Linux..."
clj -M:prod uberjar linux
Move-Item -Path "target\clojure-code-$version-standalone.jar" -Destination "target\clojure-code-linux.jar" -Force

Write-Host "Build complete!"
