$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
# Only a generated isolated database is created/dropped by this check.
$checkDatabase = 'shopping_check_' + [Guid]::NewGuid().ToString('N')
function Invoke-RootSql([string]$Sql) {
    $command = 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql -N -uroot <<''CHECK_SQL''' + "`n" + $Sql + "`nCHECK_SQL`n"
    $result = $command | docker compose exec -T mysql sh -c 'tr -d ''\r'' | sh'
    if ($LASTEXITCODE -ne 0) { throw 'MySQL verification query failed' }
    return $result
}
try {
    $null = Invoke-RootSql "CREATE DATABASE $checkDatabase; GRANT ALL ON $checkDatabase.* TO 'shopping'@'%';"
    $url = "jdbc:mysql://mysql:3306/${checkDatabase}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    for ($run = 1; $run -le 2; $run++) {
        docker compose run --rm --no-deps -e "DB_URL=$url" -e FLYWAY_BASELINE_ON_MIGRATE=false app --spring.main.web-application-type=none
        if ($LASTEXITCODE -ne 0) { throw "Migration startup $run failed" }
        $rows = @(Invoke-RootSql "SELECT CONCAT(version,':',type,':',success) FROM $checkDatabase.flyway_schema_history ORDER BY installed_rank; SELECT COUNT(*) FROM $checkDatabase.products;")
        if (($rows -join ',') -ne '1:SQL:1,1.1:SQL:1,3') { throw "Unexpected migration state: $rows" }
        Write-Output "PASS: fresh MySQL startup $run, versions 1 and 1.1, three products"
    }
} finally {
    if ($checkDatabase -match '^shopping_check_[a-f0-9]{32}$') {
        $null = Invoke-RootSql "DROP DATABASE IF EXISTS $checkDatabase;"
    }
    Pop-Location
}
