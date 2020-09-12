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

  class CartTable(tag: Tag) extends Table[Cart](tag, "CARTS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[String]("USER_ID")
    def productId = column[Int]("PRODUCT_ID")
    def quantity = column[Int]("QUANTITY")

    def * = (id, userId, productId, quantity) <> ((Cart.apply _).tupled, Cart.unapply)
  }

  private val cart = TableQuery[CartTable]

  def add(userId: String, productId: Int, quantity: Int): Future[Cart] = db.run {
    (cart.map(c => (c.userId, c.productId, c.quantity))
      returning cart.map(_.id)
      into { case ((userId, productId, quantity), id) => Cart(id, userId, productId, quantity) }
      )  += (userId, productId, quantity)
  }

  def list(): Future[Seq[Cart]] = db.run {
    cart.result
  }

  def detailsUser(uid: String): Future[Seq[Cart]] = db.run {
    cart.filter(_.userId === uid).result
  }

  def delete(uid: String): Future[Unit] = db.run {
    cart.filter(_.userId === uid)
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