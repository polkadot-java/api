package org.polkadot.common;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventEmitter {

    public Map<EventType, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public interface EventType {

    }


    //public enum Events implements EventType {
    //    connected, disconnected, error, ready
    //    //'connected' | 'disconnected' | 'error','ready';
    //}

    @FunctionalInterface
    public interface EventListener {
        void onEvent(Object... args);

        default boolean isOnce() {
            return false;
        }
    }

    static class EventListenerProxy implements EventListener {
        Object context;
        EventListener listener;
        boolean once = false;

        @Override
        public boolean isOnce() {
            return once;
        }

        EventListenerProxy(EventListener listener, Object context) {
            this(listener, context, false);
        }

        EventListenerProxy(EventListener listener, Object context, boolean once) {
            this.context = context;
            this.listener = listener;
            this.once = once;
        }

        @Override
        public void onEvent(Object... args) {
            this.listener.onEvent(args);
        }
    }

    public List<?> getEventNames() {
        //TODO 2019-05-04 16:36
        throw new UnsupportedOperationException();
    }

    public List<EventListener> getListeners(String eventType) {


//TODO 2019-05-04 16:41
        throw new UnsupportedOperationException();
    }

    public int getListenerCount(String eventType) {
//TODO 2019-05-04 16:41
        throw new UnsupportedOperationException();
    }

    public boolean emit(EventType eventType, Object... args) {
        List<EventListener> eventListeners = listeners.get(eventType);
        if (CollectionUtils.isEmpty(eventListeners)) {
            return false;
        }
        List<EventListener> onces = Lists.newArrayList();
        for (EventListener eventListener : eventListeners) {
            eventListener.onEvent(args);
            if (eventListener.isOnce()) {
                onces.add(eventListener);
            }
        }
        eventListeners.removeAll(onces);

        return true;
    }

    public EventEmitter on(EventType eventType, EventListener listener) {
        return this.on(eventType, listener, null);
    }


    public EventEmitter on(EventType eventType, EventListener listener, Object context) {
        List<EventListener> eventListeners = this.listeners.computeIfAbsent(eventType, e -> new CopyOnWriteArrayList<>());
        eventListeners.add(new EventListenerProxy(listener, context));
        return this;
    }

    public EventEmitter addListener(EventType eventType, EventListener listener, Object context) {
        return on(eventType, listener, context);
    }

    public EventEmitter once(EventType eventType, EventListener listener) {
        return once(eventType, listener, null);

    }


    public EventEmitter once(EventType eventType, EventListener listener, Object context) {
        List<EventListener> eventListeners = this.listeners.computeIfAbsent(eventType, e -> new CopyOnWriteArrayList<>());
        eventListeners.add(new EventListenerProxy(listener, context, true));
        return this;
    }


    public EventEmitter removeListener(EventType event, EventListener listener, Object context, boolean once) {
//TODO 2019-05-04 16:45
        throw new UnsupportedOperationException();
    }

    public EventEmitter off(EventType event, EventListener listener, Object context, boolean once) {
//TODO 2019-05-04 16:45
        throw new UnsupportedOperationException();
    }

    public EventEmitter removeAllListener(EventType eventType) {
        //TODO 2019-05-04 16:45
        throw new UnsupportedOperationException();
    }

}
