package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.Scanner;

public class FriendActor extends AbstractActor {

    public static Props props() {
        return Props.create(FriendActor.class, FriendActor::new);
    }

    public static class AddFriendMessage {
        public final String currentUser;
        public final String friendUser;
        public final String userStatus;

        public AddFriendMessage(String currentUser, String friendUser, String userStatus) {
            this.currentUser = currentUser;
            this.friendUser = friendUser;
            this.userStatus = userStatus;
        }
    }

    public static class FriendResponse {
        public final String username;
        public final String message;
        public final String userStatus;


        public FriendResponse(String username, String message, String userStatus) {
            this.username = username;
            this.message = message;
            this.userStatus = userStatus;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddFriendMessage.class, msg -> {
//                    addFriend();
                    // Simulate adding a friend
                    System.out.println("Adding friend: " + msg.friendUser);
                    getSender().tell(new FriendResponse(msg.currentUser, "Friend " + msg.friendUser + " added successfully!", msg.userStatus), getSelf());
                })
                .build();
    }



}
