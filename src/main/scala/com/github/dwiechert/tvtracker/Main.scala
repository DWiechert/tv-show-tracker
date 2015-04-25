package com.github.dwiechert.tvtracker

object Main extends App {
  val dbHelper = new DatabaseHelper(user = "tv-show-tracker-role", password = "tracker")
  
  dbHelper.insertUsers()
  dbHelper.deleteUsers()
  dbHelper.insertShow()
  dbHelper.insertSeason()
  dbHelper.queryShowsAndSeasons()
}