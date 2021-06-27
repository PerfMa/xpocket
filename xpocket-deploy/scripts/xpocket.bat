@echo off
set BASEDIR=%~dp0

PATH %PATH%;%JAVA_HOME%\bin\
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k%"

set LAUNCHER="com.perfma.xlab.xpocket.launcher.XPocketLauncher"
set tools_jar="%BASEDIR%/tools/windows/tools.jar"
REM set DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8002"

if %jver% LSS 19 (
java -cp .;%tools_jar%;%BASEDIR%lib\* -DXPOCKET_PLUGIN_PATH=%BASEDIR%plugins %DEBUG_OPTS% -DXPOCKET_HOME=%BASEDIR% -DXPOCKET_CONFIG_PATH=%BASEDIR%config\ -Djava.library.path="%BASEDIR%/tools/windows/" %LAUNCHER% %*
) else (
java -cp .;%BASEDIR%\lib\* -DXPOCKET_PLUGIN_PATH=%BASEDIR%plugins %DEBUG_OPTS% -DXPOCKET_HOME=%BASEDIR% -DXPOCKET_CONFIG_PATH=%BASEDIR%config\ -Djava.library.path="%BASEDIR%/tools/windows/" %LAUNCHER% %*
)
