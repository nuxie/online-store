package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Wishlist(id: Int, userId: Int, productId: Int)

object Wishlist {
  implicit val wishlistFormat: OFormat[Wishlist] = Json.format[Wishlist]
}