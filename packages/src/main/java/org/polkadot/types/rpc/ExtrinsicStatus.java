package org.polkadot.types.rpc;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.EnumType;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Null;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.type.Hash;
import org.polkadot.utils.MapUtils;

import java.util.LinkedHashMap;

/**
 * An {@link org.polkadot.types.codec.EnumType} that indicates the status of the {@link org.polkadot.type.extrinsics} as been submitted
 */
//export default class ExtrinsicStatus extends EnumType<Future | Ready | Finalized | Usurped | Broadcast | Dropped | Invalid> {
public class ExtrinsicStatus extends EnumType {
    public ExtrinsicStatus(Object value) {
        this(value, -1);
    }

    public ExtrinsicStatus(Object value, int index) {
        super(new Types.ConstructorDef()
                        .add("Future", Future.class)
                        .add("Ready", Ready.class)
                        .add("Finalized", Finalized.class)
                        .add("Usurped", Usurped.class)
                        .add("Broadcast", Broadcast.class)
                        .add("Dropped", Dropped.class)
                        .add("Invalid", Invalid.class)
                , value, index,
                (LinkedHashMap<String, String>) MapUtils.ofMap("Finalised", "Finalized"));
    }


    /**
     * Returns the item as a {@link org.polkadot.types.rpc.ExtrinsicStatus.Broadcast}
     */
    public Broadcast asBroadcast() {
        return (Broadcast) this.value();
    }

    /**
     * Returns the item as a {@link org.polkadot.types.rpc.ExtrinsicStatus.Dropped}
     */
    public Dropped asDropped() {
        return (Dropped) this.value();
    }

    /**
     * Returns the item as a {@link org.polkadot.types.rpc.ExtrinsicStatus.Finalized}
     */
    public Finalized asFinalized() {
        return (Finalized) this.value();
    }

    /**
     * Returns the item as a {@link org.polkadot.types.rpc.ExtrinsicStatus.Future}
     */
    public Future asFuture() {
        return (Future) this.value();
    }

    /**
      * Returns the item as a {@link org.polkadot.types.rpc.ExtrinsicStatus.Invalid}
     */
    public Invalid asInvalid() {
        return (Invalid) this.value();
    }

    /**
     * Returns the item as a {@link org.polkadot.types.rpc.ExtrinsicStatus.Ready}
     */
    public Ready asReady() {
        return (Ready) this.value();
    }

    /**
     * Returns the item as a {@link org.polkadot.types.rpc.ExtrinsicStatus.Usurped}
     */
    public Usurped asUsurped() {
        return (Usurped) this.value();
    }

    /**
     * Returns true if the status is boadcast
     */
    public boolean isBroadcast() {
        return this.isType("Broadcast");
    }

    /**
     * Returns true if the status is dropped
     */
    public boolean isDropped() {
        return this.isType("Dropped");
    }

    /**
     * Returns true if the status is finalized
     */
    public boolean isFinalized() {
        return this.isType("Finalized");
    }

    /**
     * Returns true if the status is future
     */
    public boolean isFuture() {
        return this.isType("Future");
    }

    /**
     * Returns true if the status is invalid
     */
    public boolean isInvalid() {
        return this.isType("Invalid");
    }

    /**
     * Returns true if the status is eady
     */
    public boolean isReady() {
        return this.isType("Ready");
    }

    /**
     * Returns true if the status is usurped
     */
    public boolean isUsurped() {
        return this.isType("Usurped");
    }

    /**
     * An {@link org.polkadot.types.rpc.ExtrinsicStatus} indicating that the {@link org.polkadot.type.extrinsics} has been boradcast to peers
     */
    public static class Broadcast extends Vector<Text> {
        public Broadcast(Object value) {
            super(TypesUtils.getConstructorCodec(Text.class), value);
        }
    }


    /**
     * An {@link org.polkadot.types.rpc.ExtrinsicStatus} indicating that the {@link org.polkadot.type.extrinsics} has been dropped
     */
    public static class Dropped extends Null {
    }

    /**
     * An {@link org.polkadot.types.rpc.ExtrinsicStatus} indicating that the {@link org.polkadot.type.extrinsics}] has been finalized and included
     */
    public static class Finalized extends Hash {
        public Finalized(Object value) {
            super(value);
        }
    }

    /**
     * An {@link org.polkadot.types.rpc.ExtrinsicStatus} indicating that the {@link org.polkadot.type.extrinsics} has been added to the future queue
     */
    public static class Future extends Null {
    }

    /**
     * An {@link org.polkadot.types.rpc.ExtrinsicStatus} indicating that the {@link org.polkadot.type.extrinsics} has been added to the ready queue
     */
    public static class Ready extends Null {
    }

    /**
     * An {@link org.polkadot.types.rpc.ExtrinsicStatus} indicating that the {@link org.polkadot.type.extrinsics} is invalid
     */
    public static class Invalid extends Null {
    }

    /**
     * An {@link org.polkadot.types.rpc.ExtrinsicStatus} indicating that the {@link org.polkadot.type.extrinsics} has been usurped
     */
    public static class Usurped extends Hash {
        public Usurped(Object value) {
            super(value);
        }
    }

}
