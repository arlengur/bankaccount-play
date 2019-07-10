package repositories

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import exception.AccountException._
import java.util.Date
import utils.Utils._
import model.Account

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DbStorage @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.db
  import dbConfig.profile.api._

  private class AccountTable(tag: Tag) extends Table[Account](tag, "ACCOUNT") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def balance = column[Int]("BALANCE")
    def depCount = column[Int]("DEP_COUNT")
    def withdrawCount = column[Int]("WITHDRAW_COUNT")
    def depUpdated = column[String]("DEP_UPDATED")
    def withdrawUpdated = column[String]("WITHDRAW_UPDATED")
    override def * = (id, balance, depCount, withdrawCount, depUpdated, withdrawUpdated) <> (Account.tupled, Account.unapply)
  }

  private val accounts = TableQuery[AccountTable]

  def balance(): Future[Int] = {
    db.run(accounts.map(_.balance).result.head)
  }

  def deposit(count: Int): Future[Boolean] = {
    if (count >= 150000) Future.failed(ExceededMaxDepositPerDay())
    else if (count >= 40000) Future.failed(ExceededMaxDepositPerTrans())
    else {
      val targetRows = accounts.map(acc => (acc.balance, acc.depCount, acc.depUpdated))
      val actions = for {
        fieldsOption <- targetRows.result.headOption
        updateActionOption = fieldsOption.map { f =>
          if (f._2 >= 4) throw new ExceededMaxDepositFrequencyPerDay()
          targetRows.update(f._1 + count, getDepCount(f._2, f._3), dateToString(new Date()))
        }
        affected <- updateActionOption.getOrElse(DBIO.successful(0))
      } yield affected != 0

      db.run(actions)
    }
  }

  def withdrawal(count: Int): Future[Boolean] = {
    if (count >= 50000) Future.failed(ExceededMaxWithdrawPerDay())
    else if (count >= 20000) Future.failed(ExceededMaxWithdrawPerTrans())
    else {
      val targetRows = accounts.map(acc => (acc.balance, acc.withdrawCount, acc.withdrawUpdated))
      val actions = for {
        fieldsOption <- targetRows.result.headOption
        updateActionOption = fieldsOption.map { f =>
          if (f._2 >= 3) throw new ExceededMaxWithdrawFrequencyPerDay()
          if (f._1 < count) throw new ExceededAccountBalance()
          targetRows.update(f._1 - count, getWithdrawCount(f._2, f._3), dateToString(new Date()))
        }
        affected <- updateActionOption.getOrElse(DBIO.successful(0))
      } yield affected != 0

      db.run(actions)      
    }
  }

  def getDepCount(depCount: Int, depUpdated: String): Int = {
    val now = dateToString(new Date())
    if (now == depUpdated) depCount + 1 else 1
  }

  def getWithdrawCount(withdrawCount: Int, withdrawUpdated: String): Int = {
    val now = dateToString(new Date())
    if (now == withdrawUpdated) withdrawCount + 1 else 1
  }
}
