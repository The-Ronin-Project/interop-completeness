docker exec adb-free /bin/bash -c 'while [ ! -f /u01/ords/self-signed.crt ]; do sleep 2; done;'
docker exec adb-free /bin/bash -c 'yes | keytool -import -trustcacerts -alias adbmongocert -file /u01/ords/self-signed.crt -keystore /u01/app/oracle/wallets/tls_wallet/truststore.jks -storepass Longpassword1'
docker exec adb-free /bin/bash -c 'chmod 755 /u01/app/oracle/wallets/tls_wallet/*'
docker cp adb-free:u01/app/oracle/wallets/tls_wallet /tmp/adb-free
