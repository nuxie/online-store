package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class CartController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def show(id: Int) = Action { req =>
    Ok("Showing cart for the user id: " + id)
  }

  def addProduct(id: Int) = Action { req =>
    Ok("Adding to cart; product id:" + id)
  }

  def removeProduct(id: Int) = Action { req =>
    Ok("Removing from cart; product id:" + id)
  }

}