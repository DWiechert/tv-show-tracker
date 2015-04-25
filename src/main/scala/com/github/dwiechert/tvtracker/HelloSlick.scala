package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

object HelloSlick extends App {
  val users = TableQuery[Users]
  val shows = TableQuery[Shows]
  val seasons = TableQuery[Seasons]

  // Create a connection (called a "session") to a PostgreSQL database
  val db = Database.forURL("jdbc:postgresql://localhost/tv-show-tracker", driver = "org.postgresql.Driver", user = "tv-show-tracker-role", password = "tracker")
  db.withSession { implicit session =>

    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
    createIfNotExists(users, shows, seasons)

    /* Create / Insert */
    users.insert(
      User("John Doe")
    )
    users.insert(
      User("Fred Smith")
    )

    val query = for {
      u <- users
      if (u.id < 50)
    } yield (u)
    query.delete

    insertIfNotExists(Show("Oz"))
    insertIfNotExists(Show("Breaking Bad"))

    insertIfNotExists(Season(1, "Oz"))
    insertIfNotExists(Season(2, "Oz"))
    insertIfNotExists(Season(1, "Breaking Bad"))

    val joinQuery = for {
      show <- shows
      season <- seasons
      if (show.name === season.showName)
    } yield (show, season)

    for {
      (show, season) <- joinQuery
    } println(s"Show: $show\tSeason: $season")
  }

  def createIfNotExists(tables: TableQuery[_ <: Table[_]]*)(implicit session: Session) {
    tables foreach { table => if (MTable.getTables(table.baseTableRow.tableName).list.isEmpty) table.ddl.create }
  }

  def insertIfNotExists(show: Show)(implicit session: Session) {
    if (!(shows.map(s => s.name).list.contains(show.name))) shows.insert(show)
  }

  def insertIfNotExists(season: Season)(implicit session: Session) {
    if (!(seasons.map { s => (s.showName, s.number) }.list.contains((season.showName, season.number)))) seasons.insert(season)
    //    if (!(seasons.filter(s => s.showName === season.showName).map(s => s.number).list.contains(season.number))) seasons.insert(season)
  }
}