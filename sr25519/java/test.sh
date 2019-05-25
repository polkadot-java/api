javac -cp org/polkadot/sr25519/ -d out org/polkadot/sr25519/SR25519.java org/polkadot/sr25519/TestMain.java
java -Djava.library.path=out -cp out org.polkadot.sr25519.TestMain
