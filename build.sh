pushd ~/Coding/GoSphere/output/
if [ "$1" != "nc" ]; then
  echo "compiling..."
  javac --release 11 -d . ../client/*.java ../data/*.java ../math/*.java ../server/*.java ../network/*.java
  echo $((`cat VERSION.log`+1)) > VERSION.log # version + 1
  jar cfm Go3D.jar ./MANIFEST.txt ./VERSION.log ./res/icon.png ./client/*.class ./data/*.class ./math/*.class ./server/*.class ./network/*.class
fi
if [ "$1" != "ns" ]; then
  sshpass -pwetter56 rsync Go3D.jar maximilian@server:/home/maximilian/go3d
  sshpass -pwetter56 ssh maximilian@server '/home/maximilian/go3d/start.sh'
fi
popd