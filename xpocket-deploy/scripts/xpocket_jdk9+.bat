@echo off
set BASEDIR=%~dp0

set tools_jar="%BASEDIR%/tools/windows/tools.jar"
set LAUNCHER="com.perfma.xlab.xpocket.launcher.XPocketLauncher"

REM set DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8002"
java -cp .;%BASEDIR%\lib\* -DPLUGIN_PATH=%BASEDIR%\plugins %DEBUG_OPTS% -Dconfig_dir=%BASEDIR%/config/ -Djava.library.path="%BASEDIR%/tools/windows/" %LAUNCHER%
