package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class WishlistController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def show(id: Int) = Action {
    Ok("Showing wishlist id: " + id)
  }

  def addProduct(id: Int) = Action {
    Ok("Adding to wishlist; product id:" + id)
  }

  def removeProduct(id: Int) = Action {
    Ok("Removing from wishlist; product id:" + id)
  }

}
