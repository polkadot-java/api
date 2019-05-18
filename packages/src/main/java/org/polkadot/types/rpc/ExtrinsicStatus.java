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
 * @description An [[EnumType]] that indicates the status of the [[Extrinsic]] as been submitted
 */
//export default class ExtrinsicStatus extends EnumType<Future | Ready | Finalized | Usurped | Broadcast | Dropped | Invalid> {
public class ExtrinsicStatus extends EnumType {


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
     * @description Returns the item as a [[Broadcast]]
     */
    public Broadcast asBroadcast() {
        return (Broadcast) this.value();
    }

    /**
     * @description Returns the item as a [[Dropped]]
     */
    public Dropped asDropped() {
        return (Dropped) this.value();
    }

    /**
     * @description Returns the item as a [[Finalized]]
     */
    public Finalized asFinalized() {
        return (Finalized) this.value();
    }

    /**
     * @description Returns the item as a [[Future]]
     */
    public Future asFuture() {
        return (Future) this.value();
    }

    /**
     * @description Returns the item as a [[Invalid]]
     */
    public Invalid asInvalid() {
        return (Invalid) this.value();
    }

    /**
     * @description Returns the item as a [[Ready]]
     */
    public Ready asReady() {
        return (Ready) this.value();
    }

    /**
     * @description Returns the item as a [[Usurped]]
     */
    public Usurped asUsurped() {
        return (Usurped) this.value();
    }

    /**
     * @description Returns true if the status is boadcast
     */
    public boolean isBroadcast() {
        return this.isType("Broadcast");
    }

    /**
     * @description Returns true if the status is dropped
     */
    public boolean isDropped() {
        return this.isType("Dropped");
    }

    /**
     * @description Returns true if the status is finalized
     */
    public boolean isFinalized() {
        return this.isType("Finalized");
    }

    /**
     * @description Returns true if the status is future
     */
    public boolean isFuture() {
        return this.isType("Future");
    }

    /**
     * @description Returns true if the status is invalid
     */
    public boolean isInvalid() {
        return this.isType("Invalid");
    }

    /**
     * @description Returns true if the status is eady
     */
    public boolean isReady() {
        return this.isType("Ready");
    }

    /**
     * @description Returns true if the status is usurped
     */
    public boolean isUsurped() {
        return this.isType("Usurped");
    }

    /**
     * @name Broadcast
     * @description An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been boradcast to peers
     */
    public static class Broadcast extends Vector<Text> {
        public Broadcast(Object value) {
            super(TypesUtils.getConstructorCodec(Text.class), value);
        }
    }


    /**
     * @name Dropped
     * @description An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been dropped
     */
    public static class Dropped extends Null {
    }

    /**
     * @name Finalized
     * @description An [[ExtrinsicStatus] indicating that the [[Extrinsic]]] has been finalized and included
     */
    public static class Finalized extends Hash {
        public Finalized(Object value) {
            super(value);
        }
    }

    /**
     * @name Future
     * @description An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been added to the future queue
     */
    public static class Future extends Null {
    }

    /**
     * @name Ready
     * @description An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been added to the ready queue
     */
    public static class Ready extends Null {
    }

    /**
     * @name Invalid
     * @description An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] is invalid
     */
    public static class Invalid extends Null {
    }

    /**
     * @name Usurped
     * @description An [[ExtrinsicStatus]] indicating that the [[Extrinsic]] has been usurped
     */
    public static class Usurped extends Hash {
        public Usurped(Object value) {
            super(value);
        }
    }

}
