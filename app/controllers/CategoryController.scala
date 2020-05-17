package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class CategoryController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

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
