package controllers

import javax.inject._
import models.{Order, OrderRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(cc: MessagesControllerComponents, orderRepo: OrderRepository)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "user_id" -> number
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }
  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> number,
      "user_id" -> number
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
        orderRepo.create(order.user_id).map { _ =>
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
      case None => Redirect("/orders/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    orderRepo.delete(id)
    Redirect("/orders/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    orderRepo.details(id).map {
      case Some(i) => Ok(views.html.order.update(updateOrderForm.fill(UpdateOrderForm(i.id, i.user_id))))
      case None => Redirect("/orders/all")
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
        orderRepo.update(order.id, Order(order.id, order.user_id)).map { _ =>
          Redirect(routes.OrderController.update(order.id: Int)).flashing("success" -> "Order updated")
        }
      }
    )
  }
}

case class CreateOrderForm(user_id: Int)
case class UpdateOrderForm(id: Int, user_id: Int)