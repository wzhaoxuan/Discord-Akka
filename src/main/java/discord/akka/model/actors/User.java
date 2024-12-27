package discord.akka.model.actors;

import java.util.ArrayList;
import java.util.List;


public class User {
    private String username;
    private List<User> friends;

    public User(String username) {
        this.username = username;
        this.friends = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void addFriend(User friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
            System.out.println(friend.getUsername() + " added as a friend.");
        } else {
            System.out.println(friend.getUsername() + " is already your friend.");
        }
    }

    public List<User> getFriends() {
        return friends;
    }
}
