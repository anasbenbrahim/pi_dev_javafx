package esprit.tn.utils;

import esprit.tn.entities.Event;
import esprit.tn.entities.Reservation;
import java.util.*;

public class EventRecommender {
    /**
     * Recommande l'événement le plus populaire à partir d'une liste de noms d'événements.
     */
    public static String recommendEvent(List<String> allReservations) {
        if (allReservations == null || allReservations.isEmpty()) {
            return "Aucun événement à recommander.";
        }
        Map<String, Integer> count = new HashMap<>();
        for (String event : allReservations) {
            count.put(event, count.getOrDefault(event, 0) + 1);
        }
        return Collections.max(count.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /**
     * Prédit le nombre de réservations pour un nouvel événement en se basant sur la moyenne
     * des réservations des événements du même type.
     */
    public static int predictReservations(Event newEvent, List<Event> events, List<Reservation> reservations) {
        Map<String, List<Integer>> typeToReservationCounts = new HashMap<>();
        for (Event event : events) {
            int count = (int) reservations.stream().filter(r -> r.getEventId() == event.getId()).count();
            typeToReservationCounts
                .computeIfAbsent(event.getType(), k -> new ArrayList<>())
                .add(count);
        }
        List<Integer> counts = typeToReservationCounts.getOrDefault(newEvent.getType(), List.of());
        if (counts.isEmpty()) return 1; // valeur par défaut si aucun historique
        return (int) counts.stream().mapToInt(Integer::intValue).average().orElse(1);
    }
}
