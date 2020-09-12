package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class OrderProducts(id: Int, orderId: Int, productId: Int, quantity: Int)

object OrderProducts {
  implicit val orderFormat: OFormat[OrderProducts] = Json.format[OrderProducts]
}