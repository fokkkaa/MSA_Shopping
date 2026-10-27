param([string]$BaseUrl = 'http://127.0.0.1:8082', [switch]$VerifyPersistence)
$ErrorActionPreference = 'Stop'

function Request-Api($Method, $Path, $Body = $null, $Expected = 200) {
    $options = @{ Uri = "$BaseUrl$Path"; Method = $Method; UseBasicParsing = $true; TimeoutSec = 15 }
    if ($null -ne $Body) {
        $options.ContentType = 'application/json; charset=utf-8'
        $options.Body = [Text.Encoding]::UTF8.GetBytes(($Body | ConvertTo-Json -Compress))
    }
    try {
        $response = Invoke-WebRequest @options
        $code = [int]$response.StatusCode
    } catch {
        if ($null -eq $_.Exception.Response) { throw }
        $code = [int]$_.Exception.Response.StatusCode
        if ($code -ne $Expected) { throw }
        return
    }
    if ($code -ne $Expected) { throw "$Method $Path returned $code, expected $Expected" }
    if ($response.Content) { return ($response.Content | ConvertFrom-Json) }
}

$user = $null
$list = $null
try {
    $tag = [Guid]::NewGuid().ToString('N')
    $user = Request-Api POST /api/users @{firstName='Angelina'; lastName='Zanko'; email="lab3-$tag@example.com"; password='testpass123'} 201
    $list = Request-Api POST /api/lists @{authorId=$user.id; slug="lab3-$tag"; name='Weekly groceries'; description='Docker API check'} 201
    $product = Request-Api POST /api/products @{listId=$list.id; authorId=$user.id; name='Молоко'; quantity=2; unit='л'} 201
    $read = Request-Api GET "/api/products/$($product.id)"
    if ($read.name -ne 'Молоко' -or $read.quantity -ne 2) { throw 'Product fields were not saved correctly' }
    $updated = Request-Api PUT "/api/products/$($product.id)" @{quantity=3; purchased=$true}
    if ($updated.quantity -ne 3 -or !$updated.purchased -or $updated.name -ne 'Молоко') { throw 'Partial update failed' }
    if ($VerifyPersistence) {
        Push-Location (Split-Path $PSScriptRoot -Parent)
        try {
            docker compose down
            if ($LASTEXITCODE -ne 0) { throw 'Could not stop containers' }
            docker compose up -d --wait
            if ($LASTEXITCODE -ne 0) { throw 'Could not recreate containers' }
            $ready = $false
            for ($attempt = 0; $attempt -lt 30; $attempt++) {
                try {
                    $null = Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl/api/users" -TimeoutSec 2
                    $ready = $true
                    break
                } catch { Start-Sleep -Seconds 1 }
            }
            if (!$ready) { throw 'Application did not become ready' }
            $stored = Request-Api GET "/api/products/$($product.id)"
            if ($stored.name -ne 'Молоко' -or $stored.quantity -ne 3 -or !$stored.purchased) {
                throw 'Data was not preserved after container recreation'
            }
            Write-Output 'PASS: MySQL data survived removal and recreation of both containers'
        } finally { Pop-Location }
    }
    $items = @(Request-Api GET "/api/lists/$($list.id)/products")
    if ($items.Count -ne 1 -or $items[0].id -ne $product.id) { throw 'List lookup failed' }
    $null = Request-Api PUT "/api/products/$($product.id)" @{quantity=0} 400
    $null = Request-Api POST /api/users @{firstName='Duplicate'; lastName='User'; email=$user.email; password='testpass123'} 409
    $null = Request-Api DELETE "/api/users/$($user.id)" $null 409
    $null = Request-Api DELETE "/api/lists/$($list.id)" $null 204
    $list = $null
    $null = Request-Api GET "/api/products/$($product.id)" $null 404
    $null = Request-Api DELETE "/api/users/$($user.id)" $null 204
    $user = $null
    Write-Output 'PASS: CRUD, Cyrillic text, partial update, list lookup, validation, conflicts and cascading delete'
} finally {
    if ($null -ne $list) { $null = Request-Api DELETE "/api/lists/$($list.id)" $null 204 }
    if ($null -ne $user) { $null = Request-Api DELETE "/api/users/$($user.id)" $null 204 }
}
