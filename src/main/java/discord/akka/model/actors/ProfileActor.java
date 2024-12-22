package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProfileActor extends AbstractActor {
    private final Scanner scanner = new Scanner(System.in);
    // User Profile Class
    public static class Profile {
        public String displayName;
        public String pronouns;
        public String aboutMe;
        public String avatar;
        public String background;

        public Profile(String displayName, String pronouns, String aboutMe, String avatar, String background) {
            this.displayName = displayName;
            this.pronouns = pronouns;
            this.aboutMe = aboutMe;
            this.avatar = avatar;
            this.background = background;
        }

        @Override
        public String toString() {
            return "--Profile--" +
                    "\ndisplayName='" + displayName + '\'' +
                    "\npronouns='" + pronouns + '\'' +
                    "\naboutMe='" + aboutMe + '\'' +
                    "\navatar='" + avatar + '\'' +
                    "\nbackground='" + background + '\'';
        }
    }

    // Map to store user profiles
    private final Map<String, Profile> userProfiles = new HashMap<>();

    // Message class for updating profile fields
    public static class UpdateProfileMessage {
        public final String userName;
        public final String displayName;
        public final String pronouns;
        public final String aboutMe;
        public final String avatar;
        public final String background;

        public UpdateProfileMessage(String userName, String displayName, String pronouns, String aboutMe, String avatar, String background) {
            this.userName = userName;
            this.displayName = displayName;
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
        public final Profile profile;
        public final String status;

        public ProfileResponse(boolean success, String message, Profile profile, String status) {
            this.success = success;
            this.message = message;
            this.profile = profile;
            this.status = status;
        }
    }

    // Props method to create the ProfileActor
    public static Props props() {
        return Props.create(ProfileActor.class, ProfileActor::new);
    }

    // Method to update a user's profile
    private Profile updateProfile(String userName) {
        System.out.println("\n--- Update your profile details ---");

        System.out.print("Enter your new display name: ");
        String displayName = scanner.nextLine();

        System.out.print("Enter your new pronouns: ");
        String pronouns = scanner.nextLine();

        System.out.print("Enter your 'About Me' text: ");
        String aboutMe = scanner.nextLine();

        System.out.print("Enter your avatar URL: ");
        String avatar = scanner.nextLine();

        System.out.print("Enter your background URL: ");
        String background = scanner.nextLine();

        // Update the profile in the map
        Profile updatedProfile = new Profile(displayName, pronouns, aboutMe, avatar, background);
        userProfiles.put(userName, updatedProfile);
        return updatedProfile;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateProfileMessage.class, msg -> {
                    System.out.print("Enter your username: ");
                    String userName = scanner.nextLine();

                    // Prompt for profile update and display the updated profile
                    Profile updatedProfile = updateProfile(userName);

                    // Respond to sender
                    getSender().tell(new ProfileResponse(true, "Profile updated successfully!", updatedProfile, "Online"), getSelf());
                    System.out.println(updatedProfile);
                })
                .build();
    }

    // For debugging or fetching all profiles
    public Map<String, Profile> getAllProfiles() {
        return userProfiles;
    }
}
