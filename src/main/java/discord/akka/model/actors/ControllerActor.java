package discord.akka.model.actors;

import akka.actor.ActorRef;
import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ControllerActor extends AbstractActor {
    private final ActorRef loginActor;
    private final ActorRef changeStatusActor;
    private final ActorRef profileActor;
    private final Scanner scanner = new Scanner(System.in);

    // Constructor
    public ControllerActor(ActorRef loginActor, ActorRef profileActor, ActorRef changeStatusActor) {
        this.loginActor = loginActor;
        this.profileActor = profileActor;
        this.changeStatusActor = changeStatusActor;
    }

    public static Props props(ActorRef loginActor, ActorRef profileActor, ActorRef changeStatusActor) {
        return Props.create(ControllerActor.class, () -> new ControllerActor(loginActor, profileActor, changeStatusActor));
    }

    public static class StartInteraction {}

    private void displayMenu() {
        System.out.println("\nWelcome to Discord Application!");
        System.out.println("1. Login");
        System.out.println("2. Signup");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    private void loginSuccessful(String currentUsername, String currentStatus) {
        int choice = 0;
        System.out.println("\n=== User: " + currentUsername + " | Status: " + currentStatus + " ===");
        System.out.println("Select a functionality:");
        System.out.println("1. Edit Profile");
        System.out.println("2. View Profile");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number (1 or 2).");
        }

        switch (choice) {
            case 1:
                // Edit Profile first
                profileActor.tell(new ProfileActor.UpdateProfileMessage(currentUsername, "", "", "", ""), getSelf());
                break;
            case 2:
                // View Profile
                profileActor.tell(new ProfileActor.GetProfileMessage(currentUsername), getSelf());
                break;
            case 3:
                // Back to Main Menu
                self().tell(new StartInteraction(), getSelf());
                break;
            default:
                System.out.println("Invalid option! Try again.");
                break;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartInteraction.class, msg -> {
                    displayMenu();
                    int choice = 0;
                    try {
                        choice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter an Integer");
                        scanner.nextLine();
                        self().tell(new StartInteraction(), getSelf());
                        return; // Skip further processing
                    }

                    if (choice == 1) {
                        System.out.print("Enter username: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();

                        loginActor.tell(new LoginActor.LoginMessage(username, password), getSelf());
                    } else if (choice == 2) {
                        System.out.print("Choose a username: ");
                        String username = scanner.nextLine();
                        System.out.print("Choose a password: ");
                        String password = scanner.nextLine();

                        loginActor.tell(new LoginActor.SignupMessage(username, password), getSelf());
                    } else if (choice == 3) {
                        System.out.println("Exiting. Goodbye!");
                        getContext().getSystem().terminate();
                    } else {
                        System.out.println("Invalid option. Try again.");
                        self().tell(new StartInteraction(), getSelf()); // Restart the interaction
                    }
                })
                .match(LoginActor.ResponseMessage.class, response -> {
                    // Handle the response from LoginActor
                    if (response.isSignup) {
                        // Signup response
                        System.out.println(response.message);
                        self().tell(new StartInteraction(), getSelf()); // Prompt the user to log in
                    } else if (!response.success) {
                        // Login failed
                        System.out.println(response.message);
                        self().tell(new StartInteraction(), getSelf());
                    } else {
                        // Login successful
                        System.out.println(response.message);
                        loginSuccessful(response.username, response.status);  // Show main menu after successful login
                    }
                })
                .match(ProfileActor.ProfileResponse.class, profileResponse -> {
                    if (profileResponse.success) {
                        System.out.println(profileResponse.message);
                        // After profile is updated, allow user to change status
                        changeUserStatus(profileResponse.username);
                    } else {
                        System.out.println("Profile update failed: " + profileResponse.message);
                    }
                })
                .match(ProfileActor.Profile.class, profile -> {
                    // This message is sent when viewing the profile.
                    System.out.println("\n=== View Profile ===");
                    System.out.println(profile);
                    loginSuccessful(profile.username, profile.status);// Return to main menu after viewing the profile
                })
                .match(ChangeStatusActor.ChangeStatusMessage.class, statusResponse -> {
                    if (statusResponse.success) {
                        System.out.println(statusResponse.message);
                        loginSuccessful(statusResponse.username, statusResponse.newStatus);
                    }
                })
                .build();
    }

    private void changeUserStatus(String currentUsername) {
        System.out.println("\nAvailable statuses: Online, Idle, Do Not Disturb, Invisible");
        boolean validStatus = false;
        while (!validStatus) {
            System.out.print("Enter your new status: ");
            String currentStatus = scanner.nextLine();
            // Check if the status is valid
            if (currentStatus.equalsIgnoreCase("Online") ||
                    currentStatus.equalsIgnoreCase("Idle") ||
                    currentStatus.equalsIgnoreCase("Do Not Disturb") ||
                    currentStatus.equalsIgnoreCase("Invisible")) {
                validStatus = true;
                // Send the message to ChangeStatusActor
                changeStatusActor.tell(new ChangeStatusActor.ChangeStatusMessage(true, currentUsername, "Status changed to: ", currentStatus), getSelf());
                profileActor.tell(new ProfileActor.UpdateStatusMessage(currentUsername, currentStatus), getSelf());
            } else {
                System.out.println("Invalid status! Please choose from: Online, Idle, Do Not Disturb, Invisible.");
            }
        }
    }
}
