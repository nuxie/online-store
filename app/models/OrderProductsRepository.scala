package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderProductsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderProductsTable(tag: Tag) extends Table[OrderProducts](tag, "ORDERS_PRODUCTS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def orderId = column[Int]("ORDER_ID")
    def productId = column[Int]("PRODUCT_ID")
    def quantity = column[Int]("QUANTITY")

    def * = (id, orderId, productId, quantity) <> ((OrderProducts.apply _).tupled, OrderProducts.unapply)
  }

  private val orderProducts = TableQuery[OrderProductsTable]

  def add(orderId: Int, productId: Int, quantity: Int): Future[OrderProducts] = db.run {
    (orderProducts.map(op => (op.orderId, op.productId, op.quantity))
      returning orderProducts.map(_.id)
      into { case ((orderId, productId, quantity), id) => OrderProducts(id, orderId, productId, quantity) }
      )  += (orderId, productId, quantity)
  }

  def list(): Future[Seq[OrderProducts]] = db.run {
    orderProducts.result
  }

  def details(id: Int): Future[Option[OrderProducts]] = db.run { //getById
    orderProducts.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    orderProducts.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: OrderProducts): Future[OrderProducts] = {
    val toUpdate: OrderProducts = updated.copy(id)
    db.run {
      orderProducts.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}