package discord.akka.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.AbstractActor;
import akka.actor.Props;
import discord.akka.model.actors.LoginActor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Create ActorSystem
        ActorSystem system = ActorSystem.create("LoginSystem");

        // Create LoginActor
        ActorRef loginActor = system.actorOf(LoginActor.props(), "loginActor");

        // Create a controller actor to handle user interaction
        ActorRef controllerActor = system.actorOf(ControllerActor.props(loginActor), "controllerActor");

        // Start the application loop
        controllerActor.tell(new ControllerActor.StartInteraction(), ActorRef.noSender());
    }

    public static class ControllerActor extends AbstractActor {
        private final ActorRef loginActor;
        private final Scanner scanner = new Scanner(System.in);

        public ControllerActor(ActorRef loginActor) {
            this.loginActor = loginActor;
        }

        public static Props props(ActorRef loginActor) {
            return Props.create(ControllerActor.class, () -> new ControllerActor(loginActor));
        }

        // Message to start the interaction
        public static class StartInteraction {
        }

        private void displayMenu() {
            System.out.println("\nWelcome to Discord Application!");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(StartInteraction.class, msg -> {
                        displayMenu();
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

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
                        System.out.println(response.message);

                        // Restart the interaction
                        self().tell(new StartInteraction(), getSelf());
                    })
                    .build();
        }
    }

}
