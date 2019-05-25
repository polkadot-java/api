package org.polkadot.api;

import org.polkadot.types.rpc.SignedBlock;
import org.polkadot.types.type.EventRecord;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ApiUtils {
    private static final Logger logger = LoggerFactory.getLogger(ApiUtils.class);

    //export default function filterEvents (extHash: U8a, { block: { extrinsics, header } }: SignedBlock, allEvents: Array<EventRecord>): Array<EventRecord> | undefined {
    public static List<EventRecord> filterEvents(byte[] extHash, SignedBlock signedBlock, List<EventRecord> allEvents) {
        // extrinsics to hashes
        String myHash = Utils.u8aToHex(extHash);
        List<String> allHashes = signedBlock.getBlock().getExtrinsics()
                .stream().map(ext -> ext.getHash().toHex())
                .collect(Collectors.toList());

        // find the index of our extrinsic in the block
        int index = allHashes.indexOf(myHash);
        // if we do get the block after finalized, it _should_ be there
        if (index < 0) {
            logger.warn("block {} Unable to find extrinsic {} inside {}",
                    signedBlock.getBlock().getHeader().getHash(), myHash, allHashes);
            return null;
        }

        return allEvents.stream().filter(eventRecord -> {
            // only ApplyExtrinsic has the extrinsic index
            return eventRecord.getPahase().isApplyExtrinsic() && eventRecord.getPahase().asApplyExtrinsic().intValue() == index;
        }).collect(Collectors.toList());
    }
}
