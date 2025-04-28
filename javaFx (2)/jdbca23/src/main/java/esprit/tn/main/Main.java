package esprit.tn.main;

import esprit.tn.entities.Event;
import esprit.tn.services.EventService;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Get database connection instance
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        // Create event service with connection
        EventService eventService = new EventService(dbConnection.getCnx());

        // Create a new event (using correct constructor)
        Event newEvent = new Event(
                "Tech Conference",
                "Annual technology conference",
                Date.valueOf(LocalDate.now()), // Convert LocalDate to java.sql.Date
                "Conference",
                "conference.jpg"
        );

        try {
            // Add the event
            eventService.ajouter(newEvent);
            System.out.println("Event added successfully!");

            // Get all events
            List<Event> events = eventService.getall();
            System.out.println("\nList of all events:");
            for (Event event : events) {
                System.out.println(event);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close connection when done
            dbConnection.closeConnection();
        }
    }
}