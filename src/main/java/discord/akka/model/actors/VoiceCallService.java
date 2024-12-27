package discord.akka.model.actors;

public class VoiceCallService {
    public void startVoiceCall(User user1, User user2) {
        System.out.println("Starting a voice call between " + user1.getUsername() + " and " + user2.getUsername() + ".");
    }
}
