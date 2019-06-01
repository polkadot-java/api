package org.polkadot.types.type;


import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.primitive.U32;

/**
 * @name Schedule
 * @description Definition of the cost schedule and other parameterizations for wasm vm
 */
public class Schedule extends Struct {

    public Schedule(Object value) {
        super(new Types.ConstructorDef()
                        .add("version", U32.class)
                        .add("putCodePerByteCost", Gas.class)
                        .add("growMemCost", Gas.class)
                        .add("regularOpCost", Gas.class)
                        .add("returnDataPerByteCost", Gas.class)
                        .add("sandboxDataReadCost", Gas.class)
                        .add("sandboxDataWriteCost", Gas.class)
                        .add("maxStackHeight", U32.class)
                        .add("maxMemoryPages", U32.class)
                , value);
    }

    /**
     * @description Gas cost of a growing memory by single page.
     */
    public Gas getGrowMemCost() {
        return this.getField("growMemCost");
    }

    /**
     * @description What is the maximal memory pages amount is allowed to have for a contract.
     */
    public U32 getMaxMemoryPages() {
        return this.getField("maxMemoryPages");
    }

    /**
     * @description How tall the stack is allowed to grow?
     */
    public U32 getMaxStackHeight() {
        return this.getField("maxStackHeight");
    }

    /**
     * @description Cost of putting a byte of code into the storage.
     */
    public Gas getPutCodePerByteCost() {
        return this.getField("putCodePerByteCost");
    }

    /**
     * @description Gas cost of a regular operation.
     */
    public Gas getRegularOpCost() {
        return this.getField("regularOpCost");
    }

    /**
     * @description Gas cost per one byte returned.
     */
    public Gas getReturnDataPerByteCost() {
        return this.getField("returnDataPerByteCost");
    }

    /**
     * @description Gas cost per one byte read from the sandbox memory.
     */
    public Gas getSandboxDataReadCost() {
        return this.getField("sandboxDataReadCost");
    }

    /**
     * @description Gas cost per one byte written to the sandbox memory.
     */
    public Gas getSandboxDataWriteCost() {
        return this.getField("sandboxDataWriteCost");
    }

    /**
     * @description Version of the schedule.
     */
    public U32 getVersion() {
        return this.getField("version");
    }
}
