To use the JNI, copy the libjni.so in proper platform to the folder where
Java `System.loadLibrary("jni")` can find the shared library.

Note to pass the library folder to Java, you can use,
`java -Djava.library.path=FOLDER  blahblah`
See test.sh in ../java folder as a reference.
