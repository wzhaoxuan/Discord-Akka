package discord.akka.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import discord.akka.model.actors.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Create ActorSystem
        ActorSystem system = ActorSystem.create("LoginSystem");

        // Create all actors
        ActorRef loginActor = system.actorOf(LoginActor.props(), "loginActor");
        ActorRef profileActor = system.actorOf(ProfileActor.props(), "profileActor");
        ActorRef changeStatusActor = system.actorOf(ChangeStatusActor.props(), "changeStatusActor");
        ActorRef paymentActor = system.actorOf(PaymentActor.props(), "paymentActor");
        ActorRef premiumActor = system.actorOf(PremiumActor.props(paymentActor), "premiumActor");

        // Create controller actor
        ActorRef controllerActor = system.actorOf(
                ControllerActor.props(loginActor, profileActor, changeStatusActor, premiumActor),
                "controllerActor"
        );

        // Start the application
        controllerActor.tell(new ControllerActor.StartInteraction(), ActorRef.noSender());





        //Li Yang's part - Asking user what he wanna do
        Scanner scanner = new Scanner(System.in);

        // Users
        User ali = new User("Ali");
        User muthu = new User("Muthu");

        // Adding friends
        ali.addFriend(muthu);

        // Creating and managing a server
        Server myServer = new Server("Gaming Buddies");
        myServer.addMember(ali);
        myServer.addMember(muthu);

        // Messaging
        MessageService messageService = new MessageService();

        // Voice and video calls
        VoiceCallService voiceCallService = new VoiceCallService();
        VideoCallService videoCallService = new VideoCallService();

        // AI Chatbot interaction
        AIChatbot chatbot = new AIChatbot();

        // User interaction
        while (true) {
            System.out.println("Type a command (message, voicecall, videocall, chatbot, or exit):");
            String command = scanner.nextLine();

            switch (command.toLowerCase()) {
                case "message":
                    System.out.println("Enter your message:");
                    String message = scanner.nextLine();
                    messageService.sendMessage(ali, muthu, message);
                    break;
                case "voicecall":
                    voiceCallService.startVoiceCall(ali, muthu);
                    break;
                case "videocall":
                    videoCallService.startVideoCall(ali, muthu);
                    break;
                case "chatbot":
                    System.out.println("Enter a message for the chatbot:");
                    String userInput = scanner.nextLine();
                    System.out.println(chatbot.respond(userInput));
                    break;
                case "exit":
                    System.out.println("Exiting the application.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Unknown command.");
            }
        }





    }
}
