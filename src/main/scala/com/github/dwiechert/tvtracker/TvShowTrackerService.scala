package com.github.dwiechert.tvtracker

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import com.github.dwiechert.tvtracker.db.DatabaseHelper
import com.github.dwiechert.tvtracker.db.Show
import com.github.dwiechert.tvtracker.db.Season

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

  dbHelper.insertSeason()

  val route: Route = {
    (path("")) {
      get {
        ctx => ctx.redirect("/index", StatusCodes.PermanentRedirect)
      }
    } ~
    (path("index") & get) {
      respondWithMediaType(`text/html`) {
        complete {
          html.index("TV Show Tracker").toString()
        }
      }
    } ~
    (path("shows") & get) {
      respondWithMediaType(`text/html`) {
        complete {
          val shows = dbHelper.getShows
          html.shows(shows).toString
        }
      }
    } ~
      (path("searchshow") & get) {
        respondWithMediaType(`text/html`) {
          complete {
            html.searchshow("Search Shows").toString()
          }
        }
      } ~
      (path("show") & get) {
        respondWithMediaType(`text/html`) {
          parameters("showName") {
            showName =>
              complete {
                val show = dbHelper.getShow(showName)
                show match {
                  case Some(Show(_)) => {
                    val seasons = dbHelper.getSeasons(showName)
                    html.show(showName, seasons).toString()
                  }
                  case None => html.notfound(showName).toString()
                }
              }
          }
        }
      } ~
      (path("searchseason") & get) {
        respondWithMediaType(`text/html`) {
          complete {
            html.searchseason("Search Season").toString()
          }
        }
      } ~
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
      } ~
      (path("addshow") & post) {
        respondWithMediaType(`text/html`) {
          // TODO: This needs to accept a JSON Show
          entity(as[String]) {
            showName =>
              complete {
                val show = Show(showName)
                dbHelper.insertShow(show)
                html.addshow(show).toString()
              }
          }
        }
      }
      
      
//      get {
//        ctx => ctx.redirect("/index", StatusCodes.PermanentRedirect)
//      }
  }
}