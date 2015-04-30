package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend

/**
 * Case class to define a User.
 */
case class User(name: String, id: Option[Int] = None)

/**
 * Class to define the Users table.
 */
class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def * = (name, id.?) <> (User.tupled, User.unapply)
}

/**
 * Case class to define a Show.
 */
case class Show(name: String)

/**
 * Class to define the Shows table.
 */
class Shows(tag: Tag) extends Table[Show](tag, "SHOWS") {
  def name = column[String]("NAME", O.PrimaryKey, O.NotNull)
  def * = (name) <> (Show, Show.unapply)
}

/**
 * Case class to define a Season.
 */
case class Season(number: Int, showName: String, id: Option[Int] = None)

/**
 * Class to define the Seasons table.
 */
class Seasons(tag: Tag) extends Table[Season](tag, "SEASONS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def number = column[Int]("NUMBER")
  def showName = column[String]("SHOW_NAME")
  def * = (number, showName, id.?) <> (Season.tupled, Season.unapply)

  def show = foreignKey("SHOW_FK", showName, TableQuery[Shows])(_.name)
}