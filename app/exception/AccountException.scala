package exception

import scala.concurrent.Future

object AccountException {

  class ExceededException() extends Exception()
  final case class ExceededMaxWithdrawPerDay() extends ExceededException {
    override def getMessage(): String = "Exceeded Maximum Withdrawal Per Day."
  }
  final case class ExceededMaxWithdrawPerTrans() extends ExceededException {
    override def getMessage(): String = "Exceeded Maximum Withdrawal Per Transaction."
  }
  final case class ExceededMaxWithdrawFrequencyPerDay() extends ExceededException {
    override def getMessage(): String = "Exceeded Maximum Withdrawal Frequency Per Day."
  }
  final case class ExceededMaxDepositPerDay() extends ExceededException {
    override def getMessage(): String = "Exceeded Maximum Deposit Per Day."
  }
  final case class ExceededMaxDepositPerTrans() extends ExceededException {
    override def getMessage(): String = "Exceeded Maximum Deposit Per Transaction."
  }
  final case class ExceededMaxDepositFrequencyPerDay() extends ExceededException {
    override def getMessage(): String = "Exceeded Maximum Deposit Frequency Per Day."
  }
  final case class ExceededAccountBalance() extends ExceededException {
    override def getMessage(): String = "Exceeded Account Balance."
  }

}