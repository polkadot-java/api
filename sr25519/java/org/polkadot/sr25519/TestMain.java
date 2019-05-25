package org.polkadot.sr25519;

import java.io.*;
import java.util.*;

public class TestMain
{
	public static void main(String[] args)
	{
		new TestMain().test();
	}
	
	private void test()
	{
		System.out.println("This is a test");
		
		//testSimple();
		testDerivedHard();
		testDerivedSoft();
		testKeyFromSeed();
		testSignAndVerifyValid();
		testSignAndVerifyInvalid();
		testVerifyExisting();
	}
	
	private void testSimple()
	{
		SR25519 sr = new SR25519();
		byte[] a = { 1, 2, 3, 4, 5 };
		byte[] b = new byte[5];
		sr.test1(a, b);
		System.out.println("a=" + bytesToHex(a) + "  b=" + bytesToHex(b));
	}
	
	private void testDerivedHard()
	{
		SR25519 sr = new SR25519();

		byte[] known_kp =
			hexToBytes("28b0ae221c6bb06856b287f60d7ea0d98552ea5a16db16956849aa371db3eb51fd190cce74df356432b410bd64682309d6dedb27c76845daf388557cbac3ca3446ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a");

		byte[] cc =
		  hexToBytes("14416c6963650000000000000000000000000000000000000000000000000000");

		byte[] derived = new byte[SR25519.SR25519_KEYPAIR_SIZE];
		sr.sr25519_derive_keypair_hard(derived, known_kp, cc);

	  // pubkey = last 32 bytes
	  String actual_pubkey = bytesToHex(Arrays.copyOfRange(derived, 64, derived.length));
	  String expected_pubkey =
		  "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d";

	System.out.println(
		"testDerivedHard:\n"
		+ "actual_pubkey=   " + actual_pubkey + "\n"
		+ "expected_pubkey= " + expected_pubkey + "\n"
	);
	}

	private void testDerivedSoft()
	{
		SR25519 sr = new SR25519();

		byte[] known_kp =
			hexToBytes("28b0ae221c6bb06856b287f60d7ea0d98552ea5a16db16956849aa371db3eb51fd190cce74df356432b410bd64682309d6dedb27c76845daf388557cbac3ca3446ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a");

		byte[] cc =
		  hexToBytes("0c666f6f00000000000000000000000000000000000000000000000000000000");

		byte[] derived = new byte[SR25519.SR25519_KEYPAIR_SIZE];
		sr.sr25519_derive_keypair_soft(derived, known_kp, cc);

	  // pubkey = last 32 bytes
	  String actual_pubkey = bytesToHex(Arrays.copyOfRange(derived, 64, derived.length));
	  String expected_pubkey =
		  "40b9675df90efa6069ff623b0fdfcf706cd47ca7452a5056c7ad58194d23440a";

	System.out.println(
		"testDerivedSoft:\n"
		+ "actual_pubkey=   " + actual_pubkey + "\n"
		+ "expected_pubkey= " + expected_pubkey + "\n"
	);
	}

	private void testKeyFromSeed()
	{
		SR25519 sr = new SR25519();
		
		byte[] seed = hexToBytes("fac7959dbfe72f052e5a0c3c8d6530f202b02fd8f9f5ca3580ec8deb7797479e");
		String expected_keypair = "28b0ae221c6bb06856b287f60d7ea0d98552ea5a16db16956849aa371db3eb51fd190cce74df356432b410bd64682309d6dedb27c76845daf388557cbac3ca3446ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a";
		
		byte[] kp = new byte[SR25519.SR25519_KEYPAIR_SIZE];
		sr.sr25519_keypair_from_seed(kp, seed);
		String actual_keypair = bytesToHex(kp);
		System.out.println(
			"testKeyFromSeed:\n"
			+ "actual_keypair=   " + actual_keypair + "\n"
			+ "expected_keypair= " + expected_keypair + "\n"
		);
	}
	
	private void testSignAndVerifyValid()
	{
		SR25519 sr = new SR25519();
		
		byte[] kp = randomKeypair();
		byte[] msg = "hello world".getBytes();
		byte[] sig = new byte[SR25519.SR25519_SIGNATURE_SIZE];
		sr.sr25519_sign(sig, Arrays.copyOfRange(kp, SR25519.SR25519_SECRET_SIZE, kp.length), kp, msg, msg.length);
		boolean valid = sr.sr25519_verify(sig, msg, msg.length, Arrays.copyOfRange(kp, SR25519.SR25519_SECRET_SIZE, kp.length));
		if(valid) {
			System.out.println("testSignAndVerifyValid OK\n");
		}
		else {
			System.out.println("testSignAndVerifyValid FAIL\n");
		}
	}

	private void testSignAndVerifyInvalid()
	{
		SR25519 sr = new SR25519();
		
		byte[] kp = randomKeypair();
		byte[] msg = "hello world".getBytes();
		byte[] sig = new byte[SR25519.SR25519_SIGNATURE_SIZE];
		sr.sr25519_sign(sig, Arrays.copyOfRange(kp, SR25519.SR25519_SECRET_SIZE, kp.length), kp, msg, msg.length);
		++sig[0];
		boolean valid = sr.sr25519_verify(sig, msg, msg.length, Arrays.copyOfRange(kp, SR25519.SR25519_SECRET_SIZE, kp.length));
		if(! valid) {
			System.out.println("testSignAndVerifyInvalid OK\n");
		}
		else {
			System.out.println("testSignAndVerifyInvalid FAIL\n");
		}
	}
	
	private void testVerifyExisting()
	{
		SR25519 sr = new SR25519();
		
		byte[] pub = hexToBytes("741c08a06f41c596608f6774259bd9043304adfa5d3eea62760bd9be97634d63");
		byte[] msg = "this is a message".getBytes();
		byte[] sig = hexToBytes("decef12cf20443e7c7a9d406c237e90bcfcf145860722622f92ebfd5eb4b5b3990b6443934b5cba8f925a0ae75b3a77d35b8490cbb358dd850806e58eaf72904");
		boolean valid = sr.sr25519_verify(sig, msg, msg.length, pub);
		if(valid) {
			System.out.println("testVerifyExisting OK\n");
		}
		else {
			System.out.println("testVerifyExisting FAIL\n");
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
}


