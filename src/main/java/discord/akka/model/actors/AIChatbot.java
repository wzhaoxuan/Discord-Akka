package discord.akka.model.actors;

public class AIChatbot {
    public String respond(String userInput) {

        return "AI Bot: " + (userInput.equalsIgnoreCase("Hello") ? "Hi there! How can I assist you?" : "I'm here to help!");
    }
}
