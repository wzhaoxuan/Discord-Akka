package discord.akka.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import discord.akka.model.actors.LoginActor;
import discord.akka.model.actors.ControllerActor;


public class Main {
    public static void main(String[] args) {
        // Create ActorSystem
        ActorSystem system = ActorSystem.create("LoginSystem");

        // Create LoginActor
        ActorRef loginActor = system.actorOf(LoginActor.props(), "loginActor");

        // Create a controller actor to handle user interaction
        ActorRef controllerActor = system.actorOf(ControllerActor.props(loginActor), "controllerActor");

        // Start the application loop
        controllerActor.tell(new ControllerActor.StartInteraction(), ActorRef.noSender());
    }
}
