package discord.akka.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import discord.akka.model.actors.*;

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
    }
}
