package org.polkadot.types.codec;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.polkadot.types.Codec;
import org.polkadot.types.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateType {
    public static enum TypeDefInfo {
        Compact,
        Enum,
        Option,
        Plain,
        Struct,
        Tuple,
        Vector,
        Linkage
    }

    public static class TypeDef {
        TypeDefInfo info;
        String name;
        String type;
        List<TypeDef> sub;

        public TypeDef(TypeDefInfo info, String name, String type, List<TypeDef> sub) {
            this.info = info;
            this.name = name;
            this.type = type;
            this.sub = sub;
        }

        public TypeDefInfo getInfo() {
            return info;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public List<TypeDef> getSub() {
            return sub;
        }
    }

    // safely split a string on ', ' while taking care of any nested occurences
    public static List<String> typeSplit(String type) {

        int sDepth = 0;
        int tDepth = 0;
        int vDepth = 0;
        int start = 0;

        List<String> result = new ArrayList<>();
        for (int i = 0; i < type.length(); i++) {
            char c = type.charAt(i);
            switch (c) {
                case ',':
                    // we are not nested, add the type
                    if (sDepth == 0 && tDepth == 0 && vDepth == 0) {
                        result.add(type.substring(start, i - start).trim());
                        start = i + 1;
                    }
                    break;
                // inc struct depth, start found
                case '{':
                    sDepth++;
                    break;
                // dec struct depth, end found
                case '}':
                    sDepth--;
                    break;
                // inc tuple depth, start found
                case '(':
                    tDepth++;
                    break;
                // dec tuple depth, end found
                case ')':
                    tDepth--;
                    break;
                // inc compact/vec depth, start found
                case '<':
                    vDepth++;
                    break;
                // dec compact/vec depth, end found
                case '>':
                    vDepth--;
                    break;
                default:
                    break;

            }
        }


        //assert(!sDepth && !tDepth && !vDepth, `Invalid defintion (missing terminators) found in ${type}`);

        // the final leg of the journey
        result.add(type.substring(start, type.length()).trim());
        return result;
    }

    private static String startingWith(String type, String start, String end) {
        if (start.length() >= type.length() || end.length() >= type.length()) {
            return null;
        }
        if (!type.substring(0, start.length()).equals(start)) {
            return null;
        }
        assert type.substring(type.length() - end.length()).equals(end) : "Expected " + start + " closing with " + end;

        return type.substring(start.length(), type.length() - end.length());
    }

    public static TypeDef getTypeDef(String type) {
        return getTypeDef(type, null);
    }

    public static TypeDef getTypeDef(String type, String name) {
        type = type.trim();

        TypeDef value = new TypeDef(TypeDefInfo.Plain, name, type, null);

        String subType = "";

        if ((subType = startingWith(type, "(", ")")) != null) {
            value.info = TypeDefInfo.Tuple;
            value.sub = typeSplit(subType).stream().map((inner) -> getTypeDef(inner, null)).collect(Collectors.toList());
        } else if ((subType = startingWith(type, "{", "}")) != null) {
            JSONObject parsed = JSONObject.parseObject(type);
            Set<String> keys = parsed.keySet();

            if (keys.size() == 1 && keys.stream().findFirst().get().equals("_enum")) {
                Object details = parsed.get("_enum");
                value.info = TypeDefInfo.Enum;
                // not as pretty, but remain compatible with oo7 for both struct and Array types
                if (details instanceof JSONArray) {
                    value.sub = ((JSONArray) details)
                            .stream()
                            .map((n) -> new TypeDef(TypeDefInfo.Plain, name, "Null", null))
                            .collect(Collectors.toList());
                } else if (details instanceof JSONObject) {
                    value.sub = ((JSONObject) details)
                            .entrySet()
                            .stream()
                            .map(e -> {
                                String iType = ((String) e.getValue());
                                if (StringUtils.isEmpty(iType)) {
                                    iType = "Null";
                                }
                                return new TypeDef(TypeDefInfo.Plain, e.getKey(), ((String) e.getValue()), null);
                            }).collect(Collectors.toList());
                } else {
                    //TODO 2019-05-06 20:12
                    throw new UnsupportedOperationException("" + details.getClass());
                }
            } else {
                value.info = TypeDefInfo.Struct;
                value.sub = keys.stream().map((n) -> getTypeDef(parsed.getString(n), n)).collect(Collectors.toList());
            }
        } else if ((subType = startingWith(type, "Compact<", ">")) != null) {
            value.info = TypeDefInfo.Compact;
            value.sub = Lists.newArrayList(getTypeDef(subType, null));
        } else if ((subType = startingWith(type, "Option<", ">")) != null) {
            value.info = TypeDefInfo.Option;
            value.sub = Lists.newArrayList(getTypeDef(subType, null));
        } else if ((subType = startingWith(type, "Vec<", ">")) != null) {
            value.info = TypeDefInfo.Vector;
            value.sub = Lists.newArrayList(getTypeDef(subType, null));
        } else if ((subType = startingWith(type, "Linkage<", ">")) != null) {
            value.info = TypeDefInfo.Linkage;
            value.sub = Lists.newArrayList(getTypeDef(subType, null));
        }
        return value;
    }

    public static Types.ConstructorCodec getTypeClass(TypeDef value) {
        Types.ConstructorCodec type = TypeRegistry.getDefaultRegistry().get(value.type);
        if (type != null) {
            return type;
        }

        assert CollectionUtils.isEmpty(value.sub) : "Expected subtype for " + value.info;

        switch (value.info) {
            case Compact:
                return Compact.with(
                        (Types.ConstructorCodec<UInt>) getTypeClass(value.sub.get(0))
                );
            case Enum:
                Types.ConstructorDef enumDefs = new Types.ConstructorDef();
                value.sub.forEach(def -> enumDefs.add(def.name, getTypeClass(def)));
                return EnumType.with(enumDefs);
            case Option:
                return Option.with(
                        getTypeClass(value.sub.get(0))
                );
            case Struct:
                Types.ConstructorDef structDefs = new Types.ConstructorDef();
                value.sub.forEach(def -> structDefs.add(def.name, getTypeClass(def)));
                return Struct.with(structDefs);
            case Tuple:
                Types.ConstructorDef tupleDef = new Types.ConstructorDef();
                value.sub.forEach(def -> tupleDef.add(def.name, getTypeClass(def)));
                return Tuple.with(tupleDef);
            case Vector:
                return Vector.with(
                        getTypeClass(value.sub.get(0))
                );
            case Linkage:
                return Linkage.withKey(
                        getTypeClass(value.sub.get(0))
                );
            default:
                throw new UnsupportedOperationException("Unable to determine type from " + value.type);
        }
    }

    public static Types.ConstructorCodec createClass(String type) {
        return getTypeClass(getTypeDef(type, null));
    }

    public static Codec createType(String type, Object value) {
        Types.ConstructorCodec typeClass = createClass(type);
        return typeClass.newInstance(value);

    }
}
