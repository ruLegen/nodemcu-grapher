$scriptpath = $MyInvocation.MyCommand.Path
$dir = Split-Path $scriptpath
Write-host "My directory is $dir"
# temporarily change to the correct folder
Push-Location $dir
cd out
# do stuff, call ant, etc
..\luac.cross_3.0.0-release_20240225_x64_float_Lua51.exe -f ../../*.lua 

# now back to previous directory
Pop-Location
