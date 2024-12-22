package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// Actor for handling login functionality
public class LoginActor extends AbstractActor {
    private final Map<String, String> userCredentials = new HashMap<>();
    private static final String FILE_NAME = "discord_user.txt";

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
                    System.out.println("Current users in Discord");
                    for(String entry : userCredentials.keySet()) {
                        System.out.println(entry);
                    }
                    if (userCredentials.containsKey(signup.username)) {
                        getSender().tell(new ResponseMessage(false, "Username already exists. Please choose another."), getSelf());
                    } else {
                        userCredentials.put(signup.username, signup.password);
                        getSender().tell(new ResponseMessage(true, "Signup Successful! You can now log in."), getSelf());
                    }
                })
                .build();
    }

    private void saveUserCredentials() {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for(Map.Entry<String, String> entry : userCredentials.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch(IOException e) {
            System.err.println("Error Saving user credentials: " +e.getMessage());
        }
    }

    private void loadCredentialsFromFile() {
        File file = new File(FILE_NAME);
        // Ensure parent directories exist
        file.getParentFile().mkdirs();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        userCredentials.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading user credentials: " + e.getMessage());
            }
        }
    }
}
