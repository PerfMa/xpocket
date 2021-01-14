#/bin/sh
appName="XPocket"
rm -rf ${appName}-*-ear.tar.gz
rm -rf ${appName}-*-ear.zip
mvn clean:clean
mvn install -Dmaven.test.skip
cp xpocket-deploy/target/${appName}-*-ear.*  .