package model

case class Account(id: Long, balance: Int, depCount: Int, withdrawCount: Int, depUpdated: String, withdrawUpdated: String)