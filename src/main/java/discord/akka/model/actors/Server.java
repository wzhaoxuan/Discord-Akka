package discord.akka.model.actors;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private String serverName;
    private List<User> members;

    public Server(String serverName) {
        this.serverName = serverName;
        this.members = new ArrayList<>();
    }

    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            System.out.println(user.getUsername() + " joined the server: " + serverName);
        } else {
            System.out.println(user.getUsername() + " is already a member of this server.");
        }
    }

    public List<User> getMembers() {
        return members;
    }

    public String getServerName() {
        return serverName;
    }
}
