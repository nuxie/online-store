package controllers

import javax.inject._
import models.{Wishlist, WishlistRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishlistController @Inject()(cc: MessagesControllerComponents, wishlistRepo: WishlistRepository)
                               (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val wishlistForm: Form[CreateWishlistForm] = Form {
    mapping(
      "user_id" -> number,
      "product_id" -> number
    )(CreateWishlistForm.apply)(CreateWishlistForm.unapply)
  }
  val updateWishlistForm: Form[UpdateWishlistForm] = Form {
    mapping(
      "id" -> number,
      "user_id" -> number,
      "product_id" -> number
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
        wishlistRepo.add(wishlist.user_id, wishlist.product_id).map { _ =>
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
      case None => Redirect("/wishlists/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    wishlistRepo.delete(id)
    Redirect("/wishlists/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    wishlistRepo.details(id).map {
      case Some(w) => Ok(views.html.wishlist.update(updateWishlistForm.fill(UpdateWishlistForm(w.id, w.user_id, w.product_id))))
      case None => Redirect("/wishlists/all")
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
        wishlistRepo.update(wishlist.id, Wishlist(wishlist.id, wishlist.user_id, wishlist.product_id)).map { _ =>
          Redirect(routes.WishlistController.update(wishlist.id: Int)).flashing("success" -> "Wishlist updated")
        }
      }
    )
  }
}

case class CreateWishlistForm(user_id: Int, product_id: Int)
case class UpdateWishlistForm(id: Int, user_id: Int, product_id: Int)