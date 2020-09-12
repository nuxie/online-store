package controllers

import javax.inject._
import models.{Invoice, InvoiceRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InvoiceController @Inject()(cc: MessagesControllerComponents, invoiceRepo: InvoiceRepository)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val redirect = "/invoices/all"

  val invoiceForm: Form[CreateInvoiceForm] = Form {
    mapping(
      "orderId" -> number,
      "paymentDue" -> nonEmptyText
    )(CreateInvoiceForm.apply)(CreateInvoiceForm.unapply)
  }
  val updateInvoiceForm: Form[UpdateInvoiceForm] = Form {
    mapping(
      "id" -> number,
      "orderId" -> number,
      "paymentDue" -> nonEmptyText
    )(UpdateInvoiceForm.apply)(UpdateInvoiceForm.unapply)
  }

  def create: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.invoice.add(invoiceForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    invoiceForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.invoice.add(errorForm))
        )
      },
      invoice => {
        invoiceRepo.create(invoice.orderId, invoice.paymentDue).map { _ =>
          Redirect(routes.InvoiceController.create()).flashing("success" -> "Invoice created")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    invoiceRepo.list().map(i => Ok(views.html.invoice.list(i)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val inv: Future[Option[Invoice]] = invoiceRepo.details(id)
    inv.map {
      case Some(i) => Ok(views.html.invoice.details(i))
      case None => Redirect(redirect)
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    invoiceRepo.delete(id)
    Redirect(redirect)
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    invoiceRepo.details(id).map {
      case Some(i) => Ok(views.html.invoice.update(updateInvoiceForm.fill(UpdateInvoiceForm(i.id, i.orderId, i.paymentDue))))
      case None => Redirect(redirect)
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateInvoiceForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.invoice.update(errorForm))
        )
      },
      invoice => {
        invoiceRepo.update(invoice.id, Invoice(invoice.id, invoice.orderId, invoice.paymentDue)).map { _ =>
          Redirect(routes.InvoiceController.update(invoice.id: Int)).flashing("success" -> "Invoice updated")
        }
      }
    )
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    invoiceRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    invoiceRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def createJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Invoice].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { invoice =>
      invoiceRepo.create(invoice.orderId, invoice.paymentDue)
      Ok(Json.obj("status" -> "OK", "message" -> "Invoice created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Invoice].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { invoice =>
      invoiceRepo.update(id, invoice)
      Ok(Json.obj("status" -> "OK", "message" -> "Invoice updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    invoiceRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Invoice deleted"))
  }
}

case class CreateInvoiceForm(orderId: Int, paymentDue: String)
case class UpdateInvoiceForm(id: Int, orderId: Int, paymentDue: String)