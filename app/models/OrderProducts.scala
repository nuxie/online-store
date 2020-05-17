package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class OrderProducts(id: Int, order_id: Int, product_id: Int, quantity: Int)

object OrderProducts {
  implicit val orderFormat: OFormat[Order] = Json.format[Order]
}