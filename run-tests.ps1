param([int]$Runs = 10, [string]$Log = "test_runs.log", [string]$RunDir = "test-runs")

$env:JAVA_HOME = "$env:USERPROFILE\.jdks\openjdk-25.0.2"
$mvn = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3\bin\mvn.cmd"
New-Item -ItemType Directory $RunDir -Force | Out-Null
"=== $(Get-Date -Format s) | runs=$Runs ===" | Set-Content $Log -Encoding UTF8
$flaky = @{}

for ($i = 1; $i -le $Runs; $i++) {
    $sw = [Diagnostics.Stopwatch]::StartNew()
    & $mvn clean test *> "$RunDir\run$i.txt"
    $sw.Stop()

    $ok = 0; $skipped = 0; $fails = @()
    foreach ($f in Get-ChildItem target\surefire-reports -Filter 'TEST-*.xml' -ErrorAction SilentlyContinue) {
        $x = New-Object Xml.XmlDocument; $x.Load($f.FullName)
        foreach ($tc in $x.testsuite.testcase) {
            $err = $tc.SelectSingleNode('failure|error')
            if ($err) {
                $name = "$($tc.classname).$($tc.name)"
                $msg = $err.GetAttribute('message') -replace '\s+', ' '
                $fails += "    FAIL $name : $($msg.Substring(0, [Math]::Min(200, $msg.Length)))"
                $flaky[$name] = 1 + [int]$flaky[$name]
            } elseif ($tc.SelectSingleNode('skipped')) { $skipped++ } else { $ok++ }
        }
    }
    $line = "run {0,2} | {1:HH:mm:ss} | ok {2} | fail {3} | skipped {4} | {5:N0}s" -f `
        $i, (Get-Date), $ok, $fails.Count, $skipped, $sw.Elapsed.TotalSeconds
    @($line) + $fails | Add-Content $Log -Encoding UTF8
    @($line) + $fails | Write-Output
}

"=== flaky summary ===" | Add-Content $Log -Encoding UTF8
$flaky.GetEnumerator() | Sort-Object Value -Descending |
    ForEach-Object { "  $($_.Value)/$Runs  $($_.Key)" } | Add-Content $Log -Encoding UTF8
