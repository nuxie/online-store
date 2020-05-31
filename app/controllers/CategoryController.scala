package controllers

import javax.inject._
import models.{Category, CategoryRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(cc: MessagesControllerComponents, categoryRepo: CategoryRepository)
                                  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }
  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def create: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.category.add(categoryForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.category.add(errorForm))
        )
      },
      category => {
        categoryRepo.create(category.name).map { _ =>
          Redirect(routes.CategoryController.create()).flashing("success" -> "category created")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.list().map(c => Ok(views.html.category.list(c)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val cat: Future[Option[Category]] = categoryRepo.details(id)
    cat.map {
      case Some(c) => Ok(views.html.category.details(c))
      case None => Redirect("/categories/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    categoryRepo.delete(id)
    Redirect("/categories/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    categoryRepo.details(id).map {
        case Some(c) => Ok(views.html.category.update(updateCategoryForm.fill(UpdateCategoryForm(c.id, c.name))))
        case None => Redirect("/categories/all")
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.category.update(errorForm))
        )
      },
      category => {
        categoryRepo.update(category.id, Category(category.id, category.name)).map { _ =>
          Redirect(routes.CategoryController.update(category.id: Int)).flashing("success" -> "category updated")
        }
      }
    )
  }
  def listJSON: Action[AnyContent] = Action.async { implicit request =>
      categoryRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def createJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Category].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { category =>
      categoryRepo.create(category.name)
      Ok(Json.obj("status" -> "OK", "message" -> "Category created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Category].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { category =>
      categoryRepo.update(id, category)
      Ok(Json.obj("status" -> "OK", "message" -> "Category updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    categoryRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Category deleted"))
  }
}


case class CreateCategoryForm(name: String)
case class UpdateCategoryForm(id: Int, name: String)