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

  class WishlistTable(tag: Tag) extends Table[Wishlist](tag, "wishlists") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Int]("user_id")
    def product_id = column[Int]("product_id")

    def * = (id, user_id, product_id) <> ((Wishlist.apply _).tupled, Wishlist.unapply)
  }

  private val wishlist = TableQuery[WishlistTable]

  def add(user_id: Int, product_id: Int): Future[Wishlist] = db.run {
    (wishlist.map(w => (w.user_id, w.product_id))
      returning wishlist.map(_.id)
      into { case ((user_id, product_id), id) => Wishlist(id, user_id, product_id) }
      ) += (user_id, product_id)
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