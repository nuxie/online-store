package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Cart(id: Int, user_id: String, product_id: Int, quantity: Int)

object Cart {
  implicit val cartFormat: OFormat[Cart] = Json.format[Cart]
}