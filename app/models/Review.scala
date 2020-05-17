package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Review(id: Int, product_id: Int, description: String)

object Review {
  implicit val reviewFormat: OFormat[Review] = Json.format[Review]
}