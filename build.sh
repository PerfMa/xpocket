#/bin/sh
appName="XPocket"
rm -rf ${appName}-*-ear.tar.gz
mvn clean:clean
mvn install -Dmaven.test.skip
cp xpocket-deploy/target/${appName}-*-ear.tar.gz  .