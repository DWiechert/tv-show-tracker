package com.github.dwiechert.tvtracker

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import util.Properties

object Boot extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[TvShowTrackerActor], "tv-show-tracker")

  implicit val timeout = Timeout(5.seconds)
  val myPort = Properties.envOrElse("PORT", "8080").toInt // For Heroku compatibility
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = myPort)
}
