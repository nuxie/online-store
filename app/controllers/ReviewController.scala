package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class ReviewController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create = Action {
    Ok("Create review")
  }

  def createHandle = Action {
    Ok("Handle create review")
  }

  def list = Action {
    Ok("All reviews")
  }

  def details(id: Int) = Action {
    Ok("Details of review number " + id)
  }

  def delete(id: Int) = Action {
    Ok("Delete review number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update review number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update review")
  }

}