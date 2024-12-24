package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.Scanner;

public class PremiumActor extends AbstractActor {

    public static class GetPremiumOptions {
        public final String username;
        public final String status; // Include status in the GetPremiumOptions message

        public GetPremiumOptions(String username, String status) {
            this.username = username;
            this.status = status;
        }
    }

    public static class SubscribePremium {
        public final String username;
        public final int planNumber; // 1 for Premium Basic, 2 for Premium
        public final String status;

        public SubscribePremium(String username, int planNumber, String status) {
            this.username = username;
            this.planNumber = planNumber;
            this.status = status;
        }
    }

    public static class SubscriptionResponse {
        public final boolean success;
        public final String message;
        public final String username;
        public final String status;
        public final String plan;

        public SubscriptionResponse(boolean success, String message, String username, String status, String plan) {
            this.success = success;
            this.message = message;
            this.username = username;
            this.status = status;
            this.plan = plan;
        }
    }

    public static Props props() {
        return Props.create(PremiumActor.class, PremiumActor::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetPremiumOptions.class, this::handleGetPremiumOptions)
                .match(SubscribePremium.class, this::handleSubscribePremium)
                .match(SubscriptionResponse.class, this::handleSubscriptionResponse)
                .build();
    }

    // Handle the GetPremiumOptions message
    private void handleGetPremiumOptions(GetPremiumOptions msg) {
        displayPremiumPlans();
        int planNumber = getPlanNumberFromUser();
        if (planNumber != -1) {
            self().tell(new SubscribePremium(msg.username, planNumber, msg.status), getSelf());
        } else {
            System.out.println("Invalid option! Please choose 1 or 2.");
        }
    }

    // Display the available premium plans
    private void displayPremiumPlans() {
        System.out.println("Choose a premium plan:");
        System.out.println("1. Premium Basic (RM11.90/month)");
        System.out.println("   - 50MB uploads");
        System.out.println("   - Custom emoji anywhere");
        System.out.println("   - Special Nitro badge");
        System.out.println("2. Premium (RM31.90/month)");
        System.out.println("   - 500MB uploads");
        System.out.println("   - Custom emoji anywhere");
        System.out.println("   - Unlimited Super Reactions");
        System.out.println("   - HD video streaming");
        System.out.println("   - 2 Server Boosts");
        System.out.println("   - Custom profiles and more!");
    }

    // Prompt the user for a plan selection and return the plan number
    private int getPlanNumberFromUser() {
        Scanner scanner = new Scanner(System.in);
        int planNumber = -1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter your choice (1 for Premium Basic, 2 for Premium): ");
            try {
                planNumber = Integer.parseInt(scanner.nextLine());
                if (planNumber == 1 || planNumber == 2) {
                    validInput = true;
                } else {
                    System.out.println("Invalid option! Please choose 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter 1 or 2.");
            }
        }
        return planNumber;
    }

    // Handle subscription to the selected premium plan
    private void handleSubscribePremium(SubscribePremium msg) {
        String plan = determinePlan(msg.planNumber);
        if (plan != null) {
            System.out.println("Debug");
            getSender().tell(new SubscriptionResponse(true, msg.username + " has successfully subscribed to " + plan + "!", msg.username, msg.status, plan), getSelf());
        } else {
            getSender().tell(new SubscriptionResponse(false, "Invalid plan selected.", msg.username, msg.status,null), getSelf());
        }
    }

    // Determine the plan name based on the plan number
    private String determinePlan(int planNumber) {
        switch (planNumber) {
            case 1:
                return "Premium Basic";
            case 2:
                return "Premium";
            default:
                return null;
        }

    }

    // Handle the SubscriptionResponse message and provide feedback to the user
    private void handleSubscriptionResponse(SubscriptionResponse response) {
        if (response.success) {
            System.out.println(response.message);
        } else {
            System.out.println("Subscription failed: " + response.message);
        }
    }

}
