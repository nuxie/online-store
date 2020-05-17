package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Order(id: Int, user_id: Int)

object Order {
  implicit val orderFormat: OFormat[Order] = Json.format[Order]
}