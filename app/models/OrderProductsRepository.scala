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

  class OrderProductsTable(tag: Tag) extends Table[OrderProducts](tag, "orders_products") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def order_id = column[Int]("order_id")
    def product_id = column[Int]("product_id")
    def quantity = column[Int]("quantity")

    def * = (id, order_id, product_id, quantity) <> ((OrderProducts.apply _).tupled, OrderProducts.unapply)
  }

  private val orderProducts = TableQuery[OrderProductsTable]

  def add(order_id: Int, product_id: Int, quantity: Int): Future[OrderProducts] = db.run {
    (orderProducts.map(op => (op.order_id, op.product_id, op.quantity))
      returning orderProducts.map(_.id)
      into { case ((order_id, product_id, quantity), id) => OrderProducts(id, order_id, product_id, quantity) }
      )  += (order_id, product_id, quantity)
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