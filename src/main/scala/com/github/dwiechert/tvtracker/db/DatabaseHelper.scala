package com.github.dwiechert.tvtracker.db

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable
import java.net.URI

/**
 * Main enterance to using Slick.
 */
class DatabaseHelper() {
  // Define our table query items
  val shows = TableQuery[Shows]
  val seasons = TableQuery[Seasons]

  // Connect to local PostgreSQL database
  val dbUri = new URI(System.getenv("DATABASE_URL"))
  val username = dbUri.getUserInfo.split(":")(0)
  val password = dbUri.getUserInfo.split(":")(1)
  val db = Database.forURL(s"jdbc:postgresql://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}", driver = "org.postgresql.Driver", user = username, password = password)
  db.withTransaction { implicit session =>
    // Create the tables if they don't already exist
    createIfNotExists(shows, seasons)
  }

  def insertShow(show: Show) = {
    db.withTransaction { implicit session =>
      insertIfNotExists(show)
    }
  }

  def getShows(): List[Show] = {
    db.withTransaction { implicit session =>
      (for {
        s <- shows
      } yield (s)).list
    }
  }

  def getShow(name: String): Option[Show] = {
    db.withTransaction { implicit session =>
      val foundShows = (for {
        s <- shows
        if (s.name === name)
      } yield (s)).list
      if (foundShows.isEmpty) None else Some(foundShows.head)
    }
  }

  def getSeasons(showName: String): List[Season] = {
    db.withTransaction { implicit session =>
      (for {
        s <- seasons
        if (s.showName === showName)
      } yield (s)).list
    }
  }

  def getSeason(showName: String, number: Int): Option[Season] = {
    db.withTransaction { implicit session =>
      val foundSeasons = (for {
        s <- seasons
        if (s.showName === showName && s.number === number)
      } yield (s)).list
      if (foundSeasons.isEmpty) None else Some(foundSeasons.head)
    }
  }

  def insertSeason(season: Season) = {
    db.withTransaction { implicit session =>
      insertIfNotExists(season)
    }
  }

  def getShowsAndSeasons(): Map[Show, List[Season]] = {
    db.withTransaction { implicit session =>
      // Pair shows with the seasons
      val joinedList = (for {
        show <- shows
        season <- seasons
        if (show.name === season.showName)
      } yield (show, season)).list

      joinedList.groupBy(_._1).mapValues(_.map(_._2))
    }
  }

  /**
   * Create a table if it doesn't already exist.
   */
  private def createIfNotExists(tables: TableQuery[_ <: Table[_]]*)(implicit session: Session) {
    tables foreach { table => if (MTable.getTables(table.baseTableRow.tableName).list.isEmpty) table.ddl.create }
  }

  /**
   * Insert a show if it doesn't already exist.
   */
  private def insertIfNotExists(show: Show)(implicit session: Session) {
    if (!(shows.map(s => s.name).list.contains(show.name))) shows.insert(show)
  }

  /**
   * Insert a season if it doesn't already exist.
   */
  private def insertIfNotExists(season: Season)(implicit session: Session) {
    if (!(seasons.map { s => (s.showName, s.number) }.list.contains((season.showName, season.number)))) seasons.insert(season)
  }
}