package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class FriendActor extends AbstractActor {

    public static Props props() {
        return Props.create(FriendActor.class, FriendActor::new);
    }

    public static class AddFriendMessage {
        public final String currentUser;
        public final String friendUser;

        public AddFriendMessage(String currentUser, String friendUser) {
            this.currentUser = currentUser;
            this.friendUser = friendUser;
        }
    }

    public static class FriendResponse {
        public final String username;
        public final String message;

        public FriendResponse(String username, String message) {
            this.username = username;
            this.message = message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddFriendMessage.class, msg -> {
                    // Simulate adding a friend
                    System.out.println("Adding friend: " + msg.friendUser);
                    getSender().tell(new FriendResponse(msg.currentUser, "Friend " + msg.friendUser + " added successfully!"), getSelf());
                })
                .build();
    }
}
