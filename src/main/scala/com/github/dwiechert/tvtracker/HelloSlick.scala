package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.AbstractTable

/**
 * Main enterance to using Slick.
 */
object HelloSlick extends App {
  // Define our table query items
  val users = TableQuery[Users]
  val shows = TableQuery[Shows]
  val seasons = TableQuery[Seasons]

  // Connect to local PostgreSQL database
  val db = Database.forURL("jdbc:postgresql://localhost/tv-show-tracker", driver = "org.postgresql.Driver", user = "tv-show-tracker-role", password = "tracker")
  db.withSession { implicit session =>

    // Create the tables if they don't already exist
    createIfNotExists(users, shows, seasons)

    // Insert some users
    users.insert(
      User("John Doe")
    )
    users.insert(
      User("Fred Smith")
    )

    // Query and delete some users
    val query = for {
      u <- users
      if (u.id < 50)
    } yield (u)
    query.delete

    // Insert some shows
    insertIfNotExists(Show("Oz"))
    insertIfNotExists(Show("Breaking Bad"))

    // Insert some seasons
    insertIfNotExists(Season(1, "Oz"))
    insertIfNotExists(Season(2, "Oz"))
    insertIfNotExists(Season(1, "Breaking Bad"))

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

  /**
   * Create a table if it doesn't already exist.
   */
  def createIfNotExists(tables: TableQuery[_ <: Table[_]]*)(implicit session: Session) {
    tables foreach { table => if (MTable.getTables(table.baseTableRow.tableName).list.isEmpty) table.ddl.create }
  }

  /**
   * Insert a show if it doesn't already exist.
   */
  def insertIfNotExists(show: Show)(implicit session: Session) {
    if (!(shows.map(s => s.name).list.contains(show.name))) shows.insert(show)
  }

  /**
   * Insert a season if it doesn't already exist.
   */
  def insertIfNotExists(season: Season)(implicit session: Session) {
    if (!(seasons.map { s => (s.showName, s.number) }.list.contains((season.showName, season.number)))) seasons.insert(season)
  }
}