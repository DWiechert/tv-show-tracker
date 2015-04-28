package com.github.dwiechert.tvtracker

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.json._
import com.github.dwiechert.tvtracker.TvShowTrackerProtocols._

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
  dbHelper.insertSeason()

  val route: Route = {
    (path("shows") & get) {
      respondWithMediaType(`application/json`) {
        complete {
          val show = dbHelper.getShows
          show.toJson.toString()
        }
      }
    } ~
      (path("show") & get) {
        respondWithMediaType(`application/json`) {
          parameters("name") {
            name =>
              complete {
                val show = dbHelper.getShow(name)
                show match {
                  case Some(Show(_)) => show.toJson.toString()
                  case None          => StatusCodes.NotFound
                }
              }
          }
        }
      } ~
      (path("seasons") & get) {
        respondWithMediaType(`application/json`) {
          parameters("showName") {
            showName =>
              complete {
                val seasons = dbHelper.getSeasons(showName)
                seasons.toJson.toString()
              }
          }
        }
      } ~
      (path("season") & get) {
        respondWithMediaType(`application/json`) {
          parameters("showName", "number".as[Int]) {
            (showName, number) =>
              complete {
                val season = dbHelper.getSeason(showName, number)
                season match {
                  case Some(Season(_, _, _)) => season.toJson.toString()
                  case None                  => StatusCodes.NotFound
                }
              }
          }
        }
      } ~
      (path("hello") & get) {
        respondWithMediaType(`text/html`) {
          complete {
            html.hello(new java.util.Date).toString
          }
        }
      }
  }
}