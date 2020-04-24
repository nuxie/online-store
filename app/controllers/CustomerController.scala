package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class CustomerController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create = Action {
    Ok("Create customer")
  }

  def createHandle(id: Int) = Action {
    Ok("Handle create customer number " + id)
  }

  def list = Action {
    Ok("All customers")
  }

  def details(id: Int) = Action {
    Ok("Details of customer number " + id)
  }

  def delete(id: Int) = Action {
    Ok("Delete customer number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update customer number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update customer")
  }

}
