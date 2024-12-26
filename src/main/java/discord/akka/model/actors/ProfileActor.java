package discord.akka.model.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProfileActor extends AbstractActor {
    private final Scanner scanner = new Scanner(System.in);
    private String currentUserName;

    public static class Profile {
        public String username;
        public String pronouns;
        public String aboutMe;
        public String avatar;
        public String background;
        public String status;
        public String premiumPlan;

        public Profile(String username, String pronouns, String aboutMe, String avatar, String background, String status, String premiumPlan) {
            this.username = username;
            this.pronouns = pronouns;
            this.aboutMe = aboutMe;
            this.avatar = avatar;
            this.background = background;
            this.status = status;
            this.premiumPlan = premiumPlan;
        }

        @Override
        public String toString() {
            return "--Profile--" +
                    "\nusername = " + username +
                    "\npronouns = " + pronouns +
                    "\naboutMe = " + aboutMe +
                    "\navatar = " + avatar +
                    "\nbackground = " + background +
                    "\nstatus = " + status +
                    "\npremiumPlan = " + premiumPlan;
        }
    }

    private final Map<String, Profile> userProfiles = new HashMap<>();

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

    public static Props props() {
        return Props.create(ProfileActor.class, ProfileActor::new);
    }

    private Profile getOrCreateProfile(String userName) {
        return userProfiles.computeIfAbsent(userName, n -> new Profile(n, "", "", "", "", "Online", "None"));
    }

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

        Profile currentProfile = userProfiles.get(currentUserName);
        String currentPremiumPlan = (currentProfile != null) ? currentProfile.premiumPlan : "None";
        String currentStatus = (currentProfile != null) ? currentProfile.status : "Online";

        Profile updatedProfile = new Profile(currentUserName, pronouns, aboutMe, avatar, background, currentStatus, currentPremiumPlan);
        userProfiles.put(currentUserName, updatedProfile);
        return updatedProfile;
    }

    private void updatePremiumPlan(String userName, String premiumPlan) {
        Profile profile = userProfiles.get(userName);
        if (profile != null) {
            System.out.println("Updating premium plan for user: " + userName + " to plan: " + premiumPlan);
            Profile updatedProfile = new Profile(
                    profile.username,
                    profile.pronouns,
                    profile.aboutMe,
                    profile.avatar,
                    profile.background,
                    profile.status,
                    premiumPlan
            );
            userProfiles.put(userName, updatedProfile);
            System.out.println("Successfully updated premium plan for user: " + userName);
        } else {
            Profile newProfile = new Profile(userName, "", "", "", "", "Online", premiumPlan);
            userProfiles.put(userName, newProfile);
            System.out.println("Created new profile with premium plan for user: " + userName);
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetProfileMessage.class, msg -> {
                    Profile profile = getOrCreateProfile(msg.userName);
                    this.currentUserName = msg.userName;
                    if (currentUserName != null && currentUserName.equals(msg.userName)) {
                        getSender().tell(profile, getSelf());
                    } else {
                        getSender().tell("No profile found for the logged-in user: " + msg.userName, getSelf());
                    }
                })
                .match(UpdateProfileMessage.class, msg -> {
                    this.currentUserName = msg.userName;
                    if (currentUserName == null) {
                        getSender().tell("No user logged in. Please login first.", getSelf());
                        return;
                    }

                    Profile updatedProfile = updateProfile(currentUserName);
                    userProfiles.put(currentUserName, updatedProfile);

                    getSender().tell(new ProfileResponse(true, "Profile updated successfully!", currentUserName, updatedProfile), getSelf());
                    System.out.println(updatedProfile);
                })
                .match(UpdateStatusMessage.class, msg -> {
                    this.currentUserName = msg.userName;
                    if (currentUserName != null) {
                        Profile profile = userProfiles.get(currentUserName);
                        if (profile != null) {
                            Profile updatedProfile = new Profile(
                                    profile.username,
                                    profile.pronouns,
                                    profile.aboutMe,
                                    profile.avatar,
                                    profile.background,
                                    msg.newStatus,
                                    profile.premiumPlan
                            );
                            userProfiles.put(currentUserName, updatedProfile);
                        }
                    }
                })
                .match(PremiumActor.SubscriptionResponse.class, msg -> {
                    System.out.println("Received subscription update for user: " + msg.username);
                    updatePremiumPlan(msg.username, msg.plan);
                })
                .build();
    }
}