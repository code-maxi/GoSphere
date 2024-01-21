pushd ~/Programmieren/GoSphere/
javac -d output/ client/*.java data/*.java math/*.java server/*.java network/*.java
cd output/
java client.GoClient
popd