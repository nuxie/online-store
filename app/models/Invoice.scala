package models

import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Invoice(id: Int, orderId: Int, paymentDue: String)

object Invoice {
  implicit val invoiceFormat: OFormat[Invoice] = Json.format[Invoice]
}