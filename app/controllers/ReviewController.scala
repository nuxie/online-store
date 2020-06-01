package controllers

import javax.inject._
import models.{Review, ReviewRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewController @Inject()(cc: MessagesControllerComponents, reviewRepo: ReviewRepository)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val reviewForm: Form[CreateReviewForm] = Form {
    mapping(
      "product_id" -> number,
      "description" -> nonEmptyText
    )(CreateReviewForm.apply)(CreateReviewForm.unapply)
  }
  val updateReviewForm: Form[UpdateReviewForm] = Form {
    mapping(
      "id" -> number,
      "product_id" -> number,
      "description" -> nonEmptyText
    )(UpdateReviewForm.apply)(UpdateReviewForm.unapply)
  }

  def create: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.review.add(reviewForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    reviewForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.review.add(errorForm))
        )
      },
      review => {
        reviewRepo.create(review.product_id, review.description).map { _ =>
          Redirect(routes.ReviewController.create()).flashing("success" -> "Review created")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    reviewRepo.list().map(r => Ok(views.html.review.list(r)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val rev: Future[Option[Review]] = reviewRepo.details(id)
    rev.map {
      case Some(r) => Ok(views.html.review.details(r))
      case None => Redirect("/reviews/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    reviewRepo.delete(id)
    Redirect("/reviews/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    reviewRepo.details(id).map {
      case Some(r) => Ok(views.html.review.update(updateReviewForm.fill(UpdateReviewForm(r.id, r.product_id, r.description))))
      case None => Redirect("/reviews/all")
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateReviewForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.review.update(errorForm))
        )
      },
      review => {
        reviewRepo.update(review.id, Review(review.id, review.product_id, review.description)).map { _ =>
          Redirect(routes.ReviewController.update(review.id: Int)).flashing("success" -> "Review updated")
        }
      }
    )
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    reviewRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    reviewRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def createJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Review].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { review =>
      reviewRepo.create(review.product_id, review.description)
      Ok(Json.obj("status" -> "OK", "message" -> "Review created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Review].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { review =>
      reviewRepo.update(id, review)
      Ok(Json.obj("status" -> "OK", "message" -> "Review updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    reviewRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Review deleted"))
  }
}

case class CreateReviewForm(product_id: Int, description: String)
case class UpdateReviewForm(id: Int, product_id: Int, description: String)