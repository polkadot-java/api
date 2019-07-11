package org.polkadot.types;

import com.alibaba.fastjson.JSON;
import org.polkadot.types.codec.CreateType;
import org.polkadot.utils.MapUtils;
import org.polkadot.utils.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContractAbi extends Types.Contract {

    public static void validateArgs(String name, List<Types.ContractABIArg> args) {
        //assert(Array.isArray(args), `Expected 'args' to exist on ${name}`);

        for (Types.ContractABIArg arg : args) {

            //assert(unknownKeys.length === 0, `Unknown keys ${unknownKeys.join(', ')} found in ABI args for ${name}`);
            //assert(isString(arg.name) && isString(arg.type), `${name} args should have valid name & type values`);
            assert arg.name != null && arg.type != null : name + " args should have valid name & type values";
        }
    }

    public static void validateDeploy(Types.ContractABI contractABI) {
        //const unknownKeys = Object.keys(deploy).filter((key) => !['args'].includes(key));
        //assert(unknownKeys.length === 0, `Unknown keys ${unknownKeys.join(', ')} found in ABI deploy`);
        validateArgs("deploy", contractABI.deploy.args);
    }

    public static void validateMethods(Types.ContractABI contractABI) {
        for (Types.ContractABIMethod method : contractABI.messages) {
            validateArgs("messages" + method.name, method.args);
        }

    }

    public static void validateAbi(Types.ContractABI abi) {
        assert abi.deploy != null && abi.messages != null && abi.name != null : "ABI should have deploy, messages & name sections";

        validateDeploy(abi);
        validateMethods(abi);
    }

    public ContractAbi(Types.ContractABI abi) {
        validateAbi(abi);

        this.messages = new LinkedHashMap<>();
        this.abi = abi;
        this.deploy = this.createEncoded("deploy", abi.deploy);

        for (Types.ContractABIMethod method : abi.messages) {
            String name = Utils.stringCamelCase(method.name);
            this.messages.put(name, this.createEncoded("messages." + name, method));
        }
    }

    private Types.ConstructorCodec createClazz(List<Types.ContractABIArg> args, Map<String, String> baseDef) {
        for (Types.ContractABIArg arg : args) {
            baseDef.put(arg.name, arg.type);
        }
        String type = JSON.toJSONString(baseDef);
        return CreateType.createClass(type);
    }

    private Types.ContractABIFn createEncoded(String name, Types.ContractABIMethodBase method) {
        List<Types.ContractABIArg> args = method.args.stream().map((arg -> {
            Types.ContractABIArg mapped = new Types.ContractABIArg();
            mapped.name = Utils.stringCamelCase(arg.name);
            mapped.type = arg.type;
            return mapped;
        })).collect(Collectors.toList());

        boolean isAbiMethod = method instanceof Types.ContractABIMethod;

        Types.ConstructorCodec clazz = this.createClazz(args,
                isAbiMethod
                        ? MapUtils.ofMap("__selector", "u32")
                        : new LinkedHashMap<>()
        );

        Map<String, Object> baseStruct = isAbiMethod
                ? MapUtils.ofMap("__selector", ((Types.ContractABIMethod) method).selector)
                : new LinkedHashMap<>();


        Types.ContractABIFn fn = new Types.ContractABIFn() {
            @Override
            public byte[] call(Object... params) {
                assert params.length == args.size() : "Expected " + args.size() + " arguments to contract " + name + ", found " + params.length;

                for (int i = 0; i < args.size(); i++) {
                    Types.ContractABIArg abiArg = args.get(i);
                    baseStruct.put(abiArg.name, params[i]);
                }

                Codec codec = clazz.newInstance(baseStruct);
                byte[] u8a = codec.toU8a();
                byte[] addLength = Utils.compactAddLength(u8a);
                return addLength;
            }

            @Override
            public List<Types.ContractABIArg> getArgs() {
                return args;
            }

            @Override
            public boolean isConstant() {
                return !isAbiMethod;
            }

            @Override
            public String getType() {
                return isAbiMethod ? ((Types.ContractABIMethod) method).returnType : null;
            }
        };

        return fn;
    }
}
