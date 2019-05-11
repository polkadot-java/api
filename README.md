# Polkadot/Substrate Java Api
Java APIs around Polkadot and any Substrate-based chain RPC calls. It is dynamically generated based on what the Substrate runtime provides in terms of metadata.

- [packages](https://github.com/polkadot-java/api/tree/master/packages) -- Polkadot/substrate api Java implementation.  
- [examples](https://github.com/polkadot-java/api/tree/master/examples) -- Some api demo source(Gradle project).  
- [examples_runnable](https://github.com/polkadot-java/api/tree/master/examples_runnable) -- Some api demo jar packages, can be running.  

## How to running examples

1. Need install substrate local node:  

`https://github.com/paritytech/substrate`  

2. Running the Shell script:  

[01_simple_connect.sh](https://github.com/polkadot-java/api/blob/master/examples_runnable/01_simple_connect.sh)   
[02_listen_to_blocks_and_unsubscribe.sh](https://github.com/polkadot-java/api/blob/master/examples_runnable/02_listen_to_blocks_and_unsubscribe.sh)  

## Current progress

A.JSON-RPC, specifically including
Authoring of network items -- 100%
Retrieval of chain data -- 100%
Query of state -- 80%
Methods to retrieve system info -- 70%
 
B.balances, session, demoracacy, staking, consensus and other functions
corresponding to Storage Extrinsics Events -- 70%

C.timestamp function corresponding to Storage, Extrinsics. -- 70%

D.type, including Primitive types, Substrate types, Codec types, RPC types,
Derived types  -- 70%
