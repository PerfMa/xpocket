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
        OS="linux"
fi

JAVA_VERSION=`java -fullversion 2>&1 |awk -F"[ +\".]" '{print $5$6 }'`
#DEBUG_OPT="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8002"


JAVA_VER=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
LAUNCHER="com.perfma.xlab.xpocket.launcher.XPocketLauncher"


if [ ${JAVA_VER} -le 8 ]; then
  tools_jar=`ls ${XPOCKET_HOME}/tools/${OS}/tools.jar`

  if [ -f ${JAVA_HOME}/lib/sa-jdi.jar ];then
     sa_jdi_jar=`ls ${JAVA_HOME}/lib/sa-jdi.jar`
     cp $sa_jdi_jar ${XPOCKET_HOME}/lib/  
  fi
  java -cp .:${tools_jar}:${XPOCKET_HOME}/lib/* ${DEBUG_OPT} -DXPOCKET_HOME=${XPOCKET_HOME}/ -DXPOCKET_PLUGIN_PATH=${XPOCKET_HOME}/plugins -DXPOCKET_CONFIG_PATH=${XPOCKET_HOME}/config/ -Djava.library.path=${XPOCKET_HOME}/tools/${OS}/ ${LAUNCHER} "$@"
else
  java -cp .:${XPOCKET_HOME}/lib/* ${DEBUG_OPT} -DXPOCKET_HOME=${XPOCKET_HOME}/ -DXPOCKET_PLUGIN_PATH=${XPOCKET_HOME}/plugins -DXPOCKET_CONFIG_PATH=${XPOCKET_HOME}/config/ -Djava.library.path=${XPOCKET_HOME}/tools/${OS}/ ${LAUNCHER} "$@"
fi





