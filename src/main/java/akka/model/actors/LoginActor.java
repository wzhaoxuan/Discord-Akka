//LoginActor.java
package akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

// Actor for handling login functionality
public class LoginActor extends AbstractActor {
    public static Props props() {
        return Props.create(LoginActor.class);
    }

    // Predefined credentials
    private static final String VALID_USERNAME = "abc123";
    private static final String VALID_PASSWORD = "12345";

    // Messages for this actor
    public static class LoginMessage {
        public final String username;
        public final String password;

        public LoginMessage(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class LoginResponse {
        public final boolean success;
        public final String message;

        public LoginResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LoginMessage.class, login -> {
                    // Check predefined credentials
                    if (VALID_USERNAME.equals(login.username) && VALID_PASSWORD.equals(login.password)) {
                        getSender().tell(new LoginResponse(true, "Login Successful!"), getSelf());
                    } else {
                        getSender().tell(new LoginResponse(false, "Invalid credentials. Please try again."), getSelf());
                    }
                })
                .build();
    }
}