package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.Scanner;

public class CallActor extends AbstractActor {

    public static Props props() {
        return Props.create(CallActor.class);
    }

    // Message to initiate a call
    public static class InitiateCallMessage {
        public final String caller;
        public final String status;

        public InitiateCallMessage(String caller, String status) {
            this.caller = caller;
            this.status = status;
        }
    }

    // Call details message
    public static class CallDetails {
        public final String recipient;
        public final boolean cameraOn;
        public final boolean micOn;
        public final String currentUsername;
        public final String currentStatus;

        public CallDetails(String recipient, boolean cameraOn, boolean micOn, String currentUsername, String currentStatus) {
            this.recipient = recipient;
            this.cameraOn = cameraOn;
            this.micOn = micOn;
            this.currentUsername = currentUsername;
            this.currentStatus = currentStatus;
        }
    }

    // Method to initiate a call
    private void initiateCall(String currentUsername, String currentStatus) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting a voice/video call:");
        System.out.print("Enter recipient's username: ");
        String recipient = scanner.nextLine();
        System.out.print("Enable camera (yes/no)? ");
        boolean cameraOn = scanner.nextLine().equalsIgnoreCase("yes");
        System.out.print("Enable microphone (yes/no)? ");
        boolean micOn = scanner.nextLine().equalsIgnoreCase("yes");

        // Send the call details back to the sender (ControllerActor)
        getSender().tell(new CallDetails(recipient, cameraOn, micOn, currentUsername, currentStatus), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(InitiateCallMessage.class, msg -> {
                    // Pass caller and status to initiateCall
                    initiateCall(msg.caller, msg.status);
                })
                .build();
    }
}
