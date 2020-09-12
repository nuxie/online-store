package controllers
import akka.http.scaladsl.model.headers._
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import javax.inject._
import models.{Cart, CartRepository, Category, ExtendedCart, ExtendedProduct, Product, ProductRepository, Promotion, PromotionRepository, Stock, User, UserIdentity}
import play.api.Environment
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, request, _}
import services.UserServiceImpl
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class CartController @Inject()(val silhouette: Silhouette[DefaultEnv], productRepo: ProductRepository,
                              productController: ProductController, promoRepo: PromotionRepository,
                               cc: MessagesControllerComponents, cartRepo: CartRepository)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) with I18nSupport {

  val cartForm: Form[CreateCartForm] = Form {
    mapping(
      "user_id" -> text,
      "product_id" -> number,
      "quantity" -> number
    )(CreateCartForm.apply)(CreateCartForm.unapply)
  }
  val updateCartForm: Form[UpdateCartForm] = Form {
    mapping(
      "id" -> number,
      "user_id" -> text,
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


  def detailsUserJSON(): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => {
        println(user.id)
        cartRepo.detailsUser(user.id).map(i => Ok(Json.toJson(i))) }
      case None => Future.successful(Redirect(routes.HomeController.index()))
    }
  }

  def deleteUser(): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => {
        println(user.id)
        cartRepo.delete(user.id).map(i => Ok(views.html.index())) }
      case None => Future.successful(Redirect(routes.HomeController.index()))
    }

  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    cartRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def addJSON(): Action[JsValue] = silhouette.UserAwareAction(parse.json) { implicit request =>
    request.identity match {
      case Some(user) => {
        println(user.id)
        request.body.validate[Cart].fold({ errors =>
          BadRequest(Json.obj(
            "status" -> "Error",
            "message" -> "Bad JSON"
          ))
        }, { cart =>
          cartRepo.add(user.id, cart.product_id, cart.quantity)
          Ok(Json.obj("status" -> "OK", "message" -> "Cart created"))
        })
      }
    }
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

  def deleteJSON(uid: String): Action[JsValue] = Action(parse.json) {  request =>
      cartRepo.delete(uid)
      Ok(Json.obj("status" -> "OK", "message" -> "Cart deleted"))
  }

  def detailsUserExtendedJSON(): Action[AnyContent] = silhouette.UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => {
        cartRepo.detailsUser(user.id)
          .map(p =>
            p.map(p => extendedDetailsHelper(p))
          ).flatMap(extendedCart => Future.sequence(extendedCart))
          .map(extendedCart => Json.toJson(extendedCart))
          .map(json => Ok(json))
      }
      case None => Future.successful(Redirect(routes.HomeController.index()))
    }
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val prod: Future[Option[Product]] = productRepo.details(id)
    prod.map {
      case Some(p) => Ok(views.html.product.details(p))
      case None => Redirect("/products/all")
    }
  }

  def extendedDetailsHelper(c: Cart): Future[ExtendedCart] = {
    productRepo.details(c.product_id).flatMap(p =>
        promoRepo.promoActiveProduct(c.product_id).map(promo => {
          ExtendedCart(c.id, c.user_id, c.product_id, c.quantity,
            p.getOrElse(Product(0,"None","None",0,0)).name, p.getOrElse(Product(0,"None","None",0,0)).price,
            promo.getOrElse(Promotion(0,"none",1,c.product_id,0)).percentage_sale)}))}
}

case class CreateCartForm(user_id: String, product_id: Int, quantity: Int)
case class UpdateCartForm(id: Int, user_id: String, product_id: Int, quantity: Int)