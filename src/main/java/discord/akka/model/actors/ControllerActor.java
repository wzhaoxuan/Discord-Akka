package discord.akka.model.actors;
import akka.actor.ActorRef;
import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.InputMismatchException;
import java.util.Scanner;
public class ControllerActor extends AbstractActor {
    private final ActorRef loginActor;
    private final Scanner scanner = new Scanner(System.in);

    public ControllerActor(ActorRef loginActor) {
        this.loginActor = loginActor;
    }

    public static Props props(ActorRef loginActor) {
        return Props.create(ControllerActor.class, () -> new ControllerActor(loginActor));
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
        System.out.println("\n=== User: " + currentUsername + " | Status: " + currentStatus + " ===");
        System.out.println("Select a functionality:");
        System.out.println("1. Create Profile");
        System.out.println("2. Change Status");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ControllerActor.StartInteraction.class, msg -> {
                    displayMenu();
                    int choice = 0;
                    try{
                        choice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                    } catch(InputMismatchException e){
                        System.out.println("Please enter an Integer");
                        scanner.nextLine();
                        self().tell(new ControllerActor.StartInteraction(), getSelf());
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
                        self().tell(new ControllerActor.StartInteraction(), getSelf()); // Restart the interaction
                    }
                })
                .match(LoginActor.ResponseMessage.class, response -> {
                    // Handle the response from LoginActor
                    if(!response.success){
                        System.out.println(response.message);
                        // Restart the interaction
                        self().tell(new ControllerActor.StartInteraction(), getSelf());
                    }
                    else{
                        System.out.println(response.message);
                        loginSuccessful(response.username, response.status);
                    }
                })
                .build();
    }
}
