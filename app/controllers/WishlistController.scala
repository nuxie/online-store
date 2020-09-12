package controllers

import javax.inject._
import models.{Wishlist, WishlistRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishlistController @Inject()(cc: MessagesControllerComponents, wishlistRepo: WishlistRepository)
                               (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val redirect = "/wishlists/all"

  val wishlistForm: Form[CreateWishlistForm] = Form {
    mapping(
      "userId" -> number,
      "productId" -> number
    )(CreateWishlistForm.apply)(CreateWishlistForm.unapply)
  }
  val updateWishlistForm: Form[UpdateWishlistForm] = Form {
    mapping(
      "id" -> number,
      "userId" -> number,
      "productId" -> number
    )(UpdateWishlistForm.apply)(UpdateWishlistForm.unapply)
  }

  def add: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.wishlist.add(wishlistForm))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    wishlistForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.wishlist.add(errorForm))
        )
      },
      wishlist => {
        wishlistRepo.add(wishlist.userId, wishlist.productId).map { _ =>
          Redirect(routes.WishlistController.add()).flashing("success" -> "Product(s) added to wishlist")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    wishlistRepo.list().map(w => Ok(views.html.wishlist.list(w)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val cat: Future[Option[Wishlist]] = wishlistRepo.details(id)
    cat.map {
      case Some(w) => Ok(views.html.wishlist.details(w))
      case None => Redirect(redirect)
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    wishlistRepo.delete(id)
    Redirect(redirect)
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    wishlistRepo.details(id).map {
      case Some(w) => Ok(views.html.wishlist.update(updateWishlistForm.fill(UpdateWishlistForm(w.id, w.userId, w.productId))))
      case None => Redirect(redirect)
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateWishlistForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.wishlist.update(errorForm))
        )
      },
      wishlist => {
        wishlistRepo.update(wishlist.id, Wishlist(wishlist.id, wishlist.userId, wishlist.productId)).map { _ =>
          Redirect(routes.WishlistController.update(wishlist.id: Int)).flashing("success" -> "Wishlist updated")
        }
      }
    )
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    wishlistRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    wishlistRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def addJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Wishlist].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { wishlist =>
      wishlistRepo.add(wishlist.userId, wishlist.productId)
      Ok(Json.obj("status" -> "OK", "message" -> "Wishlist created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Wishlist].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { wishlist =>
      wishlistRepo.update(id, wishlist)
      Ok(Json.obj("status" -> "OK", "message" -> "Wishlist updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    wishlistRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Wishlist deleted"))
  }
}

case class CreateWishlistForm(userId: Int, productId: Int)
case class UpdateWishlistForm(id: Int, userId: Int, productId: Int)