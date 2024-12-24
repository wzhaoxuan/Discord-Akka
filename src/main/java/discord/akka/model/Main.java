package discord.akka.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import discord.akka.model.actors.ChangeStatusActor;
import discord.akka.model.actors.LoginActor;
import discord.akka.model.actors.ControllerActor;
import discord.akka.model.actors.PremiumActor;
import discord.akka.model.actors.ProfileActor;


public class Main {
    public static void main(String[] args) {
        // Create ActorSystem
        ActorSystem system = ActorSystem.create("LoginSystem");

        // Create LoginActor
        ActorRef loginActor = system.actorOf(LoginActor.props(), "loginActor");
        ActorRef profileActor = system.actorOf(ProfileActor.props(), "profileActor");
        ActorRef changeStatusActor = system.actorOf(ChangeStatusActor.props(), "changeStatusActor");
        ActorRef premiumActor = system.actorOf(PremiumActor.props(), "premiumActor");

        // Create a controller actor to handle user interaction
        ActorRef controllerActor = system.actorOf(ControllerActor.props(loginActor, profileActor, changeStatusActor, premiumActor), "controllerActor");

        // Start the application loop
        controllerActor.tell(new ControllerActor.StartInteraction(), ActorRef.noSender());
    }
}
