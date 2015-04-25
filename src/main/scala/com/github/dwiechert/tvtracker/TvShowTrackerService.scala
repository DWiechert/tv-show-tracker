package com.github.dwiechert.tvtracker

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.json._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class TvShowTrackerActor extends Actor with TvShowTrackerService {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

// this trait defines our service behavior independently from the service actor
trait TvShowTrackerService extends HttpService {
  val dbHelper = new DatabaseHelper(user = "tv-show-tracker-role", password = "tracker")

  dbHelper.insertShow()

  import spray.json._
  import com.github.dwiechert.tvtracker.ShowProtocol._

  val route: Route = {
    (path("show") & get) {
      respondWithMediaType(`application/json`) {
        parameter("name") {
          name =>
            complete {
              val show = dbHelper.getShow(name)
              show.toJson.toString()
            }
        }
      }
    } 
  }
}