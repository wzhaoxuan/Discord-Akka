package akka.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.model.actors.LoginActor;
import akka.model.actors.ChangeStatusActor;
import akka.pattern.Patterns;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Initialize Actor System
        ActorSystem system = ActorSystem.create("Discord_Prototype");

        // Create actors
        ActorRef loginActor = system.actorOf(LoginActor.props(), "loginActor");
        ActorRef changeStatusActor = system.actorOf(ChangeStatusActor.props(), "changeStatusActor");

        Scanner scanner = new Scanner(System.in);

        // Track username and status
        String currentUsername = "";
        String currentStatus = "Online"; // Default status

        try {
            // AUTOMATED TESTING OUTPUT BEFORE INTERACTIVE MENU
            System.out.println("\nTesting LoginActor...");
            Future<Object> testLoginFuture = Patterns.ask(loginActor, new LoginActor.LoginMessage("abc123", "12345"), 3000);
            LoginActor.LoginResponse testLoginResponse = (LoginActor.LoginResponse) Await.result(testLoginFuture, Duration.create(3, TimeUnit.SECONDS));
            System.out.println("Login Response: " + testLoginResponse.message);

            System.out.println("Testing ChangeStatusActor...");
            Future<Object> testStatusFuture = Patterns.ask(changeStatusActor, new ChangeStatusActor.ChangeStatusMessage("Online"), 3000);
            ChangeStatusActor.StatusResponse testStatusResponse = (ChangeStatusActor.StatusResponse) Await.result(testStatusFuture, Duration.create(3, TimeUnit.SECONDS));
            System.out.println("Status Response: " + testStatusResponse.message);

            // LOGIN PROCESS
            System.out.println("\nWelcome to Discord!");
            boolean loggedIn = false;

            while (!loggedIn) {
                System.out.print("Enter username (abc123): ");
                String username = scanner.nextLine();
                System.out.print("Enter password (12345): ");
                String password = scanner.nextLine();

                Future<Object> loginFuture = Patterns.ask(loginActor, new LoginActor.LoginMessage(username, password), 3000);
                LoginActor.LoginResponse loginResponse = (LoginActor.LoginResponse) Await.result(loginFuture, Duration.create(3, TimeUnit.SECONDS));

                System.out.println(loginResponse.message);

                if (loginResponse.success) {
                    loggedIn = true;
                    currentUsername = username; // Store logged-in username
                } else {
                    System.out.println("Login failed! Try again.");
                }
            }

            // MAIN MENU AFTER LOGIN
            boolean exit = false;

            while (!exit) {
                System.out.println("\n=== User: " + currentUsername + " | Status: " + currentStatus + " ===");
                System.out.println("Select a functionality:");
                System.out.println("1. Change Status");
                System.out.println("2. Exit");
                System.out.print("Enter your choice: ");

                int choice = -1; // Default invalid choice
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number (1 or 2).");
                    continue; // Restart the loop
                }

                switch (choice) {
                    case 1: // Change Status
                        System.out.println("Available statuses: Online, Idle, Do Not Disturb, Invisible");
                        String newStatus = "";
                        boolean validStatus = false;

                        while (!validStatus) {
                            System.out.print("Enter your new status: ");
                            newStatus = scanner.nextLine();

                            // Check if the status is valid
                            if (newStatus.equalsIgnoreCase("Online") ||
                                    newStatus.equalsIgnoreCase("Idle") ||
                                    newStatus.equalsIgnoreCase("Do Not Disturb") ||
                                    newStatus.equalsIgnoreCase("Invisible")) {
                                validStatus = true;
                            } else {
                                System.out.println("Invalid status! Please choose from: Online, Idle, Do Not Disturb, Invisible.");
                            }
                        }

                        // Send the valid status to the ChangeStatusActor
                        Future<Object> statusFuture = Patterns.ask(changeStatusActor, new ChangeStatusActor.ChangeStatusMessage(newStatus), 3000);
                        ChangeStatusActor.StatusResponse statusResponse = (ChangeStatusActor.StatusResponse) Await.result(statusFuture, Duration.create(3, TimeUnit.SECONDS));

                        currentStatus = newStatus; // Update the current status
                        System.out.println(statusResponse.message);
                        break;

                    case 2: // Exit
                        System.out.println("Exiting Discord... Goodbye, " + currentUsername + "!");
                        exit = true; // Set exit flag to true
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            system.terminate();
        }
    }
}




