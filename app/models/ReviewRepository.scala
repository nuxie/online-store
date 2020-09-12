package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ReviewTable(tag: Tag) extends Table[Review](tag, "REVIEWS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def productId = column[Int]("PRODUCT_ID")
    def description = column[String]("DESCRIPTION")
    def * = (id, productId, description) <> ((Review.apply _).tupled, Review.unapply)
  }

  private val review = TableQuery[ReviewTable]

  def create(productId: Int, description: String): Future[Review] = db.run {
    (review.map(r => (r.productId, r.description))
      returning review.map(_.id)
      into { case ((productId, description), id) => Review(id, productId, description) }
      ) += (productId, description)
  }

  def list(): Future[Seq[Review]] = db.run {
    review.result
  }

  def listProduct(productId: Int): Future[Seq[Review]] = db.run {
    review.filter(_.productId === productId).result
  }

  def details(id: Int): Future[Option[Review]] = db.run {
    review.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    review.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Review): Future[Review] = {
    val toUpdate: Review = updated.copy(id)
    db.run {
      review.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}