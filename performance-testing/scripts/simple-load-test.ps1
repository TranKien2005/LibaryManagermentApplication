# Simple Load Test using PowerShell
# Test hiệu suất cơ bản bằng PowerShell built-in commands

param(
    [string]$BaseUrl = "http://localhost:8080/api",
    [int]$Requests = 100,
    [int]$Concurrent = 10
)

Write-Host "Starting load test..." -ForegroundColor Green
Write-Host "URL: $BaseUrl" -ForegroundColor Cyan
Write-Host "Total Requests: $Requests" -ForegroundColor Cyan
Write-Host "Concurrent: $Concurrent" -ForegroundColor Cyan
Write-Host ""

$endpoints = @(
    "/books",
    "/users",
    "/borrows"
)

$results = @()
$startTime = Get-Date

# Function to make HTTP request and measure time
function Test-Endpoint {
    param([string]$Url)
    
    $sw = [System.Diagnostics.Stopwatch]::StartNew()
    try {
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
        $sw.Stop()
        return @{
            Success = $true
            StatusCode = $response.StatusCode
            Duration = $sw.ElapsedMilliseconds
        }
    }
    catch {
        $sw.Stop()
        return @{
            Success = $false
            StatusCode = 0
            Duration = $sw.ElapsedMilliseconds
            Error = $_.Exception.Message
        }
    }
}

# Run tests
$jobs = @()
for ($i = 0; $i -lt $Requests; $i++) {
    $endpoint = $endpoints[$i % $endpoints.Count]
    $url = "$BaseUrl$endpoint"
    
    # Start job for concurrent execution
    $jobs += Start-Job -ScriptBlock ${function:Test-Endpoint} -ArgumentList $url
    
    # Limit concurrent jobs
    while ((Get-Job -State Running).Count -ge $Concurrent) {
        Start-Sleep -Milliseconds 100
    }
}

# Wait for all jobs to complete
Write-Host "Waiting for all requests to complete..." -ForegroundColor Yellow
$jobs | Wait-Job | Out-Null

# Collect results
$results = $jobs | Receive-Job
$jobs | Remove-Job

$endTime = Get-Date
$totalTime = ($endTime - $startTime).TotalSeconds

# Calculate statistics
$successCount = ($results | Where-Object { $_.Success }).Count
$failCount = $Requests - $successCount
$durations = $results | Where-Object { $_.Success } | Select-Object -ExpandProperty Duration
$avgDuration = ($durations | Measure-Object -Average).Average
$minDuration = ($durations | Measure-Object -Minimum).Minimum
$maxDuration = ($durations | Measure-Object -Maximum).Maximum

# Display results
Write-Host "`n========== RESULTS ==========" -ForegroundColor Green
Write-Host "Total Time: $([math]::Round($totalTime, 2))s"
Write-Host "Successful Requests: $successCount"
Write-Host "Failed Requests: $failCount"
Write-Host "Success Rate: $([math]::Round(($successCount / $Requests) * 100, 2))%"
Write-Host "Requests/Second: $([math]::Round($Requests / $totalTime, 2))"
Write-Host "`nResponse Times (ms):"
Write-Host "  Average: $([math]::Round($avgDuration, 2))"
Write-Host "  Min: $minDuration"
Write-Host "  Max: $maxDuration"
Write-Host "============================`n" -ForegroundColor Green
