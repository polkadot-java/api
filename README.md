# Polkadot/Substrate Java Api

This library provides a Java wrapper around all the methods exposed by a Polkadot/Subtrate network client and defines all the types exposed by a node.

- [packages](https://github.com/polkadot-java/api/tree/master/packages) -- Polkadot/substrate api Java implementation.  
- [examples](https://github.com/polkadot-java/api/tree/master/examples) -- Demo projects (Gradle).  
- [examples_runnable](https://github.com/polkadot-java/api/tree/master/examples_runnable) -- Demo executable JARs.  

## Build

Integration test require local polkadot node running (run with `polkadot --dev`),
then  
`./gradlew build`

Alternatively build without running unit tests, but still compiling them  
`./gradlew build testClasses -x test`

## JDK

Java 1.8

## Based JS code version

The Java version is based on JS commit [ff25a85ac20687de241a84e0f3ebab4c2920df7e](https://github.com/polkadot-js/api/commit/ff25a85ac20687de241a84e0f3ebab4c2920df7e).

## Substrate version

The working substrate version is 1.0.0-41ccb19c-x86_64-macos.
Newer substrate may be not supported.

## overview

The API is split up into a number of internal packages

- [@polkadot/api](packages/src/main/java/org/polkadot/api/) The API library, providing both Promise and RxJS Observable-based interfaces. This is the main user-facing entry point.
- [@polkadot/rpc](packages/src/main/java/org/polkadot/rpc/) RPC library.
- [@polkadot/type](packages/src/main/java/org/polkadot/type/) Basic types such as extrinsics and storage.
- [@polkadot/types](packages/src/main/java/org/polkadot/types/) Codecs for all Polkadot primitives.

## Document

* See the generated JavaDoc in /doc folder. Or visit the [document site](https://polkadot-java.github.io/)
* To generate JavaDoc by yourself, reference `gendoc.sh` in the root folder  
* To understand how the system works, you may reference [Substrate](https://github.com/paritytech/substrate) and [Polkadot Network](https://polkadot.network/)

## Integrate the API into your projects

The project uses [Gradle](https://gradle.org/) as build tool. You need to install Gradle.

### Build the library with Gradle then link to the JAR

1. `git clone https://github.com/polkadot-java/api.git`
3. `gradle build`
4. Check the JARs in folder `api/build/libs/`
5. Add the JARs into your projects (TODO publish on MavenCentral).

### Link to the source code directly,

1. `git clone https://github.com/polkadot-java/api.git`
2. Import the gradle project in folder `api` to your workspace.
3. Add links or dependencies in the IDE. This differs, please reference to your IDE help (IDEA, Eclipse).

## How to build and use sr25591 JNI

1. See polkadot-java/sr25519/readme.md to compile sr25591 library (Rust and C++).  
2. See polkadot-java/sr25519/cpp/compile.sh how to compile the JNI shared library.  
3. See polkadot-java/sr25519/libs/readme.md how to use the JNI in the Java API.  

## How to run examples

1. Install substrate local node:  
`https://github.com/paritytech/substrate`  

2. Running the samples:  
There are several runnable samples. To run the samples, go to folder `examples_runnable/LastestDate` (such as examples_runnable/20190525), then run each shell script.

3. To change the Substrate address, change the `endPoint` variable in each demo main file.

