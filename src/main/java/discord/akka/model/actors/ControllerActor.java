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
    private final ActorRef premiumActor;
    private final Scanner scanner = new Scanner(System.in);

    public ControllerActor(ActorRef loginActor, ActorRef profileActor, ActorRef changeStatusActor, ActorRef premiumActor) {
        this.loginActor = loginActor;
        this.profileActor = profileActor;
        this.changeStatusActor = changeStatusActor;
        this.premiumActor = premiumActor;
    }

    public static Props props(ActorRef loginActor, ActorRef profileActor, ActorRef changeStatusActor, ActorRef premiumActor) {
        return Props.create(ControllerActor.class, () -> new ControllerActor(loginActor, profileActor, changeStatusActor, premiumActor));
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
        System.out.println("3. Get Premium Profile");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            loginSuccessful(currentUsername, currentStatus);
            return;
        }

        switch (choice) {
            case 1:
                profileActor.tell(new ProfileActor.UpdateProfileMessage(currentUsername, "", "", "", ""), getSelf());
                break;
            case 2:
                profileActor.tell(new ProfileActor.GetProfileMessage(currentUsername), getSelf());
                break;
            case 3:
                premiumActor.tell(new PremiumActor.GetPremiumOptions(currentUsername, currentStatus), getSelf());
                break;
            case 4:
                self().tell(new StartInteraction(), getSelf());
                break;
            default:
                System.out.println("Invalid option! Try again.");
                loginSuccessful(currentUsername, currentStatus);
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
                        choice = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number");
                        self().tell(new StartInteraction(), getSelf());
                        return;
                    }

                    switch (choice) {
                        case 1:
                            System.out.print("Enter username: ");
                            String username = scanner.nextLine();
                            System.out.print("Enter password: ");
                            String password = scanner.nextLine();
                            loginActor.tell(new LoginActor.LoginMessage(username, password), getSelf());
                            break;
                        case 2:
                            System.out.print("Choose a username: ");
                            username = scanner.nextLine();
                            System.out.print("Choose a password: ");
                            password = scanner.nextLine();
                            loginActor.tell(new LoginActor.SignupMessage(username, password), getSelf());
                            break;
                        case 3:
                            System.out.println("Exiting. Goodbye!");
                            getContext().getSystem().terminate();
                            break;
                        default:
                            System.out.println("Invalid option. Try again.");
                            self().tell(new StartInteraction(), getSelf());
                            break;
                    }
                })
                .match(LoginActor.ResponseMessage.class, response -> {
                    System.out.println(response.message);
                    if (response.isSignup || !response.success) {
                        self().tell(new StartInteraction(), getSelf());
                    } else {
                        loginSuccessful(response.username, response.status);
                    }
                })
                .match(ProfileActor.ProfileResponse.class, profileResponse -> {
                    if (profileResponse.success) {
                        System.out.println(profileResponse.message);
                        changeUserStatus(profileResponse.username);
                    } else {
                        System.out.println("Profile update failed: " + profileResponse.message);
                        loginSuccessful(profileResponse.username, "Online");
                    }
                })
                .match(ProfileActor.Profile.class, profile -> {
                    System.out.println("\n=== View Profile ===");
                    System.out.println(profile);
                    loginSuccessful(profile.username, profile.status);
                })
                .match(ChangeStatusActor.ChangeStatusMessage.class, statusResponse -> {
                    if (statusResponse.success) {
                        System.out.println(statusResponse.message);
                        loginSuccessful(statusResponse.username, statusResponse.newStatus);
                    }
                })
                .match(PaymentActor.PaymentResponse.class, response -> {
                    if (response.success) {
                        System.out.println(response.message);
                        // Forward the subscription information to ProfileActor
                        profileActor.tell(
                                new PremiumActor.SubscriptionResponse(
                                        true,
                                        response.message,
                                        response.username,
                                        "Online",
                                        response.plan
                                ),
                                getSelf()
                        );
                        // Add a small delay to ensure profile update is processed
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        loginSuccessful(response.username, "Online");
                    } else {
                        System.out.println("Payment failed: " + response.message);
                        loginSuccessful(response.username, "Online");
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
            if (currentStatus.equalsIgnoreCase("Online") ||
                    currentStatus.equalsIgnoreCase("Idle") ||
                    currentStatus.equalsIgnoreCase("Do Not Disturb") ||
                    currentStatus.equalsIgnoreCase("Invisible")) {
                validStatus = true;
                changeStatusActor.tell(new ChangeStatusActor.ChangeStatusMessage(true, currentUsername, "Status changed to: ", currentStatus), getSelf());
                profileActor.tell(new ProfileActor.UpdateStatusMessage(currentUsername, currentStatus), getSelf());
            } else {
                System.out.println("Invalid status! Please choose from: Online, Idle, Do Not Disturb, Invisible.");
            }
        }
    }
}
