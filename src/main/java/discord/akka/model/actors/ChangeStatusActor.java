//ChangeStatusActor.java

package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

// Actor for handling "Change Status" functionality
public class ChangeStatusActor extends AbstractActor {
    public static Props props() {
        return Props.create(ChangeStatusActor.class);
    }

    // Messages for this actor
    public static class ChangeStatusMessage {
        public final String newStatus;
        public ChangeStatusMessage(String newStatus) {
            this.newStatus = newStatus;
        }
    }

    public static class StatusResponse {
        public final boolean success;
        public final String message;

        public StatusResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    private String currentStatus = "Online"; // Default status

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ChangeStatusMessage.class, statusMsg -> {
                    currentStatus = statusMsg.newStatus;
                    getSender().tell(new StatusResponse(true, "Status changed to: " + currentStatus), getSelf());
                })
                .build();
    }
}