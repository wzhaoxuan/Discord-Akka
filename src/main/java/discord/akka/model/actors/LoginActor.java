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
    private static final String DEFAULT_USER = "User";
    private static final String DEFAULT_PASSWORD = "password123";
    private static final String DEFAULT_STATUS = "Online";

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
        public final boolean isSignup;
        public final String message;
        public final String username;
        public final String status;


        public ResponseMessage(boolean success, boolean isSignup, String message, String username, String status) {
            this.success = success;
            this.isSignup = isSignup;
            this.message = message;
            this.username = username;
            this.status = status;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(LoginMessage.class, login -> {
                    if (userCredentials.containsKey(login.username)) {
                        if (userCredentials.get(login.username).equals(login.password)) {
                            getSender().tell(new ResponseMessage(true, false, "Login Successful!", login.username, DEFAULT_STATUS), getSelf());
                        } else {
                            getSender().tell(new ResponseMessage(false, false, "Incorrect password. Please try again.", null, null), getSelf());
                        }
                    } else {
                        getSender().tell(new ResponseMessage(false, true,"Username not found. Please sign up.", null, null), getSelf());
                    }
                    for(String entry : userCredentials.keySet()) {
                        System.out.println(entry);
                    }
                })
                .match(SignupMessage.class, signup -> {
                    System.out.println("Current users in Discord");
                    if (userCredentials.containsKey(signup.username)) {
                        getSender().tell(new ResponseMessage(false, false,"Username already exists. Please choose another.", null, null), getSelf());
                    } else {
                        userCredentials.put(signup.username, signup.password);
                        saveUserCredentials();
                        getSender().tell(new ResponseMessage(true, true,"Signup Successful! You can now log in.", signup.username, DEFAULT_STATUS), getSelf());
                    }
                    for(String entry : userCredentials.keySet()) {
                        System.out.println(entry);
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

    @Override
    public void preStart() throws Exception {
        // Load existing credentials from the file
        loadCredentialsFromFile();

        // Add a default user (if none exists)
        if (userCredentials.isEmpty()) {
            userCredentials.put(DEFAULT_USER, DEFAULT_PASSWORD);
        }
    }
}
