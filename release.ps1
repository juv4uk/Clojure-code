param (
    [Parameter(Mandatory=$true, Position=0)]
    [string]$Version
)

$ErrorActionPreference = "Stop"

# Автоматично прибираємо літеру 'v', якщо користувач її випадково додав (наприклад, -Version "v1.1.3")
if ($Version.StartsWith("v") -or $Version.StartsWith("V")) {
    $Version = $Version.Substring(1)
}

Write-Host "Starting release process for version $Version..."

$projectFile = "project.clj"
if (-not (Test-Path $projectFile)) {
    Write-Error "Could not find $projectFile in the current directory."
    exit 1
}

Write-Host "1. Updating project.clj to version $Version..."
$content = Get-Content $projectFile -Raw
# Replace the version in (defproject clojure-code "x.y.z"
$newContent = $content -replace '\(defproject\s+\S+\s+"[^"]+"', "(defproject clojure-code `"$Version`""

if ($content -eq $newContent) {
    Write-Host "Version in project.clj is already $Version or could not be matched. Continuing..."
} else {
    Set-Content -Path $projectFile -Value $newContent -NoNewline
    Write-Host "Successfully updated project.clj."
}

$coreFile = "src\clj\nightcode\core.clj"
if (Test-Path $coreFile) {
    Write-Host "Updating window title in core.clj..."
    $coreContent = Get-Content $coreFile -Raw
    $newCoreContent = $coreContent -replace '\(\.setTitle "Clojure-code [^"]+"', "(.setTitle `"Clojure-code $Version`""
    Set-Content -Path $coreFile -Value $newCoreContent -NoNewline
}

Write-Host "2. Committing changes..."
git add $projectFile
git add $coreFile
# Check if there are changes to commit
$gitStatus = git status --porcelain
if ($gitStatus -match "project.clj") {
    git commit -m "Bump version to $Version"
} else {
    Write-Host "No changes to commit for project.clj."
}

Write-Host "3. Creating git tag v$Version..."
# Check if tag already exists
$tagExists = git tag -l "v$Version"
if ($tagExists) {
    Write-Host "Tag v$Version already exists! Skipping tag creation."
} else {
    git tag -a "v$Version" -m "Release $Version"
    Write-Host "Tag v$Version created."
}

Write-Host "4. Pushing commits and tags to origin..."
git push origin master
git push origin "v$Version"

Write-Host ""
Write-Host "==========================================================="
Write-Host "Release v$Version pushed to GitHub!"
Write-Host "Якщо GitHub Action не запускається автоматично,"
Write-Host "перейдіть на вкладку Actions і запустіть воркфлоу Release вручну."
Write-Host "==========================================================="
