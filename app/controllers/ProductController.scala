package controllers

import javax.inject._
import models.{Product, ExtendedProduct, Promotion, Stock, Category, CategoryRepository, ProductRepository, StockRepository, PromotionRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductController @Inject()(cc: MessagesControllerComponents, productRepo: ProductRepository, categoryRepo: CategoryRepository, stockRepo: StockRepository, promoRepo: PromotionRepository)
                                 (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category_id" -> number,
      "price" -> longNumber
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }
  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category_id" -> number,
      "price" -> longNumber
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def create: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.product.add(productForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product.add(errorForm))
        )
      },
      product => {
        productRepo.add(product.name, product.description, product.category_id, product.price).map { _ =>
          Redirect(routes.ProductController.create()).flashing("success" -> "Product created")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    productRepo.list().map(i => Ok(views.html.product.list(i)))
  }

  def extendedList: Action[AnyContent] = Action.async { implicit request =>
    productRepo.list()
      .map(p =>
        p.map(p => extendedDetailsHelper(p))
      ).flatMap(extendedProducts => Future.sequence(extendedProducts).map(i => Ok(views.html.product.extendedList(i))))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val prod: Future[Option[Product]] = productRepo.details(id)
    prod.map {
      case Some(p) => Ok(views.html.product.details(p))
      case None => Redirect("/products/all")
    }
  }

  def extendedDetailsHelper(p: Product): Future[ExtendedProduct] = {
    promoRepo.details(p.id).map(promo => {ExtendedProduct(1,"name","desc",1,1,"cat",23,25)})
    categoryRepo.details(p.category_id).flatMap(cat =>
      stockRepo.details(p.id).flatMap(stock =>
        promoRepo.promoActiveProduct(p.id).map(promo => {
          ExtendedProduct(p.id, p.name, p.description, p.category_id, p.price,
            cat.getOrElse(Category(0,"None")).name, stock.getOrElse(Stock(0,p.id,0)).quantity, promo.getOrElse(Promotion(0,"none",1,p.id,0)).percentage_sale)
        })))
  }

  def delete(id: Int): Action[AnyContent] = Action {
    productRepo.delete(id)
    Redirect("/products/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    productRepo.details(id).map {
      case Some(p) => Ok(views.html.product.update(updateProductForm.fill(UpdateProductForm(p.id, p.name, p.description,
        p.category_id, p.price))))
      case None => Redirect("/products/all")
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.product.update(errorForm))
        )
      },
      product => {
        productRepo.update(product.id, Product(product.id, product.name, product.description, product.category_id, product.price)).map { _ =>
          Redirect(routes.ProductController.update(product.id: Int)).flashing("success" -> "Product updated")
        }
      }
    )
  }

  def detailsJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    productRepo.details(id).map {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.obj(
        "status" -> "Error",
        "message" -> "Not found"
      ))
    }
  }

  def listJSON: Action[AnyContent] = Action.async { implicit request =>
    productRepo.list().map(p =>
      Ok(Json.toJson(p))
    )
  }


  def extendedListJSON: Action[AnyContent] = Action.async { implicit request =>
    productRepo.list()
      .map(p =>
        p.map(p => extendedDetailsHelper(p))
      ).flatMap(extendedProducts => Future.sequence(extendedProducts))
      .map(extendedProducts => Json.toJson(extendedProducts))
      .map(json => Ok(json))
  }

  def addJSON(): Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Product].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { product =>
      productRepo.add(product.name, product.description, product.category_id, product.price)
      Ok(Json.obj("status" -> "OK", "message" -> "Product created"))
    })
  }

  def updateJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    request.body.validate[Product].fold({ errors =>
      BadRequest(Json.obj(
        "status" -> "Error",
        "message" -> "Bad JSON"
      ))
    }, { product =>
      productRepo.update(id, product)
      Ok(Json.obj("status" -> "OK", "message" -> "Product updated"))
    })
  }

  def deleteJSON(id: Int): Action[JsValue] = Action(parse.json) {  request =>
    productRepo.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> "Product deleted"))
  }
}

case class CreateProductForm(name: String, description: String, category_id: Int, price: Long)
case class UpdateProductForm(id: Int, name: String, description: String, category_id: Int, price: Long)