package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class PaymentActor extends AbstractActor {

    public static class PaymentRequest {
        public final String username;
        public final double amount;

        public PaymentRequest(String username, double amount) {
            this.username = username;
            this.amount = amount;
        }
    }

    public static class PaymentResponse {
        public final boolean success;
        public final String message;
        public final String username;

        public PaymentResponse(boolean success, String message, String username) {
            this.success = success;
            this.message = message;
            this.username = username;
        }
    }

    public static Props props() {
        return Props.create(PaymentActor.class, PaymentActor::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PaymentRequest.class, this::handlePaymentRequest)
                .build();
    }

    private void handlePaymentRequest(PaymentRequest msg) {
        // Simulate payment processing
        double requiredAmount = 31.90; // Example: Premium plan costs RM31.90/month
        String statusMessage = "";

        if (msg.amount >= requiredAmount) {
            // Payment is successful
            statusMessage = msg.username + " has successfully made a payment of RM" + msg.amount + ". You are now subscribed to Premium!";
            getSender().tell(new PaymentResponse(true, statusMessage, msg.username), getSelf());
        } else {
            // Payment failed
            statusMessage = "Payment failed. Insufficient amount. Please try again.";
            getSender().tell(new PaymentResponse(false, statusMessage, msg.username), getSelf());
        }
    }
}
