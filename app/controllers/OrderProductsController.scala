package controllers

import javax.inject._
import models.{OrderProducts, OrderProductsRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderProductsController @Inject()(cc: MessagesControllerComponents, orderProductsRepo: OrderProductsRepository)
                               (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val orderProductsForm: Form[CreateOrderProductsForm] = Form {
    mapping(
      "order_id" -> number,
      "product_id" -> number,
      "quantity" -> number
    )(CreateOrderProductsForm.apply)(CreateOrderProductsForm.unapply)
  }
  val updateOrderProductsForm: Form[UpdateOrderProductsForm] = Form {
    mapping(
      "id" -> number,
      "order_id" -> number,
      "product_id" -> number,
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
        orderProductsRepo.add(orderProd.order_id, orderProd.product_id, orderProd.quantity).map { _ =>
          Redirect(routes.OrderProductsController.add()).flashing("success" -> "Order product(s) added")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    orderProductsRepo.list().map(i => Ok(views.html.order_products.list(i)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val or_prod: Future[Option[OrderProducts]] = orderProductsRepo.details(id)
    or_prod.map {
      case Some(op) => Ok(views.html.order_products.details(op))
      case None => Redirect("/order_products/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    orderProductsRepo.delete(id)
    Redirect("/order_products/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    orderProductsRepo.details(id).map {
      case Some(op) => Ok(views.html.order_products.update(updateOrderProductsForm.fill(UpdateOrderProductsForm(op.id,
                                                                      op.order_id, op.product_id, op.quantity))))
      case None => Redirect("/order_products/all")
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
        orderProductsRepo.update(orderProd.id, OrderProducts(orderProd.id, orderProd.order_id, orderProd.product_id, orderProd.quantity)).map { _ =>
          Redirect(routes.OrderProductsController.update(orderProd.id: Int)).flashing("success" -> "Order updated")
        }
      }
    )
  }
}

case class CreateOrderProductsForm(order_id: Int, product_id: Int, quantity: Int)
case class UpdateOrderProductsForm(id: Int, order_id: Int, product_id: Int, quantity: Int)