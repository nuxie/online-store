package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class ProductsController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

    def create = Action {
      Ok("Create product")
    }

    def createHandle(id: Int) = Action {
      Ok("Handle create product number " + id)
    }

    def list = Action {
      Ok("All products")
    }

    def details(id: Int) = Action {
      Ok("Details of product number " + id)
    }

    def delete(id: Int) = Action {
      Ok("Delete product number " + id)
    }

    def update(id: Int) = Action {
      Ok("Update product number " + id)
    }

    def updateHandle = Action {
      Ok("Handle update product")
    }

}
