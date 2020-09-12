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

  class StockTable(tag: Tag) extends Table[Stock](tag, "stock") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def product_id = column[Int]("product_id")
    def quantity = column[Int]("quantity")

    def * = (id, product_id, quantity) <> ((Stock.apply _).tupled, Stock.unapply)
  }

  private val stock = TableQuery[StockTable]

  def add(product_id: Int, quantity: Int): Future[Stock] = db.run {
    (stock.map(s => (s.product_id, s.quantity))
      returning stock.map(_.id)
      into { case ((product_id, quantity), id) => Stock(id, product_id, quantity) }
      ) += (product_id, quantity)
  }

  def list(): Future[Seq[Stock]] = db.run {
    stock.result
  }

  def details(id: Int): Future[Option[Stock]] = db.run { //getById - in this case productID
    stock.filter(_.product_id === id).result.headOption
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