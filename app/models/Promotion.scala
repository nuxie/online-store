package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Promotion(id: Int, name: String, flag_active: Boolean, product_id: Int, percentage_sale: Int)

object Promotion {
  implicit val promotionFormat: OFormat[Promotion] = Json.format[Promotion]
}