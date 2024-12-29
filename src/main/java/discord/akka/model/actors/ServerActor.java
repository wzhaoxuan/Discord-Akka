package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.List;

public class ServerActor extends AbstractActor {
    private final List<String> serverMembers = new ArrayList<>();

    public static Props props() {
        return Props.create(ServerActor.class);
    }

    public static class CreateServerMessage {
        public final String owner;
        public final String serverName;
        public final String serverDescription;
        public final String status;

        public CreateServerMessage(String owner, String serverName, String serverDescription, String status) {
            this.owner = owner;
            this.serverName = serverName;
            this.serverDescription = serverDescription;
            this.status = status;
        }
    }

    public static class AddFriendsToServerMessage {
        public final String serverName;
        public final List<String> friendsToAdd;
        public final String owner;


        public AddFriendsToServerMessage(String serverName, List<String> friendsToAdd, String owner) {
            this.serverName = serverName;
            this.friendsToAdd = friendsToAdd;
            this.owner = owner;
        }
    }

    public static class ServerResponse {
        public final String username;
        public final String message;
        public final String status;

        public ServerResponse(String username, String message, String status) {
            this.username = username;
            this.message = message;
            this.status = status;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateServerMessage.class, msg -> {
                    System.out.println("Server created by " + msg.owner + ": " + msg.serverName);
                    getSender().tell(
                            new ServerResponse(msg.owner, "Server '" + msg.serverName + "' created successfully!", msg.status),
                            getSelf()
                    );
                })
                .match(AddFriendsToServerMessage.class, msg -> {
                    serverMembers.addAll(msg.friendsToAdd);
                    System.out.println("People that will be added to server '" + msg.serverName + "': " + msg.friendsToAdd);
                    getSender().tell(
                            new ServerResponse(msg.owner, "Friends added to server '" + msg.serverName + "' successfully!", "Server Updated"),
                            getSelf()
                    );
                })
                .build();
    }
}