package org.polkadot.types.metadata;

import org.polkadot.types.codec.CodecUtils;
import org.polkadot.types.codec.CreateType;
import org.polkadot.types.codec.TypeRegistry;
import org.polkadot.types.metadata.v0.MetadataV0;
import org.polkadot.types.metadata.v1.MetadataV1;
import org.polkadot.types.metadata.v2.MetadataV2;
import org.polkadot.types.metadata.v3.MetadataV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataUtils {

    private static final Logger logger = LoggerFactory.getLogger(MetadataUtils.class);

    /**
     * // Quick and dirty flatten (.flat() not available)
     * export default function flattenUniq (list: Array<any>): Array<any> {
     * const flat = list.reduce((result, entry) => {
     * return result.concat(
     * Array.isArray(entry)
     * ? flattenUniq(entry)
     * : entry
     * );
     * }, []);
     * <p>
     * return [...new Set(flat)]
     * .filter((value: any) => value)
     * .sort();
     * }
     */
    public static List<Object> flattenUniq(List<Object> list) {
        ArrayList<Object> reduce = list.stream().reduce(new ArrayList<>(), (r, e) -> {
            if (e.getClass().isArray()) {
                r.addAll(flattenUniq(CodecUtils.arrayLikeToList(e)));
            } else {
                r.add(e);
            }
            return r;
        }, (u, t) -> null);

        LinkedHashSet<Object> set = new LinkedHashSet<>();
        set.addAll(reduce);
        return new ArrayList<>(set);
    }

    private static List<Object> extractTypes(List<String> types) {
        return types.stream().map(type -> {
            CreateType.TypeDef decoded = CreateType.getTypeDef(type);
            List<CreateType.TypeDef> sub = decoded.getSub();
            switch (decoded.getInfo()) {
                case Plain:
                    return decoded.getType();
                case Compact:
                case Option:
                case Vector:
                    //return extractTypes([(decoded.sub as TypeDef).type]);
                case Tuple:
                    //return extractTypes(
                    //        (decoded.sub as Array < TypeDef >).map((sub) = > sub.type)
                    return extractTypes(sub.stream().map(def -> def.getType()).collect(Collectors.toList()));
                default:
                    throw new UnsupportedOperationException("Unreachable" + decoded.getInfo());

            }
        }).collect(Collectors.toList());
    }


    //export default function validateTypes (types: Array<string>, throwError: boolean): void {
    public static void validateTypes(List<String> types, boolean throwError) {
        TypeRegistry typeRegistry = TypeRegistry.getDefaultRegistry();
        List<Object> realTypes = extractTypes(types);
        List<Object> uniqTypes = flattenUniq(realTypes);
        List<Object> missing = uniqTypes.stream().filter(t -> typeRegistry.get((String) t) == null).collect(Collectors.toList());

        if (!missing.isEmpty()) {
            String message = "Unknown types found, no types for " + missing;

            if (throwError) {
                throw new RuntimeException(message);
            } else {
                logger.error(message);
            }
        }
    }


}
