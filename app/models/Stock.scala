package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Stock(id: Int, product_id: Int, quantity: Int)

object Stock {
  implicit val stockFormat: OFormat[Stock] = Json.format[Stock]
}