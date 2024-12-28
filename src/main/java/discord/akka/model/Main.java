package discord.akka.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import discord.akka.model.actors.*;
import java.util.Scanner;

public class Main {l
    public static void main(String[] args) {
        // Create ActorSystem
        ActorSystem system = ActorSystem.create("LoginSystem");

        // Create all actors
        ActorRef loginActor = system.actorOf(LoginActor.props(), "loginActor");
        ActorRef profileActor = system.actorOf(ProfileActor.props(), "profileActor");
        ActorRef changeStatusActor = system.actorOf(ChangeStatusActor.props(), "changeStatusActor");
        ActorRef paymentActor = system.actorOf(PaymentActor.props(), "paymentActor");
        ActorRef premiumActor = system.actorOf(PremiumActor.props(paymentActor), "premiumActor");
        ActorRef friendActor = system.actorOf(FriendActor.props(), "friendActor"); // New FriendActor
        ActorRef serverActor = system.actorOf(ServerActor.props(), "serverActor");
        ActorRef callActor = system.actorOf(CallActor.props(), "callActor");
        ActorRef messageActor = system.actorOf(MessageActor.props(), "messageActor");

        // Create controller actor
        ActorRef controllerActor = system.actorOf(
                ControllerActor.props(loginActor, profileActor, changeStatusActor, premiumActor, friendActor, serverActor, callActor, messageActor),
                "controllerActor"
        );

        // Start the application
        controllerActor.tell(new ControllerActor.StartInteraction(), ActorRef.noSender());




    }
}
