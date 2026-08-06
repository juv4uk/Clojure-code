Add-Type -AssemblyName System.Drawing
$image = [System.Drawing.Bitmap]::FromFile("c:\Users\juv4u\Documents\GitHub\Clojure-code\graphics\icon.png")
$iconHandle = $image.GetHicon()
$icon = [System.Drawing.Icon]::FromHandle($iconHandle)
$stream = [System.IO.FileStream]::new("c:\Users\juv4u\Documents\GitHub\Clojure-code\graphics\icon.ico", [System.IO.FileMode]::Create)
$icon.Save($stream)
$stream.Close()
$icon.Dispose()
$image.Dispose()
Write-Host "Success"
