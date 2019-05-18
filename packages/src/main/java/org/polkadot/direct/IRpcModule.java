package org.polkadot.direct;

import com.google.common.collect.Sets;
import org.polkadot.rpc.core.IRpc;

import java.util.Set;

public interface IRpcModule extends IModule<IRpc.RpcInterfaceSection> {
    IRpc.RpcInterfaceSection author();

    IRpc.RpcInterfaceSection chain();

    IRpc.RpcInterfaceSection state();

    IRpc.RpcInterfaceSection system();


    @Override
    default Set<String> sectionNames() {
        return Sets.newHashSet("author", "chain", "state", "system");
    }

    @Override
    default IRpc.RpcInterfaceSection section(String section) {
        switch (section) {
            case "author":
                return author();
            case "chain":
                return chain();
            case "state":
                return state();
            case "system":
                return system();
            default://TODO 2019-05-09 15:19
                throw new UnsupportedOperationException();
        }
    }
}
