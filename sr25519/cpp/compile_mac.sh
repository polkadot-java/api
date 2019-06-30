#/usr/bin/bash

# if the .sh doesn't work, such as jni.h not found, try to run the command in the terminal
g++ -shared -framework Security -framework Foundation -v -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin jnimain.cpp -L . -lsr25519crust -lstdc++  -o ../java/out/libjni.dylib


