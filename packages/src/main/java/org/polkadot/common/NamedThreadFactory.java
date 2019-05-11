package org.polkadot.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    private ThreadGroup group;
    private AtomicInteger threadCountor = new AtomicInteger(0);
    private String name;

    public NamedThreadFactory(String perfix) {
        this.name = perfix;
        group = new ThreadGroup(name);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(group, runnable, name + "-" + threadCountor.incrementAndGet());
        return thread;
    }

    public String getName() {
        return name;
    }

    public ThreadGroup getGroup() {
        return group;
    }
}
