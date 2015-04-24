package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

object HelloSlick extends App {
  // The query interface for the Suppliers table
  val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

  // the query interface for the Coffees table
  val coffees: TableQuery[Coffees] = TableQuery[Coffees]

  // Create a connection (called a "session") to a PostgreSQL database
  val db = Database.forURL("jdbc:postgresql://localhost/tv-show-tracker", driver = "org.postgresql.Driver", user = "tv-show-tracker-role", password = "tracker")
  db.withSession { implicit session =>

    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
//    (suppliers.ddl ++ coffees.ddl).create
    createIfNotExists(suppliers, coffees)

    /* Create / Insert */

    // Insert some suppliers
    suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
    suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
    suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")

    // Insert some coffees (using JDBC's batch insert feature)
    val coffeesInsertResult: Option[Int] = coffees ++= Seq(
      ("Colombian", 101, 7.99, 0, 0),
      ("French_Roast", 49, 8.99, 0, 0),
      ("Espresso", 150, 9.99, 0, 0),
      ("Colombian_Decaf", 101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )

    val allSuppliers: List[(Int, String, String, String, String, String)] =
      suppliers.list

    // Print the number of rows inserted
    coffeesInsertResult foreach { numRows =>
      println(s"Inserted $numRows rows into the Coffees table")
    }

    /* Read / Query / Select */

    // Print the SQL for the Coffees query
    println("Generated SQL for base Coffees query:\n" + coffees.selectStatement)

    // Query the Coffees table using a foreach and print each row
    coffees foreach {
      case (name, supID, price, sales, total) =>
        println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    }

    /* Filtering / Where */

    // Construct a query where the price of Coffees is > 9.0
    //    val filterQuery: Query[Coffees, (String, Int, Double, Int, Int), ""] =
    //      coffees.filter(_.price > 9.0)
    //
    //    println("Generated SQL for filter query:\n" + filterQuery.selectStatement)
    //
    //    // Execute the query
    //    println(filterQuery.list)
  }

  def createIfNotExists(tables: TableQuery[_ <: Table[_]]*)(implicit session: Session) {
    tables foreach { table => if (MTable.getTables(table.baseTableRow.tableName).list.isEmpty) table.ddl.create }
  }
}