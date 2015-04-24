package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

object HelloSlick extends App {
  // the base query for the Users table
  val users = TableQuery[Users]

  // Create a connection (called a "session") to a PostgreSQL database
  val db = Database.forURL("jdbc:postgresql://localhost/tv-show-tracker", driver = "org.postgresql.Driver", user = "tv-show-tracker-role", password = "tracker")
  db.withSession { implicit session =>

    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
    createIfNotExists(users)

    /* Create / Insert */
    users.insert(
      User("John Doe")
    )
    users.insert(
      User("Fred Smith")
    )
  }

  def createIfNotExists(tables: TableQuery[_ <: Table[_]]*)(implicit session: Session) {
    tables foreach { table => if (MTable.getTables(table.baseTableRow.tableName).list.isEmpty) table.ddl.create }
  }
}