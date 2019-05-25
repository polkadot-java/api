package org.polkadot.utils.crypto;

import java.util.Random;

public class NaclTest
{
	public static void main(String[] args) {
		new NaclTest().test();
	}

	private void test()
	{
		testNaclEncrypt();
		testNaclDecrypt();
		testSign();
		testSignAndVerifyValid();
		testSignAndVerifyInvalid();
	}

	private void testNaclEncrypt()
	{
		byte[] secret = new byte[32];
		int[] messageInts = { 1, 2, 3, 4, 5, 4, 3, 2, 1 };
		byte[] message = intsToBytes(messageInts);
		byte[] encrypted = Nacl.naclEncrypt(message, secret, new byte[24]).encrypted;
		int[] expectedInts = { 94, 21, 20, 69, 68, 221, 140, 245, 200, 67, 77, 188, 129, 85, 227, 141, 199, 60, 184, 251, 251, 129, 205, 46, 234 };
		byte[] expected = intsToBytes(expectedInts);
		System.out.println(
				"testNaclEncrypt:\n"
						+ "actual=   " + bytesToHex(encrypted) + "\n"
						+ "expected= " + bytesToHex(expected) + "\n"
		);
	}

	private void testNaclDecrypt()
	{
		byte[] secret = new byte[32];
		int[] messageInts = { 1, 2, 3, 4, 5, 4, 3, 2, 1 };
		byte[] message = intsToBytes(messageInts);
		byte[] encrypted = Nacl.naclEncrypt(message, secret, new byte[24]).encrypted;
		byte[] decrypted = Nacl.naclDecrypt(encrypted, new byte[24], secret);
		System.out.println(
				"testNaclDecrypt:\n"
						+ "message=   " + bytesToHex(message) + "\n"
						+ "decrypted= " + bytesToHex(decrypted) + "\n"
		);
	}

	private void testSign()
	{
		byte[] message = { 0x61, 0x62, 0x63, 0x64 };
		Types.Keypair keyPair = Nacl.naclKeypairFromSeed("12345678901234567890123456789012".getBytes());
		byte[] sig = Nacl.naclSign(message, keyPair);
		int[] expectedInts = { 28, 58, 206, 239, 249, 70, 59, 191, 166, 40, 219, 218, 235, 170, 25, 79, 10, 94, 9, 197, 34, 126, 1, 150, 246, 68, 28, 238, 36, 26, 172, 163, 168, 90, 202, 211, 126, 246, 57, 212, 43, 24, 88, 197, 240, 113, 118, 76, 37, 81, 91, 110, 236, 50, 144, 134, 100, 223, 220, 238, 34, 185, 211, 7 };
		byte[] expected = intsToBytes(expectedInts);
		System.out.println(
				"testSignAndVerifyValid:\n"
						+ "actual_sig=   " + bytesToHex(sig) + "\n"
						+ "expected_sig= " + bytesToHex(expected) + "\n"
		);
	}

	private void testSignAndVerifyValid()
	{
		byte[] message = { 0x61, 0x62, 0x63, 0x64 };
		Types.Keypair keyPair = Nacl.naclKeypairFromSeed("12345678901234567890123456789012".getBytes());
		byte[] sig = Nacl.naclSign(message, keyPair);
		boolean valid = Nacl.naclVerify(message, sig, keyPair.publicKey);
		if(valid) {
			System.out.println("testSignAndVerifyValid OK\n");
		}
		else {
			System.out.println("testSignAndVerifyValid FAIL\n");
		}
	}

	private void testSignAndVerifyInvalid()
	{
		byte[] message = { 0x61, 0x62, 0x63, 0x64 };
		Types.Keypair keyPair = Nacl.naclKeypairFromSeed("12345678901234567890123456789012".getBytes());
		byte[] sig = Nacl.naclSign(message, keyPair);
		++sig[0];
		boolean valid = Nacl.naclVerify(message, sig, keyPair.publicKey);
		if(! valid) {
			System.out.println("testSignAndVerifyInvalid OK\n");
		}
		else {
			System.out.println("testSignAndVerifyInvalid FAIL\n");
		}
	}

	private final char[] hexArray = "0123456789abcdef".toCharArray();
	public String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public byte[] hexToBytes(String hex) {
		int l = hex.length();
		byte[] data = new byte[l/2];
		for (int i = 0; i < l; i += 2) {
			data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
					+ Character.digit(hex.charAt(i+1), 16));
		}
		return data;
	}

	public byte[] randomKeypair() {
		byte[] seed = new byte[SR25519.SR25519_SEED_SIZE];
		new Random().nextBytes(seed);
		byte[] kp = new byte[SR25519.SR25519_KEYPAIR_SIZE];
		SR25519 sr = new SR25519();
		sr.sr25519_keypair_from_seed(kp, seed);
		return kp;
	}

	public byte[] intsToBytes(int[] data) {
		byte[] result = new byte[data.length];
		for(int i = 0; i < data.length; ++i) {
			result[i] = (byte)data[i];
		}
		return result;
	}

}
