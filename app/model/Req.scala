package model

import play.api.libs.json.Json

final case class Req(amount: Int)

object Req {
  implicit val format = Json.format[Req]
}