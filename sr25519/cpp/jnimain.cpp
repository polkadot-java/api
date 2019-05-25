#include "org_polkadot_sr25519_SR25519.h"

extern "C" {
#include "sr25519.h"
}

#include <vector>

typedef unsigned char Byte;
typedef std::vector<Byte> ByteArray;

ByteArray jByteArrayToVector(JNIEnv * env, const jbyteArray & input)
{
	const int len = env->GetArrayLength(input);
	ByteArray result(len);;
	env->GetByteArrayRegion(input, 0, len, reinterpret_cast<jbyte*>(&result[0]));
	return result;
}

void setJByteArray(JNIEnv * env, jbyteArray & output, const Byte * input, const int len)
{
	env->SetByteArrayRegion(output, 0, len, reinterpret_cast<const jbyte*>(input));
}

int getJByteArrayLength(JNIEnv * env, const jbyteArray & input)
{
	return env->GetArrayLength(input);
}

extern "C" {

JNIEXPORT void JNICALL Java_org_polkadot_sr25519_SR25519_test1
  (JNIEnv * env, jobject, jbyteArray i_input, jbyteArray i_output)
{
	ByteArray input = jByteArrayToVector(env, i_input);
	const int len = input.size();
	for(int i = 0; i < len / 2; ++i) {
		std::swap(input[i], input[len - 1 -i]);
	}
	setJByteArray(env, i_output, &input[0], input.size());
}

JNIEXPORT void JNICALL Java_org_polkadot_sr25519_SR25519_sr25519_1derive_1keypair_1hard
  (JNIEnv * env, jobject, jbyteArray i_keypair_out, jbyteArray i_pair_ptr, jbyteArray i_cc_ptr)
{
	ByteArray keypair_out(getJByteArrayLength(env, i_keypair_out));
	ByteArray pair_ptr;
	ByteArray cc_ptr;
	
	pair_ptr = jByteArrayToVector(env, i_pair_ptr);
	cc_ptr = jByteArrayToVector(env, i_cc_ptr);
	sr25519_derive_keypair_hard(&keypair_out[0], &pair_ptr[0], &cc_ptr[0]);
	setJByteArray(env, i_keypair_out, &keypair_out[0], keypair_out.size());
}

JNIEXPORT void JNICALL Java_org_polkadot_sr25519_SR25519_sr25519_1derive_1keypair_1soft
  (JNIEnv * env, jobject, jbyteArray i_keypair_out, jbyteArray i_pair_ptr, jbyteArray i_cc_ptr)
{
	ByteArray keypair_out(getJByteArrayLength(env, i_keypair_out));
	ByteArray pair_ptr;
	ByteArray cc_ptr;
	
	pair_ptr = jByteArrayToVector(env, i_pair_ptr);
	cc_ptr = jByteArrayToVector(env, i_cc_ptr);
	sr25519_derive_keypair_soft(&keypair_out[0], &pair_ptr[0], &cc_ptr[0]);
	setJByteArray(env, i_keypair_out, &keypair_out[0], keypair_out.size());
}

JNIEXPORT void JNICALL Java_org_polkadot_sr25519_SR25519_sr25519_1derive_1public_1soft
  (JNIEnv * env, jobject, jbyteArray i_keypair_out, jbyteArray i_pair_ptr, jbyteArray i_cc_ptr)
{
	ByteArray keypair_out(getJByteArrayLength(env, i_keypair_out));
	ByteArray pair_ptr;
	ByteArray cc_ptr;
	
	pair_ptr = jByteArrayToVector(env, i_pair_ptr);
	cc_ptr = jByteArrayToVector(env, i_cc_ptr);
	sr25519_derive_public_soft(&keypair_out[0], &pair_ptr[0], &cc_ptr[0]);
	setJByteArray(env, i_keypair_out, &keypair_out[0], keypair_out.size());
}

JNIEXPORT void JNICALL Java_org_polkadot_sr25519_SR25519_sr25519_1keypair_1from_1seed
  (JNIEnv * env, jobject, jbyteArray i_keypair_out, jbyteArray i_seed_ptr)
{
	ByteArray keypair_out(getJByteArrayLength(env, i_keypair_out));
	ByteArray seed_ptr;
	seed_ptr = jByteArrayToVector(env, i_seed_ptr);
	sr25519_keypair_from_seed(&keypair_out[0], &seed_ptr[0]);
	setJByteArray(env, i_keypair_out, &keypair_out[0], keypair_out.size());
}

JNIEXPORT void JNICALL Java_org_polkadot_sr25519_SR25519_sr25519_1sign
  (JNIEnv * env, jobject, jbyteArray i_signature_out, jbyteArray i_public_ptr, jbyteArray i_secret_ptr, jbyteArray i_message_ptr, jint i_message_length)
{
	ByteArray signature_out(getJByteArrayLength(env, i_signature_out));
	
	ByteArray public_ptr = jByteArrayToVector(env, i_public_ptr);
	ByteArray secret_ptr = jByteArrayToVector(env, i_secret_ptr);
	ByteArray message_ptr = jByteArrayToVector(env, i_message_ptr);
	
	sr25519_sign(&signature_out[0], &public_ptr[0], &secret_ptr[0], &message_ptr[0], i_message_length);
	
	setJByteArray(env, i_signature_out, &signature_out[0], signature_out.size());
}

JNIEXPORT jboolean JNICALL Java_org_polkadot_sr25519_SR25519_sr25519_1verify
  (JNIEnv * env, jobject, jbyteArray i_signature_ptr, jbyteArray i_message_ptr, jint i_message_length, jbyteArray i_public_ptr)
{
	ByteArray signature_ptr = jByteArrayToVector(env, i_signature_ptr);
	ByteArray message_ptr = jByteArrayToVector(env, i_message_ptr);
	ByteArray public_ptr = jByteArrayToVector(env, i_public_ptr);
	
	return sr25519_verify(&signature_ptr[0], &message_ptr[0], i_message_length, &public_ptr[0]);
}


}
