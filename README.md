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
 
B.balances, session, demoracacy, staking, consensus and other functions
corresponding to Storage Extrinsics Events -- 100%  

C.timestamp function corresponding to Storage, Extrinsics. -- 100%  

D.type, including Primitive types, Substrate types, Codec types, RPC types,
Derived types  -- 100%  

There are some other high level interface to be finished.
