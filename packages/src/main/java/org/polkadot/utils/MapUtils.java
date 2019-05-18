package org.polkadot.utils;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MapUtils {

    public static <K, V> Map<K, V> ofMap(K k1, V v1) {
        return ofMapInner(k1, v1);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2) {
        return ofMapInner(k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return ofMapInner(k1, v1, k2,v2, k3, v3);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return ofMapInner(k1, v1, k2,v2, k3, v3, k4, v4);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return ofMapInner(k1, v1, k2,v2, k3, v3, k4, v4, k5, v5);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return ofMapInner(k1, v1, k2,v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return ofMapInner(k1, v1, k2,v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }


    private static <K, V> Map<K, V> ofMapInner(Object... input) {
        LinkedHashMap<K, V> map = Maps.newLinkedHashMap();

        if ((input.length & 1) != 0) { // implicit nullcheck of input
            throw new InternalError("length is odd");
        }
        for (int i = 0; i < input.length; i += 2) {
            @SuppressWarnings("unchecked")
            K k = Objects.requireNonNull((K) input[i]);
            @SuppressWarnings("unchecked")
            V v = Objects.requireNonNull((V) input[i + 1]);

            map.put(k, v);

        }
        return map;

    }


}
