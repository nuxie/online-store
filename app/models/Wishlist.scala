package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Wishlist(id: Int, user_id: Int, product_id: Int)

object Wishlist {
  implicit val wishlistFormat: OFormat[Wishlist] = Json.format[Wishlist]
}