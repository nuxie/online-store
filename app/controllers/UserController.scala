package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create = Action {
    Ok("Create user")
  }

  def createHandle = Action {
    Ok("Handle create user")
  }

  def list = Action {
    Ok("All users")
  }

  def details(id: Int) = Action {
    Ok("Details of user number " + id)
  }

  def delete(id: Int) = Action {
    Ok("Delete user number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update user number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update user")
  }

}
