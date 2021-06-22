#!/bin/sh
cd `pwd`

XPOCKET_HOME=`dirname $0`
cd $XPOCKET_HOME
XPOCKET_HOME=`pwd`

OS="linux"
OS_LINUX=`echo $OSTYPE|cut -c1-5`
OS_MACOS=`echo $OSTYPE|cut -c1-6`

if [ "$OS_LINUX" = "linux" ]; then
        OS="linux"
elif [ "$OS_MACOS" = "darwin" ]; then
        OS="macosx"
elif [ -z $OSTYPE ]; then
        echo "OSTYPE is not seted!Use default ostype: linux"
        OS="linux"
fi

#DEBUG_OPT="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8002"
LAUNCHER="com.perfma.xlab.xpocket.launcher.XPocketLauncher"

java -cp .:${XPOCKET_HOME}/lib/* ${DEBUG_OPT} -DXPOCKET_HOME=${XPOCKET_HOME}/ -DXPOCKET_PLUGIN_PATH=${XPOCKET_HOME}/plugins -DXPOCKET_CONFIG_PATH=${XPOCKET_HOME}/config/ -Djava.library.path=${XPOCKET_HOME}/tools/${OS}/ ${LAUNCHER} "$@"