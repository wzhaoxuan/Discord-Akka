package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.Scanner;

public class MessageActor extends AbstractActor {

    private final Scanner scanner = new Scanner(System.in);

    public static Props props() {
        return Props.create(MessageActor.class);
    }


    public static class SendMessage {
        public final String sender;
        public final String recipient;
        public final String message;
        public final String status;

        public SendMessage(String sender, String recipient, String message, String status) {
            this.sender = sender;
            this.recipient = recipient;
            this.message = message;
            this.status = status;
        }
    }

    public static class MessageResponse {
        public final String sender;
        public final String message;
        public final String status;

        public MessageResponse(String sender, String message, String status) {
            this.sender = sender;
            this.message = message;
            this.status = status;
        }
    }

    public static class MessageInteraction {
        public final String currentUsername;
        public final String currentStatus;

        public MessageInteraction(String currentUsername, String currentStatus) {
            this.currentUsername = currentUsername;
            this.currentStatus = currentStatus;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SendMessage.class, msg -> {
                    System.out.println("Message sent from " + msg.sender + " to " + msg.recipient + ": " + msg.message);
                    getSender().tell(new MessageResponse(msg.sender, "Message sent successfully to " + msg.recipient, msg.status), getSelf());
                })
                .match(MessageInteraction.class, interaction -> {
                    System.out.print("Enter recipient's username: ");
                    String recipient = scanner.nextLine();
                    System.out.print("Enter your message: ");
                    String message = scanner.nextLine();
                    self().tell(new SendMessage(interaction.currentUsername, recipient, message, interaction.currentStatus), getSender());
                })
                .build();
    }
}
