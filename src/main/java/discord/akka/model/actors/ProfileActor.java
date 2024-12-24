package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProfileActor extends AbstractActor {
    private final Scanner scanner = new Scanner(System.in);
    private String currentUserName; // Store the current logged-in username

    // User Profile Class
    public static class Profile {
        public String username;
        public String pronouns;
        public String aboutMe;
        public String avatar;
        public String background;
        public String status; // New status field

        public Profile(String username, String pronouns, String aboutMe, String avatar, String background, String status) {
            this.username = username;
            this.pronouns = pronouns;
            this.aboutMe = aboutMe;
            this.avatar = avatar;
            this.background = background;
            this.status = status; // Initialize status
        }

        @Override
        public String toString() {
            return "--Profile--" +
                    "\nusername=" + username + '\'' +
                    "\npronouns='" + pronouns + '\'' +
                    "\naboutMe='" + aboutMe + '\'' +
                    "\navatar='" + avatar + '\'' +
                    "\nbackground='" + background + '\'' +
                    "\nstatus='" + status + '\''; // Include status in output
        }
    }

    // Map to store user profiles
    private final Map<String, Profile> userProfiles = new HashMap<>();

    // Message class for updating profile fields
    public static class UpdateProfileMessage {
        public final String userName;
        public final String pronouns;
        public final String aboutMe;
        public final String avatar;
        public final String background;

        public UpdateProfileMessage(String userName, String pronouns, String aboutMe, String avatar, String background) {
            this.userName = userName;
            this.pronouns = pronouns;
            this.aboutMe = aboutMe;
            this.avatar = avatar;
            this.background = background;
        }
    }

    // Message class for confirming profile changes
    public static class ProfileResponse {
        public final boolean success;
        public final String message;
        public final String username;
        public final Profile profile;

        public ProfileResponse(boolean success, String message, String username, Profile profile) {
            this.success = success;
            this.message = message;
            this.username = username;
            this.profile = profile;
        }
    }

    public static class UpdateStatusMessage {
        public final String userName;
        public final String newStatus;

        public UpdateStatusMessage(String userName, String newStatus) {
            this.userName = userName;
            this.newStatus = newStatus;
        }
    }

    public static class GetProfileMessage {
        public final String userName;

        public GetProfileMessage(String userName) {
            this.userName = userName;
        }
    }

    // Props method to create the ProfileActor
    public static Props props() {
        return Props.create(ProfileActor.class, ProfileActor::new);
    }

    private Profile getOrCreateProfile(String userName) {
        Profile profile = userProfiles.computeIfAbsent(userName, n -> new Profile(n, "", "", "", "", "Online"));
        // Save the newly created profile
        return profile;
    }

    // Method to update a user's profile
    private Profile updateProfile(String currentUserName) {
        System.out.println("\n--- Update your profile details ---");

        System.out.print("Enter your new pronouns: ");
        String pronouns = scanner.nextLine();

        System.out.print("Enter your 'About Me' text: ");
        String aboutMe = scanner.nextLine();

        System.out.print("Enter your avatar URL: ");
        String avatar = scanner.nextLine();

        System.out.print("Enter your background URL: ");
        String background = scanner.nextLine();

        // Always use userName (currentUserName) as the key
        Profile currentProfile = userProfiles.get(currentUserName);
        if (currentProfile == null) {
            // Create a new profile if not exists
            currentProfile = new Profile(currentUserName, pronouns, aboutMe, avatar, background, "Online");
        } else {
            // Update the existing profile fields
            currentProfile.pronouns = pronouns;
            currentProfile.aboutMe = aboutMe;
            currentProfile.avatar = avatar;
            currentProfile.background = background;
        }

        // Save the updated profile with the correct key
        userProfiles.put(currentUserName, currentProfile);
        return currentProfile;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // When the GetProfileMessage is received, show the logged-in user's profile
                .match(GetProfileMessage.class, msg -> {
                    Profile profile = getOrCreateProfile(msg.userName);
                    this.currentUserName = msg.userName;
                    if (currentUserName != null && currentUserName.equals(msg.userName)) {
                        getSender().tell(profile, getSelf());
                    } else {
                        getSender().tell("No profile found for the logged-in user: " + msg.userName, getSelf());
                    }

                })

                // Handle the message to update a user's profile
                .match(UpdateProfileMessage.class, msg -> {
                    this.currentUserName = msg.userName;
                    if (currentUserName == null) {
                        getSender().tell("No user logged in. Please login first.", getSelf());
                        return;
                    }

                    Profile updatedProfile = updateProfile(currentUserName);
                    userProfiles.put(currentUserName, updatedProfile); // Ensure profile is stored

                    getSender().tell(new ProfileResponse(true, "Profile updated successfully!", currentUserName, updatedProfile), getSelf());
                    System.out.println(updatedProfile);
                })
                .match(UpdateStatusMessage.class, msg -> {
                    this.currentUserName = msg.userName;
                    // Handle the status update message
                    if (currentUserName != null) {
                        Profile profile = userProfiles.get(currentUserName);
                        if (profile != null) {
                            profile.status = msg.newStatus; // Update the status
                            userProfiles.put(currentUserName, profile);
                        }
                    }
                })
                .build();
    }
}
