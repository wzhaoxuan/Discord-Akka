package discord.akka.model.actors;

public class VideoCallService {
    public void startVideoCall(User user1, User user2) {
        System.out.println("Starting a video call between " + user1.getUsername() + " and " + user2.getUsername() + ".");
    }
}
