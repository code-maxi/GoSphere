pushd ~/Programmieren/GoSphere/output/
if [ "$1" != "no" ]; then
  echo "compiling..."
  javac --release 11 -d . ../client/*.java ../data/*.java ../math/*.java ../server/*.java ../network/*.java
  echo $((`cat VERSION.log`+1)) > VERSION.log # version + 1
  jar cfm Go3D.jar ./MANIFEST.txt ./VERSION.log ./client/*.class ./data/*.class ./math/*.class ./server/*.class ./network/*.class
fi
echo "start raspberrypi"
sshpass -p+maXX.b.17+@kr rsync Go3D.jar maxipi@raspberrypi:/home/maxipi/go3d/
sshpass -p+maXX.b.17+@kr ssh maxipi@raspberrypi '/home/maxipi/go3d/start.sh' &

echo "start server"
sshpass -pwetter56 rsync Go3D.jar maximilian@server:/home/maximilian/go3d/
sshpass -pwetter56 ssh maximilian@server '/home/maximilian/go3d/start.sh' &
popd
