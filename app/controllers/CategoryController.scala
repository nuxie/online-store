package controllers

import javax.inject._
import models.CategoryRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.ExecutionContext

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

  def create = Action {
    Ok("Create category")
  }

  def createHandle = Action {
    Ok("Handle create category")
  }

  def list = Action {
    Ok("All categories")
  }

  def details(id: Int) = Action {
    Ok("Details of category number " + id)
  }

  def delete(id: Int) = Action {
    Ok("Delete category number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update category number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update category")
  }

}
case class CreateCategoryForm(name: String)
case class UpdateCategoryForm(id: Int, name: String)