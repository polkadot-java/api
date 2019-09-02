package org.polkadot.types.primitive;

import com.google.common.collect.Lists;
import org.polkadot.types.codec.Enum;

public class StorageHasher extends Enum {
    public StorageHasher(Object value) {
        super(Lists.newArrayList(
                "Blake2_128",
                "Blake2_256",
                "Twox128",
                "Twox256",
                "Twox64Concat"),
                value);
    }

    /**
     * @description Is the enum Blake2_128?
     */
    public boolean isBlake2128() {
        return this.toNumber() == 0;
    }

    /**
     * @description Is the enum Blake2_256?
     */
    public boolean isBlake2256() {
        return this.toNumber() == 1;
    }

    /**
     * @description Is the enum Twox128?
     */
    public boolean isTwox128() {
        return this.toNumber() == 2;
    }

    /**
     * @description Is the enum Twox256?
     */
    public boolean isTwox256() {
        return this.toNumber() == 3;
    }

    /**
     * @description Is the enum isTwox64Concat?
     */
    public boolean isTwox64Concat() {
        return this.toNumber() == 4;
    }

    @Override
    public Object toJson() {
        // This looks prettier in the generated JSON
        return this.toString();
    }
}
