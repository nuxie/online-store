package controllers

import javax.inject._
import models.{Cart, CartRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
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
    val car: Future[Option[Cart]] = cartRepo.details(id)
    car.map {
      case Some(c) => Ok(views.html.cart.details(c))
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

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    cartRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    cartRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def addJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Cart].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { cart =>
      cartRepo.add(cart.user_id, cart.product_id, cart.quantity)
      Ok(Json.obj("status" -> "OK", "message" -> "Cart created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Cart].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { cart =>
      cartRepo.update(id, cart)
      Ok(Json.obj("status" -> "OK", "message" -> "Cart updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
      cartRepo.delete(id)
      Ok(Json.obj("status" -> "OK", "message" -> "Cart deleted"))
  }
}

case class CreateCartForm(user_id: Int, product_id: Int, quantity: Int)
case class UpdateCartForm(id: Int, user_id: Int, product_id: Int, quantity: Int)