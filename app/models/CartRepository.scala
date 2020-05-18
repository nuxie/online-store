package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CartTable(tag: Tag) extends Table[Cart](tag, "carts") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Int]("user_id")
    def product_id = column[Int]("product_id")
    def quantity = column[Int]("quantity")

    def * = (id, user_id, product_id, quantity) <> ((Cart.apply _).tupled, Cart.unapply)
  }

  private val cart = TableQuery[CartTable]

  def add(user_id: Int, product_id: Int, quantity: Int): Future[Cart] = db.run {
    (cart.map(op => (op.user_id, op.product_id, op.quantity))
      returning cart.map(_.id)
      into { case ((user_id, product_id, quantity), id) => Cart(id, user_id, product_id, quantity) }
      )  += (user_id, product_id, quantity)
  }

  def list(): Future[Seq[Cart]] = db.run {
    cart.result
  }

  def details(id: Int): Future[Option[Cart]] = db.run { //getById
    cart.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    cart.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Cart): Future[Cart] = {
    val toUpdate: Cart = updated.copy(id)
    db.run {
      cart.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}