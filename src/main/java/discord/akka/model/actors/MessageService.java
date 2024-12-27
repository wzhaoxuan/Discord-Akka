package discord.akka.model.actors;


public class MessageService {
    public void sendMessage(User sender, User receiver, String message) {
        System.out.println(sender.getUsername() + " sent to " + receiver.getUsername() + ": " + message);
    }
}
