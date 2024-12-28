package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.List;

public class FriendActor extends AbstractActor {
    private final List<String> friends = new ArrayList<>();

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
        public final List<String> updatedFriendList; // Added field

        public FriendResponse(String username, String message, String userStatus, List<String> updatedFriendList) {
            this.username = username;
            this.message = message;
            this.userStatus = userStatus;
            this.updatedFriendList = updatedFriendList; // Store updated friend list
        }
    }

    public static class GetFriendsListMessage {
        public final String currentUser;

        public GetFriendsListMessage(String currentUser) {
            this.currentUser = currentUser;
        }
    }

    public static class FriendsListResponse {
        public final String username;
        public final List<String> friends;

        public FriendsListResponse(String username, List<String> friends) {
            this.username = username;
            this.friends = friends;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddFriendMessage.class, msg -> {
                    if (!friends.contains(msg.friendUser)) {
                        friends.add(msg.friendUser);
                        getSender().tell(
                                new FriendResponse(
                                        msg.currentUser,
                                        "Friend " + msg.friendUser + " added successfully!",
                                        msg.userStatus,
                                        new ArrayList<>(friends) // Send updated friend list
                                ),
                                getSelf()
                        );
                    } else {
                        getSender().tell(
                                new FriendResponse(
                                        msg.currentUser,
                                        "Friend " + msg.friendUser + " is already in your list!",
                                        msg.userStatus,
                                        new ArrayList<>(friends) // Send friend list
                                ),
                                getSelf()
                        );
                    }
                })
                .match(GetFriendsListMessage.class, msg -> {
                    getSender().tell(
                            new FriendsListResponse(
                                    msg.currentUser,
                                    new ArrayList<>(friends) // Send current friend list
                            ),
                            getSelf()
                    );
                })
                .build();
    }

}
