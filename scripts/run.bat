@echo off

echo Stopping Tomcat
call tomcat.bat StopTomcat

echo Deploy App
call tomcat.bat Deploy

echo Starting Tomcat
call tomcat.bat StartTomcat

pause