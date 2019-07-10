package controllers

import javax.inject._
import play.api.libs.json.{JsError, Reads}
import play.api.mvc._
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import model.Req
import actors.ProcessRqActor._
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._


@Singleton
class ApiController @Inject()(
  @Named("process-rq-actor") processRqActor: ActorRef,
    cc: ControllerComponents,
) extends AbstractController(cc) {

  private val logger = Logger(getClass)

  implicit val timeout: Timeout = 5.seconds


  def balance() = Action.async { implicit request: Request[AnyContent] =>
    logger.trace("balance: ")
    (processRqActor ? Balance).mapTo[Result]
  }
  def deposit(): Action[Req] = Action.async(validateJson[Req]) { implicit request =>
    logger.trace("deposit: ")
    (processRqActor ? Deposit(request.body.amount.toInt)).mapTo[Result]
  }
  def withdrawal(): Action[Req] = Action.async(validateJson[Req]) { implicit request =>
    logger.trace("withdrawal: ")
    (processRqActor ? Withdrawal(request.body.amount.toInt)).mapTo[Result]
  }

  // This helper parses and validates JSON using the implicit `placeReads`
  // above, returning errors if the parsed json fails validation.
  private def validateJson[A: Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )
}
