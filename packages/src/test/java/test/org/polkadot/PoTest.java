package test.org.polkadot;

import com.google.common.primitives.UnsignedBytes;
import net.jpountz.xxhash.StreamingXXHash64;
import net.jpountz.xxhash.XXHashFactory;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.polkadot.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class PoTest {
    public static void main(String[] args) throws IOException {

        //
        //byte[] data = "12345345234572".getBytes("UTF-8");
        //int seed = 0x9747b28c;
        //
        //// * xxhash64AsValue('abcd', 0xabcd)); // => e29f70f8b8c96df7
        //
        //testXXhash(data, seed);
        //
        //testXXhash("abcd".getBytes(), 0xabcd);
        //
        //
        //testlebe();


        //testBlake2b("abc".getBytes(), 128, null);
        //testBlake2b("abc".getBytes(), 256, null);
        //testBlake2b("abc".getBytes(), 512, null);

        System.out.println(UnsignedBytes.checkedCast(129));
        //testclass();
    }

    interface TestInter {
        static String getCName() {
            //return Thread.currentThread().getStackTrace()[1].getClassName();
            return MethodHandles.lookup().lookupClass().getName();
        }
    }

   static class TestA implements TestInter {

    }

    static void testclass() {
        TestA testA = new TestA();
        System.out.println(TestInter.getCName());
        Class<?> aClass = MethodHandles.lookup().lookupClass();
        System.out.println(aClass);

        System.out.println(new Object(){}.getClass().getEnclosingClass());
        //System.out.println(SecurityManager.getClassContext()[0].getName());
        System.out.println(new Throwable().getStackTrace()[0].getClassName());
        System.out.println(Thread.currentThread().getStackTrace()[1].getClassName());

        //Optional<Class<?>> myself = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE) .walk(s -> s.map(StackWalker.StackFrame::getDeclaringClass) .findFirst());

        //System.out.println(Reflection.getCallerClass());
    }
    /**
     * it('returns a 256-bit value (as specified)', () => {
     * expect(
     * blake2AsU8a('abc', 256)
     * ).toEqual(
     * new Uint8Array([189, 221, 129, 60, 99, 66, 57, 114, 49, 113, 239, 63, 238, 152, 87, 155, 148, 150, 78, 59, 177, 203, 62, 66, 114, 98, 200, 192, 104, 213, 35, 25])
     * );
     * });
     * <p>
     * <p>
     * it('returns a 128-bit value (as specified)', () => {
     * expect(
     * blake2AsU8a('abc', 128)
     * ).toEqual(
     * new Uint8Array([207, 74, 183, 145, 198, 43, 141, 43, 33, 9, 201, 2, 117, 40, 120, 22])
     * );
     * });
     * <p>
     * it('returns a 256-bit value (as specified)', () => {
     * expect(
     * blake2AsU8a('abc', 256)
     * ).toEqual(
     * new Uint8Array([189, 221, 129, 60, 99, 66, 57, 114, 49, 113, 239, 63, 238, 152, 87, 155, 148, 150, 78, 59, 177, 203, 62, 66, 114, 98, 200, 192, 104, 213, 35, 25])
     * );
     * });
     * <p>
     * it('returns a 512-bit value (as specified)', () => {
     * expect(
     * blake2AsU8a('abc', 512)
     * ).toEqual(
     * hexToU8a('0xba80a53f981c4d0d6a2797b69f12f6e94c212f14685ac4b74b12bb6fdbffa2d17d87c5392aab792dc252d5de4533cc9518d38aa8dbf1925ab92386edd4009923')
     * );
     * });
     *
     * @param data
     * @param bitLength
     * @param key
     */

// * blake2AsU8a('abc'); // => [0xba, 0x80, 0xa53, 0xf98, 0x1c, 0x4d, 0x0d]
    public static void testBlake2b(byte[] data, int bitLength, byte[] key) {

        int byteLength = (int) Math.ceil(bitLength / 8F);

        //public Blake2bDigest(byte[] key, int digestLength, byte[] salt, byte[] personalization)

        Blake2bDigest blake2bkeyed = new Blake2bDigest(key, byteLength, null, null);
        //for (int tv = 0; tv < keyedTestVectors.length; tv++) {
        //
        //    byte[] input = Hex.decode(keyedTestVectors[tv][0]);
        blake2bkeyed.reset();

        blake2bkeyed.update(data, 0, data.length);
        byte[] keyedHash = new byte[64];
        int digestLength = blake2bkeyed.doFinal(keyedHash, 0);

        //if (!Arrays.areEqual(Hex.decode(keyedTestVectors[tv][2]), keyedHash)) {
        //    fail("Blake2b mismatch on test vector ",
        //            keyedTestVectors[tv][2],
        //            new String(Hex.encode(keyedHash)));
        //}
        for (int i = 0; i < digestLength; i++) {
            System.out.print(UnsignedBytes.toInt(keyedHash[i]) + ", ");

        }
        System.out.println();

        System.out.println(Utils.u8aToHex(keyedHash));
        //System.out.println(Arrays.toString(keyedHash));
        //offsetTest(blake2bkeyed, input, keyedHash);
        //}

    }

    //public void performTest() throws Exception {
    //    // test keyed test vectors:
    //
    //    Blake2bDigest blake2bkeyed = new Blake2bDigest(Hex.decode(keyedTestVectors[0][1]));
    //    for (int tv = 0; tv < keyedTestVectors.length; tv++) {
    //
    //        byte[] input = Hex.decode(keyedTestVectors[tv][0]);
    //        blake2bkeyed.reset();
    //
    //        blake2bkeyed.update(input, 0, input.length);
    //        byte[] keyedHash = new byte[64];
    //        blake2bkeyed.doFinal(keyedHash, 0);
    //
    //        if (!Arrays.areEqual(Hex.decode(keyedTestVectors[tv][2]), keyedHash)) {
    //            fail("Blake2b mismatch on test vector ",
    //                    keyedTestVectors[tv][2],
    //                    new String(Hex.encode(keyedHash)));
    //        }
    //
    //        offsetTest(blake2bkeyed, input, keyedHash);
    //    }
    //
    //    Blake2bDigest blake2bunkeyed = new Blake2bDigest();
    //    // test unkeyed test vectors:
    //    for (int i = 0; i < unkeyedTestVectors.length; i++) {
    //
    //        try {
    //            // blake2bunkeyed.update(
    //            // unkeyedTestVectors[i][1].getBytes("UTF-8"));
    //            // test update(byte b)
    //            byte[] unkeyedInput = unkeyedTestVectors[i][1]
    //                    .getBytes("UTF-8");
    //            for (int j = 0; j < unkeyedInput.length; j++) {
    //                blake2bunkeyed.update(unkeyedInput[j]);
    //            }
    //        } catch (UnsupportedEncodingException e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //        byte[] unkeyedHash = new byte[64];
    //        blake2bunkeyed.doFinal(unkeyedHash, 0);
    //        blake2bunkeyed.reset();
    //
    //        if (!Arrays.areEqual(Hex.decode(unkeyedTestVectors[i][0]),
    //                unkeyedHash)) {
    //            fail("Blake2b mismatch on test vector ",
    //                    unkeyedTestVectors[i][0],
    //                    new String(Hex.encode(unkeyedHash)));
    //        }
    //    }
    //}


    public static void testlebe() {

        BigInteger bigInteger = BigInteger.valueOf(1557849267933L);
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asLongBuffer().put(bigInteger.longValue());
        System.out.println("BIG_ENDIAN");

        for (int i = 0; i < buffer.array().length; i++) {
            System.out.print(i + " " + buffer.array()[i] + ",");
        }
        System.out.println();
        System.out.println(Arrays.toString(buffer.array()));

        buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.asLongBuffer().put(bigInteger.longValue());
        System.out.println("LITTLE_ENDIAN");
        for (int i = 0; i < buffer.array().length; i++) {
            System.out.print(i + " " + buffer.array()[i] + ",");
        }
        System.out.println();
        System.out.println(Arrays.toString(buffer.array()));
    }

    public static void testXXhash(byte[] data, long seed) throws IOException {
        XXHashFactory factory = XXHashFactory.fastestInstance();

        //byte[] data = "12345345234572".getBytes("UTF-8");
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        //int seed = 0x9747b28c; // used to initialize the hash value, use whatever
        // value you want, but always the same
        StreamingXXHash64 streamingXXHash64 = factory.newStreamingHash64(seed);

        //StreamingXXHash32 hash32 = factory.newStreamingHash32(seed);
        byte[] buf = new byte[8]; // for real-world usage, use a larger buffer, like 8192 bytes
        for (; ; ) {
            int read = in.read(buf);
            if (read == -1) {
                break;
            }
            streamingXXHash64.update(buf, 0, read);
        }
        long hash = streamingXXHash64.getValue();
        System.out.println("value :" + Arrays.toString(data));
        System.out.println("seed :" + seed);
        System.out.println("hash :" + Long.toHexString(hash));
    }
}
