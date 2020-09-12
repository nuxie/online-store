package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Promotion(id: Int, name: String, flagActive: Int, productId: Int, percentageSale: Int)

object Promotion {
  implicit val promotionFormat: OFormat[Promotion] = Json.format[Promotion]
}