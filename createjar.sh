pushd ~/Programmieren/GoSphere/output/
javac -d . ../client/*.java ../data/*.java ../math/*.java ../server/*.java ../network/*.java
jar cfm GoSphere.jar ../MANIFEST.txt ./client/*.class ./data/*.class ./math/*.class ./server/*.class ./network/*.class
sshpass -pwetter56 rsync GoSphere.jar pi@raspberrypi:/home/pi/gosphere/
sshpass -pwetter56 ssh pi@raspberrypi '/home/pi/gosphere/start.sh'
popd