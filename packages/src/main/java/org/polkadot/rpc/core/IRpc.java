package org.polkadot.rpc.core;


import com.onehilltech.promises.Promise;
import org.polkadot.direct.IRpcFunction;
import org.polkadot.direct.ISection;

public interface IRpc {

    //abstract class RpcInterfaceMethod implements IRpcFunction<Promise> {
    //    String subscription;
    //
    //    @Override
    //    public abstract Promise invoke(Object... params);
    //
    //    abstract Promise unsubscribe(int id);
    //}

    //T : () -> {}
    //T : codec
    //abstract class RpcInterfaceMethodNew<T extends IFunction.RpcResult> implements IFunction {
    //    //String subscription;
    //
    //    abstract Promise<T> invoke(Object... params);
    //
    //    //abstract Promise<> unsubscribe(int id);
    //}


    class RpcInterfaceSection extends ISection<IRpcFunction> {
        //Map<String, RpcInterfaceMethod> methods = new HashMap<>();

        //@Override
        //public IFunction function(String function) {
        //    return methods.get(function);
        //}
    }

    RpcInterfaceSection author();

    RpcInterfaceSection chain();

    RpcInterfaceSection state();

    RpcInterfaceSection system();

}


