package org.polkadot.types.metadata.v1;

import com.google.common.collect.Lists;
import org.polkadot.types.interfaces.metadata.Types;
import org.polkadot.types.metadata.v0.Calls;
import org.polkadot.types.metadata.v0.Events;
import org.polkadot.types.metadata.v0.MetadataV0;
import org.polkadot.types.metadata.v0.Modules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ToV0 {

    static Modules.StorageMetadata storageV0(MetadataV1.MetadataModule mod) {
        if (mod.getStorage().isNone()) {
            return null;
        }
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("prefix", mod.getPrefix());
        values.put("functions",
                mod.getStorage().unwrap().stream().map(
                        (storage) -> {
                            Map<String, Object> storageValues = new LinkedHashMap<>();
                            storageValues.put("name", storage.getName());
                            storageValues.put("modifier", storage.getModifier().toNumber());
                            storageValues.put("type", new Modules.StorageFunctionType(storage.getType(), -1));
                            storageValues.put("default", storage.getFallback());
                            storageValues.put("documentation", storage.getDocs());
                            return new Modules.StorageFunctionMetadata(storageValues);
                        }
                ).collect(Collectors.toList())
        );
        return new Modules.StorageMetadata(values);
    }

    static Modules.ModuleMetadata moduleV0(MetadataV1.MetadataModule mod) {
        Map<String, Object> callValues = new LinkedHashMap<>();
        AtomicInteger funId = new AtomicInteger(0);
        callValues.put("name", "Call");
        callValues.put("functions", mod.getCalls().isNone()
                ? Lists.newArrayList()
                : mod.getCalls().unwrap().stream().map(
                (metadataCall -> {
                    Map<String, Object> funcValues = new LinkedHashMap<>();
                    funcValues.put("id", funId.getAndIncrement());
                    funcValues.put("name", metadataCall.getName());
                    funcValues.put("arguments", metadataCall.getArgs());
                    funcValues.put("documentation", metadataCall.getArgs());
                    return new Types.FunctionMetadataV7(funcValues);
                })

        ).collect(Collectors.toList()));


        Map<String, Object> values = new LinkedHashMap<>();

        values.put("name", "Module");
        values.put("call", new Modules.CallMetadata(callValues));

        return new Modules.ModuleMetadata(values);
    }


    static List<Modules.RuntimeModuleMetadata> modulesV0(MetadataV1 v1) {
        return v1.getModules().stream().map(
                (mod) -> {
                    Map<String, Object> values = new LinkedHashMap<>();
                    values.put("prefix", mod.getName());// passed from name, compact with casing
                    values.put("module", moduleV0(mod));
                    values.put("storage", storageV0(mod));
                    return new Modules.RuntimeModuleMetadata(values);
                }
        ).collect(Collectors.toList());
    }

    static Calls.OuterDispatchMetadata outerDispatchV0(MetadataV1 v1) {
        Map<String, Object> values = new LinkedHashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        values.put("name", "Call");
        values.put("calls", v1.getModules()
                .stream()
                .filter((mod) -> mod.getCalls().isSome())
                .map(
                        (mod -> {
                            Map<String, Object> callValues = new LinkedHashMap<>();
                            callValues.put("name", mod.getPrefix());
                            callValues.put("prefix", mod.getName());
                            callValues.put("index", index.getAndIncrement());

                            return new Calls.OuterDispatchCall(callValues);
                        })
                )
                .collect(Collectors.toList())
        );

        return new Calls.OuterDispatchMetadata(values);
    }

    static Events.OuterEventMetadata outerEventV0(MetadataV1 v1) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("name", "Event");
        values.put("events",
                v1.getModules()
                        .stream()
                        .filter((mod) -> mod.getEvents().isSome())
                        .map(
                                (mod) -> {
                                    ArrayList<Object> eventValues = Lists.newArrayList();
                                    eventValues.add(mod.getName());
                                    eventValues.add(mod.getEvents().unwrap().stream()
                                            .map(
                                                    (event) -> {
                                                        Map<String, Object> eventVaMap = new LinkedHashMap<>();
                                                        eventVaMap.put("name", event.getName());
                                                        eventVaMap.put("arguments", event.getArgs());
                                                        eventVaMap.put("documentation", event.getDocs());
                                                        return new Events.EventMetadata(eventVaMap);
                                                    }
                                            ).collect(Collectors.toList()));
                                    return new Events.OuterEventMetadataEvent(eventValues);
                                }
                        ).collect(Collectors.toList())
        );
        return new Events.OuterEventMetadata(values);
    }

    public static MetadataV0 toV0(MetadataV1 v1) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("outerEvent", outerEventV0(v1));
        values.put("modules", modulesV0(v1));
        values.put("outerDispatch", outerDispatchV0(v1));
        return new MetadataV0(values);
    }
}
