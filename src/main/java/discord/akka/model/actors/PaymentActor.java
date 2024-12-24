//package discord.akka.model.actors;
//
//import akka.actor.AbstractActor;
//import akka.actor.Props;
//
//import java.util.Random;
//
//public class PaymentActor extends AbstractActor {
//
//    public static class ProcessPayment {
//        public final String username;
//        public final int planNumber;
//
//        public ProcessPayment(String username, int planNumber) {
//            this.username = username;
//            this.planNumber = planNumber;
//        }
//    }
//
//    public static class PaymentResponse {
//        public final boolean success;
//        public final String message;
//        public final String username;
//        public final String status;
//
//        public PaymentResponse(boolean success, String message, String username, String status) {
//            this.success = success;
//            this.message = message;
//            this.username = username;
//            this.status = status;
//        }
//    }
//
//    public static Props props() {
//        return Props.create(PaymentActor.class, PaymentActor::new);
//    }
//
//    @Override
//    public Receive createReceive() {
//        return receiveBuilder()
//                .match(ProcessPayment.class, this::handleProcessPayment)
//                .build();
//    }
//
//    private void handleProcessPayment(ProcessPayment msg) {
//        System.out.println("Processing payment for user: " + msg.username);
//
//        // Simulate payment success or failure with a random outcome
//        Random rand = new Random();
//        boolean paymentSuccess = rand.nextBoolean();
//
//        if (paymentSuccess) {
//            String plan = msg.planNumber == 1 ? "Premium Basic" : "Premium";
//            // Return payment response with the username and status
//            getSender().tell(new PaymentResponse(true, "Payment successful. " + msg.username + " subscribed to " + plan + ".", msg.username, "Subscribed to " + plan), getSelf());
//        } else {
//            getSender().tell(new PaymentResponse(false, "Payment failed. Please try again.", msg.username, "Payment Failed"), getSelf());
//        }
//    }
//}
