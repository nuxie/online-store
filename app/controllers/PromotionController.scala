package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class PromotionController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create = Action {
    Ok("Create promotion")
  }

  def createHandle(id: Int) = Action {
    Ok("Handle create promotion number " + id)
  }

  def list = Action {
    Ok("All promotions")
  }

  def details(id: Int) = Action {
    Ok("Details of promotion number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update promotion number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update promotion")
  }

}
