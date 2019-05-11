package org.polkadot.types.primitive;


import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * @name Type
 * @description This is a extended version of String, specifically to handle types. Here we rely fully
 * on what string provides us, however we also adjust the types received from the runtime,
 * i.e. we remove the `T::` prefixes found in some types for consistency accross implementation.
 */
public class Type extends Text {

    static final String[] ALLOWED_BOXES = new String[]{"Compact", "Option", "Vec"};
    private int originalLength;

    interface Mapper {
        String apply(String value);
    }

    public Type(Object value) {
        // First decode it with Text
        // Then cleanup the textValue to get the @polkadot/types type, and pass the
        // sanitized value to constructor
        super(decodeType((new Text(value)).toString()));

        this.originalLength = (new Text(value)).getEncodedLength();
    }

    private static String decodeType(String value) {
        List<Mapper> mappings = Lists.newArrayList(
                // alias <T::InherentOfflineReport as InherentOfflineReport>::Inherent -> InherentOfflineReport
                Type.alias("<T::InherentOfflineReport as InherentOfflineReport>::Inherent", "InherentOfflineReport"),
                // alias TreasuryProposal from Proposal<T::AccountId, BalanceOf<T>>
                Type.alias("Proposal<T::AccountId, BalanceOf<T>>", "TreasuryProposal"),
                // <T::Balance as HasCompact>
                Type.cleanupCompact(),
                // Remove all the trait prefixes
                Type.removeTraits(),
                // remove PairOf<T> -> (T, T)
                Type.removePairOf(),
                // remove boxing, `Box<Proposal>` -> `Proposal`
                Type.removeWrap("Box"),
                // remove generics, `MisbehaviorReport<Hash, BlockNumber>` -> `MisbehaviorReport`
                Type.removeGenerics(),
                // alias String -> Text (compat with jsonrpc methods)
                Type.alias("String", "Text"),
                // alias () -> Null
                Type.alias("\\\\(\\\\)", "Null"),
                // alias Vec<u8> -> Bytes
                Type.alias("Compact<Index>", "IndexCompact"),
                // alias Vec<u8> -> Bytes
                Type.alias("Vec<u8>", "Bytes"),
                // alias RawAddress -> Address
                Type.alias("RawAddress", "Address"),
                // alias Lookup::Source to Address (_could be AccountId on certain chains)
                Type.alias("Lookup::Source", "Address"),
                // alias Lookup::Target to AccountId (always the case)
                Type.alias("Lookup::Target", "AccountId"),
                // flattens tuples with one value, `(AccountId)` -> `AccountId`
                Type.flattenSingleTuple()
        );

        for (Mapper mapping : mappings) {
            System.out.println(value);
            value = mapping.apply(value);
        }
        return value.trim();
    }

    @Override
    public int getEncodedLength() {
        return this.originalLength;
    }

    @Override
    public byte[] toU8a(boolean isBare) {
        // Note Since we are mangling what we get in beyond recognition, we really should
        // not allow the re-encoding. Additionally, this is probably more of a decoder-only
        // helper, so treat it as such.
        throw new UnsupportedOperationException("Type::toU8a: unimplemented");
    }


    private static Mapper alias(String src, String dest) {
        return (value -> value.replace(src, dest));
    }

    private static Mapper cleanupCompact() {
        return value -> {
            for (int index = 0; index < value.length(); index++) {
                if (value.charAt(index) != '<') {
                    continue;
                }

                int end = findClosing(value, index + 1) - 14;

                if (value.substring(end, end + 14).equals(" as HasCompact")) {
                    String substring = value.substring(index + 1, end);
                    value = "Compact<" + substring + ">";
                }
            }
            return value;
        };
    }

    private static int findClosing(String value, int start) {
        int depth = 0;

        for (int index = start; index < value.length(); index++) {
            if (value.charAt(index) == '>') {
                if (depth <= 0) {
                    return index;
                }
                depth--;
            } else if (value.charAt(index) == '<') {
                depth++;
            }

        }
        throw new RuntimeException("Unable to find closing matching <> on " + value + " (start " + start);
    }

    private static Mapper flattenSingleTuple() {
        return value -> value.replaceAll("\\(([^,]*)\\)", "$1");
    }

    private static Mapper removeGenerics() {
        return value -> {
            for (int index = 0; index < value.length(); index++) {
                if (value.charAt(index) == '<') {
                    // check against the allowed wrappers, be it Vec<..>, Option<...> ...
                    int finalIndex = index;
                    String finalValue = value;
                    String findBox = Arrays.stream(ALLOWED_BOXES).filter(box -> {
                        int start = finalIndex - box.length();
                        return start >= 0 && finalValue.substring(start, start + box.length()).equals(box);
                    }).findFirst().orElse(null);

                    if (findBox != null) {
                        int end = findClosing(value, index + 1);
                        value = value.substring(0, index) + value.substring(end + 1);
                    }

                }
            }
            return value;
        };
    }

    // remove the PairOf wrappers
    private static Mapper removePairOf() {
        return value -> {
            for (int index = 0; index < value.length() && index + 7 <= value.length(); index++) {

                if (value.substring(index, index + 7).equals("PairOf<")) {
                    int start = index + 7;
                    int end = findClosing(value, start);
                    String type = value.substring(start, end);

                    value = value.substring(0, index) + "(" + type + "," + type + ")" + value.substring(end + 1);
                }
            }
            return value;
        };
    }

    // remove the type traits
    private static Mapper removeTraits() {
        return value -> value
                // remove all whitespaces
                .replaceAll("\\s", "")
                // anything `T::<type>` to end up as `<type>`
                .replace("T::", "")
                // anything `Self::<type>` to end up as `<type>`
                .replace("Self::", "")
                // `system::` with `` - basically we find `<T as system::Trait>`
                .replace("system::", "")
                // replace `<T as Trait>::` (whitespaces were removed above)
                .replace("<TasTrait>::", "")
                // replace `<Self as Trait>::` (whitespaces were removed above)
                .replace("<SelfasTrait>::", "")
                // replace <Lookup as StaticLookup>
                .replace("<LookupasStaticLookup>", "Lookup")
                // replace `<...>::Type`
                .replace("::Type", "")
                // replace `wasm::*` eg. `wasm::PrefabWasmModule`
                .replace("wasm::", "");
    }

    // remove wrapping values, i.e. Box<Proposal> -> Proposal
    private static Mapper removeWrap(String check) {
        check = check + "<";
        String finalCheck = check;
        return value -> {
            int index = 0;
            while (index != -1) {
                index = value.indexOf(finalCheck);
                if (index != -1) {
                    int start = index + finalCheck.length();
                    int end = findClosing(value, start);

                    value = value.substring(0, index) + value.substring(start, end) + value.substring(end + 1);
                }
            }
            return value;
        };
    }
}
