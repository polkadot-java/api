package org.polkadot.common.keyring.address;

import com.google.common.collect.Lists;

import java.util.List;

public class Defaults {

    //export type Prefix = 0 | 1 | 3 | 42 | 43 | 68 | 69;

    /**
     * const defaults = {
     * allowedDecodedLengths: [1, 2, 4, 8, 32],
     * // publicKey has prefix + 2 checksum bytes, short only prefix + 1 checksum byte
     * allowedEncodedLengths: [3, 4, 6, 10, 35],
     * allowedPrefix: [0, 1, 3, 42, 43, 68, 69] as Array<Prefix>,
     * prefix: 42 as Prefix
     * };
     */

    public static List<Integer> allowedDecodedLengths = Lists.newArrayList(1, 2, 4, 8, 32);
    public static List<Integer> allowedEncodedLengths = Lists.newArrayList(3, 4, 6, 10, 35);
    public static List<Integer> allowedPrefix = Lists.newArrayList(0, 1, 3, 42, 43, 68, 69);
    public static byte prefix = 42;
}

