package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.AbstractTable

/**
 * Main enterance to using Slick.
 */
class DatabaseHelper(val user: String, val password: String) {
  // Define our table query items
  val users = TableQuery[Users]
  val shows = TableQuery[Shows]
  val seasons = TableQuery[Seasons]

  // Connect to local PostgreSQL database
  val db = Database.forURL("jdbc:postgresql://localhost/tv-show-tracker", driver = "org.postgresql.Driver", user = user, password = password)
  db.withTransaction { implicit session =>
    // Create the tables if they don't already exist
    createIfNotExists(users, shows, seasons)
  }

  def insertUsers() = {
    db.withTransaction { implicit session =>
      // Insert some users
      users.insert(
        User("John Doe")
      )
      users.insert(
        User("Fred Smith")
      )
    }
  }

  def deleteUsers() {
    db.withTransaction { implicit session =>
      // Query and delete some users
      val query = for {
        u <- users
        if (u.id < 50)
      } yield (u)
      query.delete
    }
  }

  def insertShow() = {
    db.withTransaction { implicit session =>
      // Insert some shows
      insertIfNotExists(Show("Oz"))
      insertIfNotExists(Show("Breaking Bad"))
    }
  }
  
  def getShow(name: String): Show = {
    db.withTransaction { implicit session =>
      (for {
        s <- shows
        if (s.name === name)
      } yield (s)).list.head
    }
  }
  
  def getSeasons(showName: String): List[Season] = {
    db.withTransaction { implicit session =>
      (for {
        s <- seasons
        if (s.showName === showName)
      } yield(s)).list 
    }
  }

  def insertSeason() = {
    db.withTransaction { implicit session =>
      // Insert some seasons
      insertIfNotExists(Season(1, "Oz"))
      insertIfNotExists(Season(2, "Oz"))
      insertIfNotExists(Season(1, "Breaking Bad"))
    }
  }

  def queryShowsAndSeasons() = {
    db.withTransaction { implicit session =>
      // Pair shows with the seasons
      val joinQuery = for {
        show <- shows
        season <- seasons
        if (show.name === season.showName)
      } yield (show, season)

      // Print out all shows and their seasons
      for {
        (show, season) <- joinQuery
      } println(s"Show: $show\tSeason: $season")
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