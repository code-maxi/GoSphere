pushd ~/Programmieren/GoSphere/
if [ "$1" = "com" ]; then
javac -d output/ client/*.java data/*.java math/*.java server/*.java network/*.java
fi
cd output/
java client.GoConsole localhost 5555
popd