package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class StockTable(tag: Tag) extends Table[Stock](tag, "STOCK") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def productId = column[Int]("PRODUCT_ID")
    def quantity = column[Int]("QUANTITY")
    def * = (id, productId, quantity) <> ((Stock.apply _).tupled, Stock.unapply)
  }

  private val stock = TableQuery[StockTable]

  def add(productId: Int, quantity: Int): Future[Stock] = db.run {
    (stock.map(s => (s.productId, s.quantity))
      returning stock.map(_.id)
      into { case ((productId, quantity), id) => Stock(id, productId, quantity) }
      ) += (productId, quantity)
  }

  def list(): Future[Seq[Stock]] = db.run {
    stock.result
  }

  def details(id: Int): Future[Option[Stock]] = db.run { //getById - in this case productID
    stock.filter(_.productId === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    stock.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Stock): Future[Stock] = {
    val toUpdate: Stock = updated.copy(id)
    db.run {
      stock.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}