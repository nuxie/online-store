package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class InvoiceController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def create = Action {
    Ok("Create invoice")
  }

  def createHandle(id: Int) = Action {
    Ok("Handle create invoice number " + id)
  }

  def list = Action {
    Ok("All invoices")
  }

  def details(id: Int) = Action {
    Ok("Details of invoice number " + id)
  }

  def delete(id: Int) = Action {
    Ok("Delete invoice number " + id)
  }

  def update(id: Int) = Action {
    Ok("Update invoice number " + id)
  }

  def updateHandle = Action {
    Ok("Handle update invoice")
  }

}
