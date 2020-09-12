package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishlistRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class WishlistTable(tag: Tag) extends Table[Wishlist](tag, "WISHLISTS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def productId = column[Int]("PRODUCT_ID")
    def * = (id, userId, productId) <> ((Wishlist.apply _).tupled, Wishlist.unapply)
  }

  private val wishlist = TableQuery[WishlistTable]

  def add(userId: Int, productId: Int): Future[Wishlist] = db.run {
    (wishlist.map(w => (w.userId, w.productId))
      returning wishlist.map(_.id)
      into { case ((userId, productId), id) => Wishlist(id, userId, productId) }
      ) += (userId, productId)
  }

  def list(): Future[Seq[Wishlist]] = db.run {
    wishlist.result
  }

  def details(id: Int): Future[Option[Wishlist]] = db.run { //getById
    wishlist.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    wishlist.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Wishlist): Future[Wishlist] = {
    val toUpdate: Wishlist = updated.copy(id)
    db.run {
      wishlist.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}