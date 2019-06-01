#/usr/bin/bash

# if the .sh doesn't work, such as jni.h not found, try to run the command in the terminal
g++ -shared -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin jnimain.cpp -L . -lsr25519crust -o ../java/out/libjni.dylib


