package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class OrderController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create = Action {
    Ok("Create order")
  }

  def createHandle = Action {
    Ok("Handle create order")
  }

  def list = Action {
    Ok("All orders")
  }

  def details(id: Int) = Action {
    Ok("Details of order number " + id)
  }

  def delete(id: Int) = Action {
    Ok("Delete order number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update order number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update order")
  }

}
