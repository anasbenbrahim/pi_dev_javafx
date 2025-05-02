        package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus {
    private static final List<Consumer<String>> listeners = new ArrayList<>();

    public static void subscribe(Consumer<String> listener) {
        listeners.add(listener);
    }

    public static void publish(String event) {
        listeners.forEach(listener -> listener.accept(event));
    }
}
