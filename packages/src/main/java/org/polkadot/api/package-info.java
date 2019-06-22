/**
# @polkadot/rpc-core

This library provides a clean wrapper around all the methods exposed by a Polkadot network client.

## Usage

Installation -

```
yarn add @polkadot/rpc-core
```

Initialisation -

```java
const provider = new WsProvider("http://127.0.0.1:9944");
const api = new Rpc(provider);
```

Retrieving the block header object for a given block header hash (a 0x-prefixed hex string with length of 64) -

```java
api.chain
  .getHeader("0x1234567890")
  .then((header) => System.out.println(header))
  .catch((error) => System.out.println(error));
```

Retrieving the best block number, parent hash, state root hash, extrinsics root hash, and digest (once-off) -

```java
api.chain
  .getHead()
  .then((headerHash) => {
    return api.chain.getHeader(headerHash);
  })
  .then((header) => {
    System.out.print("best ");
    System.out.println(header.blockNumber);
    System.out.print("parentHash: ");
    System.out.println(header.parentHash.toString());
    System.out.print("stateRoot: ");
    System.out.println(header.stateRoot.toString());
    System.out.print("extrinsicsRoot: ");
    System.out.println(header.extrinsicsRoot.toString());
    System.out.print("digest: ");
    System.out.println(header.digest.toString());
  })
  .catch((error) => {
    System.out.println(error);
  });
```

Retrieving best header via subscription -

```java
api.chain
  .subscribeNewHead((header) => {
    System.out.print("best ");
    System.out.println(header.blockNumber);
  })
  .then((subscriptionId) => {
    System.out.print("subscriptionId: ");
    System.out.println(subscriptionId);
    // id for the subscription, can unsubscribe via
    // api.chain.subscribeNewHead.unsubscribe(subscriptionId);
  })
  .catch((error) => {
    System.out.println(error);
  });
```

*/
package org.polkadot.api;
