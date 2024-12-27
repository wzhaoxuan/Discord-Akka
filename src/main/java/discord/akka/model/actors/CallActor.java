package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class CallActor extends AbstractActor {

    public static Props props() {
        return Props.create(CallActor.class);
    }

    public static class InitiateCallMessage {
        public final String caller;
        public final String recipient;
        public final boolean cameraOn;
        public final boolean micOn;

        public InitiateCallMessage(String caller, String recipient, boolean cameraOn, boolean micOn) {
            this.caller = caller;
            this.recipient = recipient;
            this.cameraOn = cameraOn;
            this.micOn = micOn;
        }
    }

    public static class CallResponse {
        public final String username;
        public final String message;

        public CallResponse(String username, String message) {
            this.username = username;
            this.message = message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(InitiateCallMessage.class, msg -> {
                    String callDetails = "Call started between " + msg.caller + " and " + msg.recipient;
                    callDetails += "\nCamera: " + (msg.cameraOn ? "On" : "Off");
                    callDetails += "\nMicrophone: " + (msg.micOn ? "On" : "Off");
                    System.out.println(callDetails);

                    getSender().tell(new CallResponse(msg.caller, "Call initiated successfully with " + msg.recipient), getSelf());
                })
                .build();
    }
}
