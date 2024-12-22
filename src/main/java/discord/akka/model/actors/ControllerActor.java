package discord.akka.model.actors;
import akka.actor.ActorRef;
import akka.actor.AbstractActor;
import akka.actor.Props;

import discord.akka.model.actors.ChangeStatusActor;

import java.util.InputMismatchException;
import java.util.Scanner;


public class ControllerActor extends AbstractActor {
    private final ActorRef loginActor;
    private  final ActorRef changeStatusActor;
    private final Scanner scanner = new Scanner(System.in);

    //Constructor
    public ControllerActor(ActorRef loginActor, ActorRef changeStatusActor) {
        this.loginActor = loginActor;
        this.changeStatusActor = changeStatusActor;
    }

    public static Props props(ActorRef loginActor, ActorRef changeStatusActor) {
        return Props.create(ControllerActor.class, () -> new ControllerActor(loginActor, changeStatusActor));
    }


    public static class StartInteraction{

    }

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
        System.out.println("1. Create Profile");
        System.out.println("2. Change Status");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");
        try {
             choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number (1 or 2).");
        }
        switch (choice) {
            case 1:
                // Handle Profile creation (if needed, can be added later)
                System.out.println("Profile creation is not implemented yet.");
                break;
            case 2:
                // Change Status
                changeUserStatus(currentUsername, currentStatus);
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

    private void changeUserStatus(String currentUsername, String currentStatus) {
        System.out.println("\nAvailable statuses: Online, Idle, Do Not Disturb, Invisible");
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
                // Send the message to ChangeStatusActor
                changeStatusActor.tell(new ChangeStatusActor.ChangeStatusMessage(true,currentUsername, "", newStatus), getSelf());
            } else {
                System.out.println("Invalid status! Please choose from: Online, Idle, Do Not Disturb, Invisible.");
            }
        }
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartInteraction.class, msg -> {
                    displayMenu();
                    int choice = 0;
                    try{
                        choice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                    } catch(InputMismatchException e){
                        System.out.println("Please enter an Integer");
                        scanner.nextLine();
                        self().tell(new StartInteraction(), getSelf());
                        return; //Skip further processing
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
                    if(!response.success){
                        System.out.println(response.message);
                        // Restart the interaction
                        self().tell(new StartInteraction(), getSelf());
                    }
                    else{
                        System.out.println(response.message);
                        loginSuccessful(response.username, response.status);
                    }
                })
                .match(ChangeStatusActor.ChangeStatusMessage.class, statusResponse ->{
                    if (statusResponse.success) {
                        System.out.println(statusResponse.message);
                        loginSuccessful(statusResponse.username, statusResponse.newStatus);
                    }
                })
                .build();
    }
}
