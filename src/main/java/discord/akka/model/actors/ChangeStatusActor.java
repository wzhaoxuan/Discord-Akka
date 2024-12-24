package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

// Actor for handling "Change Status" functionality
public class ChangeStatusActor extends AbstractActor {
    public static Props props() {
        return Props.create(ChangeStatusActor.class);
    }


    public static class ChangeStatusMessage {
        public final boolean success;
        public final String username;
        public final String message;
        public final String newStatus;

        public ChangeStatusMessage(boolean success, String username, String message, String newStatus) {
            this.success = success;
            this.username = username;
            this.message = message;
            this.newStatus = newStatus;
        }
    }

    private String currentStatus = "Online"; // Default status

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChangeStatusMessage.class, statusMsg -> {
                    currentStatus = statusMsg.newStatus;
                    getSender().tell(new ChangeStatusMessage(true, statusMsg.username, "Status changed to: " + currentStatus, statusMsg.newStatus), getSelf());
                })
                .build();
    }
}