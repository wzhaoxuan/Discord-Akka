package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import java.util.Scanner;

public class PaymentActor extends AbstractActor {
    private final Scanner scanner = new Scanner(System.in);

    public static Props props() {
        return Props.create(PaymentActor.class);
    }

    // Message classes
    public static class ProcessPaymentMessage {
        public final String username;
        public final String plan;
        public final double amount;
        public final String status;

        public ProcessPaymentMessage(String username, String plan, double amount, String status) {
            this.username = username;
            this.plan = plan;
            this.amount = amount;
            this.status = status;
        }
    }

    public static class PaymentResponse {
        public final boolean success;
        public final String message;
        public final String username;
        public final String plan;
        public final String status;

        public PaymentResponse(boolean success, String message, String username, String plan, String status) {
            this.success = success;
            this.message = message;
            this.username = username;
            this.plan = plan;
            this.status = status;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessPaymentMessage.class, this::handlePayment)
                .build();
    }

    private void handlePayment(ProcessPaymentMessage msg) {
        System.out.println("\n=== Payment Processing for " + msg.plan + " ===");
        System.out.println("Amount to pay: RM" + msg.amount);

        // Payment method selection
        System.out.println("\nSelect payment method:");
        System.out.println("1. Bank Transfer");
        System.out.println("2. E-Wallet");

        int paymentMethod = getValidInput("Enter your choice (1-2): ", 1, 2);

        // Get payment details based on method
        if (paymentMethod == 1) {
            processBankTransfer();
        } else {
            processEWallet();
        }

        // Simulate payment authorization
        System.out.print("\nPress Enter to authorize the transaction...");
        scanner.nextLine();

        // Send success response
        getSender().tell(
                new PaymentResponse(true,
                        "Payment successful! Your " + msg.plan + " subscription is now active.",
                        msg.username,
                        msg.plan,
                        msg.status),
                getSelf()
        );
    }

    private void processBankTransfer() {
        System.out.println("\n=== Bank Transfer Details ===");
        System.out.print("Enter bank name: ");
        scanner.nextLine();
        System.out.print("Enter account number: ");
        scanner.nextLine();
        System.out.print("Enter account holder name: ");
        scanner.nextLine();
    }

    private void processEWallet() {
        System.out.println("\n=== E-Wallet Details ===");
        System.out.print("Enter E-Wallet provider (e.g., GrabPay, Touch 'n Go): ");
        scanner.nextLine();
        System.out.print("Enter phone number linked to E-Wallet: ");
        scanner.nextLine();
    }

    private int getValidInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}