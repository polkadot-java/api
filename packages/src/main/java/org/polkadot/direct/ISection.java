package org.polkadot.direct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class ISection<F extends IFunction> {
    private static final Logger logger = LoggerFactory.getLogger(ISection.class);

    protected Map<String, F> functions = new HashMap<>();

    public F function(String function) {
        return functions.get(function);
    }

    public boolean addFunction(String name, F function) {
        boolean result = true;
        if (this.functions.containsKey(name)) {
            logger.error(" dup function name {}, {}, {}",
                    name, this.functions.get(name), function);
            result = false;
        }
        this.functions.put(name, function);
        return result;
    }
}
