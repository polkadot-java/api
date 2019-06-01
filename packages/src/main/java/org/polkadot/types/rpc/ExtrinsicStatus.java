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
 * @name ExtrinsicStatus
 * An [[EnumType]] that indicates the status of the [[Extrinsic]] as been submitted
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
     * Returns the item as a [[Broadcast]]
     */
    public Broadcast asBroadcast() {
        return (Broadcast) this.value();
    }

    /**
     * Returns the item as a [[Dropped]]
     */
    public Dropped asDropped() {
        return (Dropped) this.value();
    }

    /**
     * Returns the item as a [[Finalized]]
     */
    public Finalized asFinalized() {
        return (Finalized) this.value();
    }

    /**
     * Returns the item as a [[Future]]
     */
    public Future asFuture() {
        return (Future) this.value();
    }

    /**
     * Returns the item as a [[Invalid]]
     */
    public Invalid asInvalid() {
        return (Invalid) this.value();
    }

    /**
     * Returns the item as a [[Ready]]
     */
    public Ready asReady() {
        return (Ready) this.value();
    }

    /**
     * Returns the item as a [[Usurped]]
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
     * @name Broadcast
     * An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been boradcast to peers
     */
    public static class Broadcast extends Vector<Text> {
        public Broadcast(Object value) {
            super(TypesUtils.getConstructorCodec(Text.class), value);
        }
    }


    /**
     * @name Dropped
     * An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been dropped
     */
    public static class Dropped extends Null {
    }

    /**
     * @name Finalized
     * An [[ExtrinsicStatus] indicating that the [[Extrinsic]]] has been finalized and included
     */
    public static class Finalized extends Hash {
        public Finalized(Object value) {
            super(value);
        }
    }

    /**
     * @name Future
     * An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been added to the future queue
     */
    public static class Future extends Null {
    }

    /**
     * @name Ready
     * An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been added to the ready queue
     */
    public static class Ready extends Null {
    }

    /**
     * @name Invalid
     * An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] is invalid
     */
    public static class Invalid extends Null {
    }

    /**
     * @name Usurped
     * An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been usurped
     */
    public static class Usurped extends Hash {
        public Usurped(Object value) {
            super(value);
        }
    }

}
