package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class MessageActor extends AbstractActor {

    public static Props props() {
        return Props.create(MessageActor.class);
    }

    public static class SendMessage {
        public final String sender;
        public final String recipient;
        public final String message;

        public SendMessage(String sender, String recipient, String message) {
            this.sender = sender;
            this.recipient = recipient;
            this.message = message;
        }
    }

    public static class MessageResponse {
        public final String sender;
        public final String message;

        public MessageResponse(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SendMessage.class, msg -> {
                    System.out.println("Message sent from " + msg.sender + " to " + msg.recipient + ": " + msg.message);
                    getSender().tell(new MessageResponse(msg.sender, "Message sent successfully to " + msg.recipient), getSelf());
                })
                .build();
    }
}
