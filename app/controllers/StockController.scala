package controllers

import javax.inject._
import models.{Stock, StockRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockController @Inject()(cc: MessagesControllerComponents, stockRepo: StockRepository)
                                (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val stockForm: Form[CreateStockForm] = Form {
    mapping(
      "product_id" -> number,
      "quantity" -> number
    )(CreateStockForm.apply)(CreateStockForm.unapply)
  }
  val updateStockForm: Form[UpdateStockForm] = Form {
    mapping(
      "id" -> number,
      "product_id" -> number,
      "quantity" -> number
    )(UpdateStockForm.apply)(UpdateStockForm.unapply)
  }

  def add: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.stock.add(stockForm))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    stockForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.stock.add(errorForm))
        )
      },
      stock => {
        stockRepo.add(stock.product_id, stock.quantity).map { _ =>
          Redirect(routes.StockController.add()).flashing("success" -> "Stock added")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    stockRepo.list().map(s => Ok(views.html.stock.list(s)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val st: Future[Option[Stock]] = stockRepo.details(id)
    st.map {
      case Some(s) => Ok(views.html.stock.details(s))
      case None => Redirect("/stocks/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    stockRepo.delete(id)
    Redirect("/stocks/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    stockRepo.details(id).map {
      case Some(s) => Ok(views.html.stock.update(updateStockForm.fill(UpdateStockForm(s.id, s.product_id, s.quantity))))
      case None => Redirect("/stocks/all")
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateStockForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.stock.update(errorForm))
        )
      },
      stock => {
        stockRepo.update(stock.id, Stock(stock.id, stock.product_id, stock.quantity)).map { _ =>
          Redirect(routes.StockController.update(stock.id: Int)).flashing("success" -> "Stock updated")
        }
      }
    )
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    stockRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    stockRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def addJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Stock].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { stock =>
      stockRepo.add(stock.product_id, stock.quantity)
      Ok(Json.obj("status" -> "OK", "message" -> "Stock created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Stock].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { stock =>
      stockRepo.update(id, stock)
      Ok(Json.obj("status" -> "OK", "message" -> "Stock updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    stockRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Stock deleted"))
  }
}

case class CreateStockForm(product_id: Int, quantity: Int)
case class UpdateStockForm(id: Int, product_id: Int, quantity: Int)