package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend

case class User(name: String, id: Option[Int] = None)
class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def * = (name, id.?) <> (User.tupled, User.unapply)
}

case class Show(name: String)
class Shows(tag: Tag) extends Table[Show](tag, "SHOWS") {
  def name = column[String]("NAME", O.PrimaryKey, O.NotNull)
  def * = (name) <> (Show, Show.unapply)
}

case class Season(number: Int, showName: String, id: Option[Int] = None)
class Seasons(tag: Tag) extends Table[Season](tag, "SEASONS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def number = column[Int]("NUMBER")
  def showName = column[String]("SHOW_NAME")
  def * = (number, showName, id.?) <> (Season.tupled, Season.unapply)
  
  def show = foreignKey("SHOW_FK", showName, TableQuery[Shows])(_.name)
}