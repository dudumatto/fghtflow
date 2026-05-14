param(
  [ValidateSet("db", "backend", "frontend")]
  [string]$Service = "frontend"
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot

switch ($Service) {
  "db" {
    Push-Location $Root
    try {
      docker compose up -d
    } finally {
      Pop-Location
    }
  }
  "backend" {
    Push-Location (Join-Path $Root "backend")
    try {
      mvn spring-boot:run
    } finally {
      Pop-Location
    }
  }
  "frontend" {
    Push-Location (Join-Path $Root "frontend")
    try {
      npm run dev
    } finally {
      Pop-Location
    }
  }
}
