package com.github.dwiechert.tvtracker

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.ForeignKeyQuery

case class User(name: String, id: Option[Int] = None)
class Users(tag: Tag) extends Table[User](tag, "USERS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (name, id.?) <> (User.tupled, User.unapply)
}