package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Product(id: Int, name: String, description: String, categoryId: Int, price: Long)

object Product {
  implicit val productFormat: OFormat[Product] = Json.format[Product]
}