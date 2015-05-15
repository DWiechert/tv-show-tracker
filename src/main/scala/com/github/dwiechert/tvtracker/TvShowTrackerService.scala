package com.github.dwiechert.tvtracker

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

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
      respondWithMediaType(`text/html`) {
        complete {
          val shows = dbHelper.getShows
          html.shows(shows).toString
        }
      }
      // TODO: Make this `text/html`
      //    } ~
      //      (path("show") & get) {
      //        respondWithMediaType(`application/json`) {
      //          parameters("name") {
      //            name =>
      //              complete {
      //                val show = dbHelper.getShow(name)
      //                show match {
      //                  case Some(Show(_)) => show.toJson.toString()
      //                  case None          => StatusCodes.NotFound
      //                }
      //              }
      //          }
      //        }
    } ~
      (path("searchShows") & get) {
        respondWithMediaType(`text/html`) {
          complete {
            html.searchshows("Search Shows").toString()
          }
        }
      } ~
      (path("seasons") & get) {
        respondWithMediaType(`text/html`) {
          parameters("showName") {
            showName =>
              complete {
                val seasons = dbHelper.getSeasons(showName)
                html.seasons(showName, seasons).toString()
              }
          }
        }
      } ~
      (path("searchSeason") & get) {
        respondWithMediaType(`text/html`) {
          complete {
            html.searchseason("Search Season").toString()
          }
        }
      } ~
      // TODO: Make this `text/html`
      (path("season") & get) {
        respondWithMediaType(`text/html`) {
          parameters("showName", "number".as[Int]) {
            (showName, number) =>
              complete {
                val season = dbHelper.getSeason(showName, number)
                season match {
                  case Some(Season(_, _, _)) => html.season(showName, season.get).toString()
                  case None                  => html.notfound(showName, number).toString()
                }
              }
          }
        }
      }
  }
}