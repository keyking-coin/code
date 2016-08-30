rd /s /q dist
mkdir dist
rd /s /q jar
mkdir jar
call C:\ant\bin\ant.bat  -file build.xml
copy conf\disallow dist\disallow
del dist\jdbc.properties
del dist\Market.json
del dist\NewServersList.xml
del dist\redis.properties
del dist\RemoteServices.xml
pause