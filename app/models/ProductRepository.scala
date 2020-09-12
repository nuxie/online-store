package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductTable(tag: Tag) extends Table[Product](tag, "PRODUCTS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def description = column[String]("DESCRIPTION")
    def categoryId = column[Int]("CATEGORY_ID")
    def price = column[Long]("PRICE")
    def * = (id, name, description, categoryId, price) <> ((Product.apply _).tupled, Product.unapply)
  }
  val product = TableQuery[ProductTable]

  def add(name: String, description: String, categoryId: Int, price: Long): Future[Product] = db.run {
    (product.map(p => (p.name, p.description, p.categoryId, p.price))
      returning product.map(_.id)
      into { case ((name, description, categoryId, price), id) => Product(id, name, description, categoryId, price) }
      ) += (name, description, categoryId, price)
  }

  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def details(id: Int): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    product.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Product): Future[Product] = {
    val toUpdate: Product = updated.copy(id)
    db.run {
      product.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}