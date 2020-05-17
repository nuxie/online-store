package controllers

import javax.inject._
import models.{Cart, CartRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartController @Inject()(cc: MessagesControllerComponents, cartRepo: CartRepository)
                                       (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val cartForm: Form[CreateCartForm] = Form {
    mapping(
      "user_id" -> number,
      "product_id" -> number,
      "quantity" -> number
    )(CreateCartForm.apply)(CreateCartForm.unapply)
  }
  val updateCartForm: Form[UpdateCartForm] = Form {
    mapping(
      "id" -> number,
      "user_id" -> number,
      "product_id" -> number,
      "quantity" -> number
    )(UpdateCartForm.apply)(UpdateCartForm.unapply)
  }

  def add: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.cart.add(cartForm))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    cartForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.cart.add(errorForm))
        )
      },
      cart => {
        cartRepo.add(cart.user_id, cart.product_id, cart.quantity).map { _ =>
          Redirect(routes.CartController.add()).flashing("success" -> "Cart product(s) added")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    cartRepo.list().map(i => Ok(views.html.cart.list(i)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val or_prod: Future[Option[Cart]] = cartRepo.details(id)
    or_prod.map {
      case Some(op) => Ok(views.html.cart.details(op))
      case None => Redirect("/cart/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    cartRepo.delete(id)
    Redirect("/cart/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    cartRepo.details(id).map {
      case Some(c) => Ok(views.html.cart.update(updateCartForm.fill(UpdateCartForm(c.id,
        c.user_id, c.product_id, c.quantity))))
      case None => Redirect("/cart/all")
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCartForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.cart.update(errorForm))
        )
      },
      cart => {
        cartRepo.update(cart.id, Cart(cart.id, cart.user_id, cart.product_id, cart.quantity)).map { _ =>
          Redirect(routes.OrderProductsController.update(cart.id: Int)).flashing("success" -> "Cart updated")
        }
      }
    )
  }
}

case class CreateCartForm(user_id: Int, product_id: Int, quantity: Int)
case class UpdateCartForm(id: Int, user_id: Int, product_id: Int, quantity: Int)