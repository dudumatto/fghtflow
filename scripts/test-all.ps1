$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot

Push-Location (Join-Path $Root "backend")
try {
  mvn test
} finally {
  Pop-Location
}

Push-Location (Join-Path $Root "frontend")
try {
  npm install
  npm run build

  $Package = Get-Content -Raw package.json | ConvertFrom-Json
  if ($Package.scripts.PSObject.Properties.Name -contains "test:e2e") {
    npm run test:e2e
  }
} finally {
  Pop-Location
}
