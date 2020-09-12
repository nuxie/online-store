package controllers

import javax.inject._
import models.{Order, OrderRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(cc: MessagesControllerComponents, orderRepo: OrderRepository)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val redirect = "/orders/all"

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "userId" -> number
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }
  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> number,
      "userId" -> number
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def create: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.order.add(orderForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order.add(errorForm))
        )
      },
      order => {
        orderRepo.create(order.userId).map { _ =>
          Redirect(routes.OrderController.create()).flashing("success" -> "Order created")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    orderRepo.list().map(o => Ok(views.html.order.list(o)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val ord: Future[Option[Order]] = orderRepo.details(id)
    ord.map {
      case Some(o) => Ok(views.html.order.details(o))
      case None => Redirect(redirect)
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    orderRepo.delete(id)
    Redirect(redirect)
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    orderRepo.details(id).map {
      case Some(i) => Ok(views.html.order.update(updateOrderForm.fill(UpdateOrderForm(i.id, i.userId))))
      case None => Redirect(redirect)
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.order.update(errorForm))
        )
      },
      order => {
        orderRepo.update(order.id, Order(order.id, order.userId)).map { _ =>
          Redirect(routes.OrderController.update(order.id: Int)).flashing("success" -> "Order updated")
        }
      }
    )
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    orderRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    orderRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def createJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Order].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { order =>
      orderRepo.create(order.userId)
      Ok(Json.obj("status" -> "OK", "message" -> "Order created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Order].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { order =>
      orderRepo.update(id, order)
      Ok(Json.obj("status" -> "OK", "message" -> "Order updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    orderRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Order deleted"))
  }
}

case class CreateOrderForm(userId: Int)
case class UpdateOrderForm(id: Int, userId: Int)