package controllers

import javax.inject._
import models.{Promotion, PromotionRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PromotionController @Inject()(cc: MessagesControllerComponents, promotionRepo: PromotionRepository)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val redirect = "/promotions/all"

  val promotionForm: Form[CreatePromotionForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "flagActive" -> number,
      "productId" -> number,
      "percentageSale" -> number
    )(CreatePromotionForm.apply)(CreatePromotionForm.unapply)
  }
  val updatePromotionForm: Form[UpdatePromotionForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "flagActive" -> number,
      "productId" -> number,
      "percentageSale" -> number
    )(UpdatePromotionForm.apply)(UpdatePromotionForm.unapply)
  }

  def create: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.promotion.add(promotionForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    promotionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.promotion.add(errorForm))
        )
      },
      promotion => {
        promotionRepo.add(promotion.name, promotion.flagActive, promotion.productId, promotion.percentageSale).map { _ =>
          Redirect(routes.PromotionController.create()).flashing("success" -> "Promotion created")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    promotionRepo.list().map(p => Ok(views.html.promotion.list(p)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val promo: Future[Option[Promotion]] = promotionRepo.details(id)
    promo.map {
      case Some(p) => Ok(views.html.promotion.details(p))
      case None => Redirect(redirect)
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    promotionRepo.delete(id)
    Redirect(redirect)
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    promotionRepo.details(id).map {
      case Some(p) => Ok(views.html.promotion.update(updatePromotionForm.fill(UpdatePromotionForm(p.id, p.name, p.flagActive,
        p.productId, p.percentageSale))))
      case None => Redirect(redirect)
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updatePromotionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.promotion.update(errorForm))
        )
      },
      promotion => {
        promotionRepo.update(promotion.id, Promotion(promotion.id, promotion.name, promotion.flagActive, promotion.productId, promotion.percentageSale)).map { _ =>
          Redirect(routes.PromotionController.update(promotion.id: Int)).flashing("success" -> "Promotion updated")
        }
      }
    )
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    promotionRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    promotionRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def addJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Promotion].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { promo =>
      promotionRepo.add(promo.name, promo.flagActive, promo.productId, promo.percentageSale)
      Ok(Json.obj("status" -> "OK", "message" -> "Promotion created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Promotion].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { promo =>
      promotionRepo.update(id, promo)
      Ok(Json.obj("status" -> "OK", "message" -> "Promotion updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    promotionRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Promotion deleted"))
  }
}

case class CreatePromotionForm(name: String, flagActive: Int, productId: Int, percentageSale: Int)
case class UpdatePromotionForm(id: Int, name: String, flagActive: Int, productId: Int, percentageSale: Int)