package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class CartController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // I don't think forms are necessary here?
  // I will update in the future if the logic changes

  def show = Action {
    Ok("Showing cart...")
  }

  def addProduct(id: Int) = Action { req =>
    Ok("Adding to cart; product id:" + id)
  }

  def removeProduct(id: Int) = Action { req =>
    Ok("Removing from cart; product id:" + id)
  }

}