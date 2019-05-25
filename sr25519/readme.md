To build sr25519 C++ wrapper, get it from https://github.com/Warchant/sr25519-crust,
then
cmake .. -DCMAKE_BUILD_TYPE=Release -DTESTING=OFF -DBUILD_SHARED_LIBS=FALSE
then
make
then copy libsr25519crust.a to the folder sr25519/cpp (same as jnimain.cpp)

