package controllers

import javax.inject._
import models.{Category, CategoryRepository}
import play.api.data.Form
import play.api.data.Forms._
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
    Ok(views.html.categoryadd(categoryForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.categoryadd(errorForm))
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
    categoryRepo.list().map(i => Ok(views.html.categorylist(i)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val cat: Future[Option[Category]] = categoryRepo.details(id)
    cat.map {
      case Some(c) => Ok(views.html.categorydetails(c))
      case None => Redirect("/categories/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    categoryRepo.delete(id)
    Redirect("/categories/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    categoryRepo.details(id).map {
        case Some(c) => Ok(views.html.categoryupdate(updateCategoryForm.fill(UpdateCategoryForm(c.id, c.name))))
        case None => Redirect("/categories/all")
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.categoryupdate(errorForm))
        )
      },
      category => {
        categoryRepo.update(category.id, Category(category.id, category.name)).map { _ =>
          Redirect(routes.CategoryController.update(category.id)).flashing("success" -> "category updated")
        }
      }
    )
  }
}


case class CreateCategoryForm(name: String)
case class UpdateCategoryForm(id: Int, name: String)