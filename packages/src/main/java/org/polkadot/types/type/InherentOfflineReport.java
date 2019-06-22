package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Tuple;

/**
 * Describes the offline-reporting extrinsic
 */
//export default class InherentOfflineReport extends Tuple.with({}) {
public class InherentOfflineReport extends Tuple {
    public InherentOfflineReport() {
        super(new Types.ConstructorDef(), null);
    }
}