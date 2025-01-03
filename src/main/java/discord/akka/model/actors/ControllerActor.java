package discord.akka.model.actors;

import akka.actor.ActorRef;
import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ControllerActor extends AbstractActor {
    private final ActorRef loginActor;
    private final ActorRef changeStatusActor;
    private final ActorRef profileActor;
    private final ActorRef premiumActor;


    private final ActorRef friendActor; // Add FriendActor
    private final ActorRef serverActor;
    private final ActorRef callActor;
    private final ActorRef messageActor;


    private final Scanner scanner = new Scanner(System.in);

    public ControllerActor(ActorRef loginActor, ActorRef profileActor, ActorRef changeStatusActor,
                           ActorRef premiumActor, ActorRef friendActor, ActorRef serverActor,
                           ActorRef callActor, ActorRef messageActor) {
        this.loginActor = loginActor;
        this.profileActor = profileActor;
        this.changeStatusActor = changeStatusActor;
        this.premiumActor = premiumActor;
        this.friendActor = friendActor;
        this.serverActor = serverActor;
        this.callActor = callActor;
        this.messageActor = messageActor;
    }

    public static Props props(ActorRef loginActor, ActorRef profileActor, ActorRef changeStatusActor, ActorRef premiumActor, ActorRef friendActor, ActorRef serverActor,
                              ActorRef callActor, ActorRef messageActor) {
        return Props.create(ControllerActor.class, () -> new ControllerActor(loginActor, profileActor, changeStatusActor, premiumActor, friendActor, serverActor, callActor, messageActor));
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
        System.out.println("\n=== User: " + currentUsername + " | Status: " + currentStatus + " ===");
        System.out.println("Select a functionality:");
        System.out.println("1. Edit Profile");
        System.out.println("2. View Profile");
        System.out.println("3. Add Friend");
        System.out.println("4. Create Server"); // New
        System.out.println("5. Voice/Video Call"); // New
        System.out.println("6. Message Someone"); // New
        System.out.println("7. Get Premium Profile");
        System.out.println("8. Back to Main Menu");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    profileActor.tell(new ProfileActor.UpdateProfileMessage(currentUsername, "", "", "", ""), getSelf());
                    break;
                case 2:
                    profileActor.tell(new ProfileActor.GetProfileMessage(currentUsername), getSelf());
                    break;
                case 3:
                    friendActor.tell(new FriendActor.AddFriendMessage(currentUsername, FriendActor.addFriend(), currentStatus),getSelf());
                    break;
                case 4:
                    createServer(currentUsername, currentStatus); // New
                    break;
                case 5:
                    callActor.tell(new CallActor.InitiateCallMessage(currentUsername, currentStatus), getSelf());
                    break;
                case 6:
                    messageActor.tell(new MessageActor.MessageInteraction(currentUsername, currentStatus), getSelf());
                    break;
                case 7:
                    premiumActor.tell(new PremiumActor.GetPremiumOptions(currentUsername, currentStatus), getSelf());
                    break;
                case 8:
                    self().tell(new StartInteraction(), getSelf());
                    break;
                default:
                    System.out.println("Invalid option! Try again.");
                    loginSuccessful(currentUsername, currentStatus);
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            loginSuccessful(currentUsername, currentStatus);
        }
    }







    private void createServer(String currentUsername, String currentStatus) {
        System.out.print("Enter a name for your server: ");
        String serverName = scanner.nextLine();
        System.out.print("Enter a description for your server: ");
        String serverDescription = scanner.nextLine();

        // Create the server
        serverActor.tell(new ServerActor.CreateServerMessage(currentUsername, serverName, serverDescription, currentStatus), getSelf());

        // Prompt user to add friends to the server
        System.out.println("\nWould you like to add friends to your server? (yes/no)");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            System.out.println("Fetching your friends list...");
            friendActor.tell(new FriendActor.GetFriendsListMessage(currentUsername), getSelf());

            // Handle the response from FriendActor
            context().become(receiveBuilder()
                    .match(FriendActor.FriendsListResponse.class, response -> {
                        if (response.friends.isEmpty()) {
                            System.out.println("You have no friends to add.");
                        } else {
                            System.out.println("\nYour Friends:");
                            for (int i = 0; i < response.friends.size(); i++) {
                                System.out.println((i + 1) + ". " + response.friends.get(i));
                            }

                            System.out.println("Enter the index numbers of the friends to add to the server, separated by commas:[Eg: 1,3]");
                            String[] selections = scanner.nextLine().split(",");
                            List<String> friendsToAdd = new ArrayList<>();
                            for (String selection : selections) {
                                try {
                                    int index = Integer.parseInt(selection.trim()) - 1;
                                    if (index >= 0 && index < response.friends.size()) {
                                        friendsToAdd.add(response.friends.get(index));
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input, skipping selection: " + selection);
                                }
                            }

                            serverActor.tell(new ServerActor.AddFriendsToServerMessage(serverName, friendsToAdd, currentUsername), getSelf());
                        }


                        context().unbecome();
                    })
                    .matchAny(message -> {
                    })
                    .build().onMessage(), false);
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
                        loginSuccessful(profileResponse.username, profileResponse.profile.status);
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
                                        response.status,
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
                        loginSuccessful(response.username, response.status);
                    } else {
                        System.out.println("Payment failed: " + response.message);
                        loginSuccessful(response.username, response.status);
                    }
                })





                .match(FriendActor.FriendResponse.class, response -> {
                    System.out.println(response.message); // Print the response message
                    System.out.println("\n=== Updated Friends List ===");
                    if (response.updatedFriendList.isEmpty()) {
                        System.out.println("You have no friends added.");
                    } else {
                        response.updatedFriendList.forEach(friend -> System.out.println("- " + friend));
                    }
                    loginSuccessful(response.username, response.userStatus);
                })

                .match(ServerActor.ServerResponse.class, response -> {
                    System.out.println(response.message); // Print response message
                    loginSuccessful(response.username, response.status);
                })



                .match(CallActor.CallDetails.class, details -> {
                    // Log or display the call details
                    System.out.println("Call initiated with " + details.recipient);
                    System.out.println("Camera: " + (details.cameraOn ? "On" : "Off"));
                    System.out.println("Microphone: " + (details.micOn ? "On" : "Off"));

                    // Use the currentUsername and currentStatus from the details object
                    loginSuccessful(details.currentUsername, details.currentStatus);
                })




                .match(MessageActor.MessageResponse.class, response -> {
                    System.out.println(response.message);
                    loginSuccessful(response.sender, response.status); // Return to menu
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