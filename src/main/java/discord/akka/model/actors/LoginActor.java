package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

// Actor for handling login functionality
public class LoginActor extends AbstractActor {
    private final Map<String, String> userCredentials = new HashMap<>();

    public static Props props() {
        return Props.create(LoginActor.class);
    }

    // Messages
    public static class LoginMessage {
        public final String username;
        public final String password;

        public LoginMessage(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class SignupMessage {
        public final String username;
        public final String password;

        public SignupMessage(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class ResponseMessage {
        public final boolean success;
        public final String message;

        public ResponseMessage(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LoginMessage.class, login -> {
                    if (userCredentials.containsKey(login.username)) {
                        if (userCredentials.get(login.username).equals(login.password)) {
                            getSender().tell(new ResponseMessage(true, "Login Successful!"), getSelf());
                        } else {
                            getSender().tell(new ResponseMessage(false, "Incorrect password. Please try again."), getSelf());
                        }
                    } else {
                        getSender().tell(new ResponseMessage(false, "Username not found. Please sign up."), getSelf());
                    }
                })
                .match(SignupMessage.class, signup -> {
                    if (userCredentials.containsKey(signup.username)) {
                        getSender().tell(new ResponseMessage(false, "Username already exists. Please choose another."), getSelf());
                    } else {
                        userCredentials.put(signup.username, signup.password);
                        getSender().tell(new ResponseMessage(true, "Signup Successful! You can now log in."), getSelf());
                    }
                })
                .build();
    }
}
