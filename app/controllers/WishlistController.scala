package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class WishlistController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // one wishlist per customer; I don't think forms are necessary here?
  // I will update in the future if the logic changes

  def show = Action {
    Ok("Showing wishlist")
  }

  def addProduct(id: Int) = Action {
    Ok("Adding to cart; product id:" + id)
  }

  def removeProduct(id: Int) = Action {
    Ok("Removing from cart; product id:" + id)
  }

}
