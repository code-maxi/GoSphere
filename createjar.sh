pushd ~/Programmieren/GoSphere/output/
if [ "$1" != "no" ]; then
  echo "compiling..."
  javac -d . ../client/*.java ../data/*.java ../math/*.java ../server/*.java ../network/*.java
  jar cfm GoSphere.jar ../MANIFEST.txt ./client/*.class ./data/*.class ./math/*.class ./server/*.class ./network/*.class
fi
sshpass -p+maXX.b.17+@kr rsync GoSphere.jar maxipi@raspberrypi:/home/maxipi/gosphere/
sshpass -p+maXX.b.17+@kr ssh maxipi@raspberrypi '/home/maxipi/gosphere/start.sh'
popd
