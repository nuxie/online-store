package controllers

import javax.inject._
import models.{Promotion, PromotionRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PromotionController @Inject()(cc: MessagesControllerComponents, promotionRepo: PromotionRepository)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val promotionForm: Form[CreatePromotionForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "flag_active" -> number,
      "product_id" -> number,
      "percentage_sale" -> number
    )(CreatePromotionForm.apply)(CreatePromotionForm.unapply)
  }
  val updatePromotionForm: Form[UpdatePromotionForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "flag_active" -> number,
      "product_id" -> number,
      "percentage_sale" -> number
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
        promotionRepo.add(promotion.name, promotion.flag_active, promotion.product_id, promotion.percentage_sale).map { _ =>
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
      case None => Redirect("/promotions/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    promotionRepo.delete(id)
    Redirect("/promotions/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    promotionRepo.details(id).map {
      case Some(p) => Ok(views.html.promotion.update(updatePromotionForm.fill(UpdatePromotionForm(p.id, p.name, p.flag_active,
        p.product_id, p.percentage_sale))))
      case None => Redirect("/promotions/all")
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
        promotionRepo.update(promotion.id, Promotion(promotion.id, promotion.name, promotion.flag_active, promotion.product_id, promotion.percentage_sale)).map { _ =>
          Redirect(routes.PromotionController.update(promotion.id: Int)).flashing("success" -> "Promotion updated")
        }
      }
    )
  }
}

case class CreatePromotionForm(name: String, flag_active: Int, product_id: Int, percentage_sale: Int)
case class UpdatePromotionForm(id: Int, name: String, flag_active: Int, product_id: Int, percentage_sale: Int)