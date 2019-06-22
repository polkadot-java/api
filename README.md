# Polkadot/Substrate Java Api

This library provides a Java wrapper around all the methods exposed by a Polkadot/Subtrate network client and defines all the types exposed by a node.

- [packages](https://github.com/polkadot-java/api/tree/master/packages) -- Polkadot/substrate api Java implementation.  
- [examples](https://github.com/polkadot-java/api/tree/master/examples) -- Demo projects (Gradle).  
- [examples_runnable](https://github.com/polkadot-java/api/tree/master/examples_runnable) -- Demo executable JARs.  

## JDK

1.8

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

See the generated JavaDoc in /doc folder.

To generate JavaDoc, reference `gendoc.sh` in the root folder.

## Integrate the API into your projects

Currently the library is under active development, the best way to use the library is to link to the source code directly.  

To link to the source code directly,

1. `git clone https://github.com/polkadot-java/api.git`
2. import api gradle project to your workspace.

Or you can build the library with Gradle then link to the JAR.

1. `git clone https://github.com/polkadot-java/api.git`
2. `cd root-folder-of-polkadot-java`
3. `gradle build`
4. Get the JARs in folder `build/libs/`
5. Add the JARs into your projects.

## How to run examples

1. Install substrate local node:  

`https://github.com/paritytech/substrate`  

2. Running the samples:  

There are several runnable samples. To run the samples, go to folder `examples_runnable/LastestDate` (such as examples_runnable/20190525), then run each shell script.

Here lists all the samples, there purpose is self explained in the shell script file name.

Promise_01_simple_connect.sh (added in version 20190511)   
Promise_02_listen_to_blocks_and_unsubscribe.sh (added in version 20190511)  
Promise_03_listen_to_balance_change.sh (added in version 20190518)   
Promise_04_unsubscribe.sh (added in version 20190518)   
Promise_05_read_storage.sh (added in version 20190518)   
Promise_06_make_transfer.sh (added in version 20190525)
Promise_07 (it was not in JS version)  
Promise_08_system_events.sh (added in version 20190601)
Promise_09_transfer_events.sh (added in version 20190601)
Promise_10_upgrade_chain.sh (added in version 20190601)
Rx_01_simple_connect.sh (added in version 20190616)
Rx_02_listen_to_blocks.sh (added in version 20190616)
Rx_03_listen_to_balance_change.sh (added in version 20190616)
Rx_04_unsubscribe.sh (added in version 20190616)
Rx_05_read_storage.sh (added in version 20190616)
Rx_06_make_transfer.sh (added in version 20190616)
Rx_08_system_events.sh (added in version 20190616)
Rx_09_transfer_events.sh (added in version 20190616)
Rx_10_upgrade_chain.sh (added in version 20190616)
101_democracy.sh (added in version 20190525)  
102_staking.sh (added in version 20190525)  

3. To change the Substrate address, change the `endPoint` variable in each demo main file.

## Progress of milestone 1

All progress are rough estimation. It's impossible to estimate the development progress accurately.

A.JSON-RPC, specifically including
Authoring of network items -- 100%  
Retrieval of chain data -- 100%  
Query of state -- 100%  
Methods to retrieve system info -- 100%  
 
B.balances, session, demoracacy, staking, consensus and other functions  
corresponding to Storage Extrinsics Events  
balances -- 100%  
session -- 100%  
democracy -- 100%  
staking -- 100%  
consensus -- 100%  
other functions corresponding to Storage Extrinsics Events -- 100%  

C.timestamp function corresponding to Storage, Extrinsics. -- 100%  

D.type, including Primitive types, Substrate types, Codec types, RPC types,
Derived types  -- 100%  

There are some other high level interface to be finished.

E. Integrated third party libraries

sr25591, Rush and C++ via JNI -- 100%  
ed25591, pure Java -- 100%  
tweetnacl, pure Java -- 100%  

## Progress of milestone 2

In general all JS API were converted to Java.  
All documents were converted and generated in JavaDoc.

## How to build and use sr25591 JNI

1. See polkadot-java/sr25519/readme.md to compile sr25591 library (Rush and C++).
2. See polkadot-java/sr25519/cpp/compile.sh how to compile the JNI shared library.
3. See polkadot-java/sr25519/libs/readme.md how to use the JNI in the Java API.
