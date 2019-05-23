# Polkadot/Substrate Java Api

This library provides a Java wrapper around all the methods exposed by a Polkadot/Subtrate network client and defines all the types exposed by a node.

- [packages](https://github.com/polkadot-java/api/tree/master/packages) -- Polkadot/substrate api Java implementation.  
- [examples](https://github.com/polkadot-java/api/tree/master/examples) -- Demo projects (Gradle).  
- [examples_runnable](https://github.com/polkadot-java/api/tree/master/examples_runnable) -- Demo executable JARs.  

## JDK

1.8

## overview

The API is split up into a number of internal packages -

- [@polkadot/api](packages/src/main/java/org/polkadot/api/) The API library, providing both Promise and RxJS Observable-based interfaces. This is the main user-facing entry point.
- [@polkadot/rpc](packages/src/main/java/org/polkadot/rpc/) RPC library.
- [@polkadot/type](packages/src/main/java/org/polkadot/type/) Basic types such as extrinsics and storage.
- [@polkadot/types](packages/src/main/java/org/polkadot/types/) Codecs for all Polkadot primitives.

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

2. Running the Shell script:  

[01_simple_connect.sh](https://github.com/polkadot-java/api/blob/master/examples_runnable/20190518/01_simple_connect.sh)   
[02_listen_to_blocks_and_unsubscribe.sh](https://github.com/polkadot-java/api/blob/master/examples_runnable/20190518/02_listen_to_blocks_and_unsubscribe.sh)  
[03_listen_to_balance_change.sh](https://github.com/polkadot-java/api/blob/master/examples_runnable/20190518/03_listen_to_balance_change.sh)   
[04_unsubscribe.sh](https://github.com/polkadot-java/api/blob/master/examples_runnable/20190518/04_unsubscribe.sh)   
[05_read_storage.sh](https://github.com/polkadot-java/api/blob/master/examples_runnable/20190518/05_read_storage.sh)   

3. To change the Substrate address, change the `endPoint` variable in each demo main file.

## Current progress

A.JSON-RPC, specifically including
Authoring of network items -- 100%  
Retrieval of chain data -- 100%  
Query of state -- 100%  
Methods to retrieve system info -- 100%  
 
B.timestamp function corresponding to Storage, Extrinsics. -- 100%  

C.type, including Primitive types, Substrate types, Codec types, RPC types,
Derived types  -- 100%  

There are some other high level interface to be finished.
