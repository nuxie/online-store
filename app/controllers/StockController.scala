package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class StockController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def list = Action {
    Ok("All stock items")
  }

  def details(id: Int) = Action {
    Ok("Details of stock item number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update stock item number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update stock item")
  }

}
