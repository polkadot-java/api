javac -cp org/polkadot/utils/crypto/ -d out org/polkadot/utils/crypto/SR25519.java org/polkadot/utils/crypto/TestMain.java
java -Djava.library.path=out -cp out org.polkadot.utils.crypto.TestMain
