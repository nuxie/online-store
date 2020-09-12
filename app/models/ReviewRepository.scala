package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ReviewTable(tag: Tag) extends Table[Review](tag, "reviews") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def product_id = column[Int]("product_id")
    def description = column[String]("description")

    def * = (id, product_id, description) <> ((Review.apply _).tupled, Review.unapply)
  }

  private val review = TableQuery[ReviewTable]

  def create(product_id: Int, description: String): Future[Review] = db.run {
    (review.map(r => (r.product_id, r.description))
      returning review.map(_.id)
      into { case ((product_id, description), id) => Review(id, product_id, description) }
      ) += (product_id, description)
  }

  def list(): Future[Seq[Review]] = db.run {
    review.result
  }

  def listProduct(product_id: Int): Future[Seq[Review]] = db.run {
    review.filter(_.product_id === product_id).result
  }


  def details(id: Int): Future[Option[Review]] = db.run { //getById
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