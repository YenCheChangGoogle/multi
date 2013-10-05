@echo off

SET CURRENT_DIR=%CD%
SET TOMCAT_ROOT=C:\Programming\ThirdParty\apache-tomcat-7.0.42
SET PROJECT_DIR=C:\Programming\java_projects\multi

if "%1" == "StopTomcat" goto StopTomcat
if "%1" == "StartTomcat" goto StartTomcat
if "%1" == "Deploy" goto Deploy
goto End

:StopTomcat
cd "%TOMCAT_ROOT%\bin"
call catalina.bat stop
goto End

:Deploy
call del "%TOMCAT_ROOT%\webapps\multi-1.0-SNAPSHOT.war"
call rmdir /s /q "%TOMCAT_ROOT%\webapps\multi-1.0-SNAPSHOT"
call copy "%PROJECT_DIR%\target\multi-1.0-SNAPSHOT.war" "%TOMCAT_ROOT%\webapps"
goto End

:StartTomcat
set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=9000
set CATALINA_OPTS=%CATALINA_OPTS% ^
-Xmx512M ^
-Xms128M ^
-XX:MaxPermSize=128M
set JAVA_OPTS=%CATALINA_OPT%
cd "%TOMCAT_ROOT%\bin"
call catalina.bat jpda start
goto End

:End
cd "%CURRENT_DIR%"