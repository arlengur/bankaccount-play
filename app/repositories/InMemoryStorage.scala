package repositories

import scala.concurrent.Future
import exception.AccountException._
import java.text.SimpleDateFormat
import java.util.Date
import utils.Utils._
import model.Account

class InMemoryAccountRepo {
  private var account: Account = Account(1, 0, 0, 0, "", "")

  def balance(): Future[Int] = Future.successful(account.balance)

  def deposit(count: Int): Future[Boolean] = synchronized {
    println(account)
    if (count >= 150000) Future.failed(ExceededMaxDepositPerDay())
    else if (count >= 40000) Future.failed(ExceededMaxDepositPerTrans())
    else if (account.depCount > 4) Future.failed(ExceededMaxDepositFrequencyPerDay())
    else {
      account = account.copy(
        balance = account.balance + count,
        depCount = getDepCount,
        depUpdated = dateToString(new Date())
      )
      Future.successful(true)
    }
  }

  def withdrawal(count: Int): Future[Boolean] = synchronized {
    println(account)
    if (count >= 50000) Future.failed(ExceededMaxWithdrawPerDay())
    else if (count >= 20000) Future.failed(ExceededMaxWithdrawPerTrans())
    else if (account.withdrawCount > 3) Future.failed(ExceededMaxWithdrawFrequencyPerDay())
    else if (account.balance < count) Future.failed(ExceededAccountBalance())
    else {
      account = account.copy(
        balance = account.balance - count,
        withdrawCount = getWithdrawCount,
        withdrawUpdated = dateToString(new Date())
      )
      Future.successful(true)
    }
  }

  def getDepCount(): Int = {
    val now = dateToString(new Date())
    if (now == account.depUpdated) account.depCount + 1 else 1
  }
  def getWithdrawCount(): Int = {
    val now = dateToString(new Date())
    if (now == account.withdrawUpdated) account.withdrawCount + 1 else 1
  }

  override def toString = s"InMemoryAccountRepo:\n${account}"
}
