package actors

import akka.actor._
import javax.inject.Inject
import repositories.DbStorage
import play.api.Configuration
import play.api.mvc.Result
import play.api.mvc._
import scala.concurrent.ExecutionContext
import exception.AccountException.ExceededException
import scala.util.control.NonFatal
import play.api.mvc.Results._

object ProcessRqActor {
  case class Deposit(amount: Int)
  case class Withdrawal(amount: Int)
  case object Balance
}

class ProcessRqActor @Inject()(accountRepo: DbStorage)(implicit ec: ExecutionContext) extends Actor {
  import ProcessRqActor._

  def receive = {
    case Balance =>
    val senderRef = sender()
      accountRepo
        .balance()
        .map(b => senderRef ! Ok(s"Your balance is: $$$b"))
    case Deposit(amount: Int) =>
      val senderRef = sender()
      accountRepo
        .deposit(amount)
        .map(res => senderRef ! Ok(s"Successful deposit: $$$amount"))
        .recover(requestErrorHandling(senderRef))
    case Withdrawal(amount: Int) =>
      val senderRef = sender()
      accountRepo
        .withdrawal(amount)
        .map(res => senderRef ! Ok(s"Successful withdrawal: $$$amount"))
        .recover(requestErrorHandling(senderRef))
  }

  private def requestErrorHandling(senderRef: ActorRef): PartialFunction[Throwable, Unit] = {
    case ex: ExceededException => senderRef ! Forbidden(ex.getMessage)
    case NonFatal(e)           => senderRef ! InternalServerError(e.getMessage)
  }
}
