package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Invoice(id: Int, order_id: Int, payment_due: String)

object Invoice {
  implicit val invoiceFormat: OFormat[Invoice] = Json.format[Invoice]
}