package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class ServerActor extends AbstractActor {

    public static Props props() {
        return Props.create(ServerActor.class);
    }

    public static class CreateServerMessage {
        public final String owner;
        public final String serverName;
        public final String serverDescription;

        public CreateServerMessage(String owner, String serverName, String serverDescription) {
            this.owner = owner;
            this.serverName = serverName;
            this.serverDescription = serverDescription;
        }
    }

    public static class ServerResponse {
        public final String username;
        public final String message;

        public ServerResponse(String username, String message) {
            this.username = username;
            this.message = message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateServerMessage.class, msg -> {
                    System.out.println("Server created by " + msg.owner + ": " + msg.serverName);
                    getSender().tell(new ServerResponse(msg.owner, "Server '" + msg.serverName + "' created successfully!"), getSelf());
                })
                .build();
    }
}
