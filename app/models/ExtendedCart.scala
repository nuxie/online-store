package models

import play.api.libs.json.{Json, OFormat}

case class ExtendedCart(id: Int, userId: String, productId: Int, quantity: Int, name: String, price: Long, promotion: Int)

object ExtendedCart {
  implicit val extendedCartFormat: OFormat[ExtendedCart] = Json.format[ExtendedCart]
}
