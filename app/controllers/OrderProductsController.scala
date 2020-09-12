package controllers

import javax.inject._
import models.{OrderProducts, OrderProductsRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderProductsController @Inject()(cc: MessagesControllerComponents, orderProductsRepo: OrderProductsRepository)
                               (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val redirect = "/order_products/all"

  val orderProductsForm: Form[CreateOrderProductsForm] = Form {
    mapping(
      "orderId" -> number,
      "productId" -> number,
      "quantity" -> number
    )(CreateOrderProductsForm.apply)(CreateOrderProductsForm.unapply)
  }
  val updateOrderProductsForm: Form[UpdateOrderProductsForm] = Form {
    mapping(
      "id" -> number,
      "orderId" -> number,
      "productId" -> number,
      "quantity" -> number
    )(UpdateOrderProductsForm.apply)(UpdateOrderProductsForm.unapply)
  }

  def add: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.order_products.add(orderProductsForm))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    orderProductsForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_products.add(errorForm))
        )
      },
      orderProd => {
        orderProductsRepo.add(orderProd.orderId, orderProd.productId, orderProd.quantity).map { _ =>
          Redirect(routes.OrderProductsController.add()).flashing("success" -> "Order product(s) added")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    orderProductsRepo.list().map(i => Ok(views.html.order_products.list(i)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val orderProd: Future[Option[OrderProducts]] = orderProductsRepo.details(id)
    orderProd.map {
      case Some(op) => Ok(views.html.order_products.details(op))
      case None => Redirect(redirect)
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    orderProductsRepo.delete(id)
    Redirect(redirect)
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    orderProductsRepo.details(id).map {
      case Some(op) => Ok(views.html.order_products.update(updateOrderProductsForm.fill(UpdateOrderProductsForm(op.id,
                                                                      op.orderId, op.productId, op.quantity))))
      case None => Redirect(redirect)
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateOrderProductsForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order_products.update(errorForm))
        )
      },
      orderProd => {
        orderProductsRepo.update(orderProd.id, OrderProducts(orderProd.id, orderProd.orderId, orderProd.productId, orderProd.quantity)).map { _ =>
          Redirect(routes.OrderProductsController.update(orderProd.id: Int)).flashing("success" -> "Order updated")
        }
      }
    )
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    orderProductsRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    orderProductsRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def addJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[OrderProducts].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { op =>
      orderProductsRepo.add(op.orderId, op.productId, op.quantity)
      Ok(Json.obj("status" -> "OK", "message" -> "Order product(s) added"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[OrderProducts].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { op =>
      orderProductsRepo.update(id, op)
      Ok(Json.obj("status" -> "OK", "message" -> "Order product(s) updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    orderProductsRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Order product(s) deleted"))
  }
}

case class CreateOrderProductsForm(orderId: Int, productId: Int, quantity: Int)
case class UpdateOrderProductsForm(id: Int, orderId: Int, productId: Int, quantity: Int)