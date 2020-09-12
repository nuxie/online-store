package models

import play.api.libs.json.{Json, OFormat}

case class ExtendedProduct(id: Int, name: String, description: String, category_id: Int, price: Long, category: String, stock: Int, promotion: Int)

object ExtendedProduct {
  implicit val extendedProductFormat: OFormat[ExtendedProduct] = Json.format[ExtendedProduct]
}
