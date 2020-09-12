package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PromotionRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PromotionTable(tag: Tag) extends Table[Promotion](tag, "promotions") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def flag_active = column[Int]("flag_active")
    def product_id = column[Int]("product_id")
    def percentage_sale = column[Int]("percentage_sale")

    def * = (id, name, flag_active, product_id, percentage_sale) <> ((Promotion.apply _).tupled, Promotion.unapply)
  }
  private val promotion = TableQuery[PromotionTable]

  def add(name: String, flag_active: Int, product_id: Int, percentage_sale: Int): Future[Promotion] = db.run {
    (promotion.map(p => (p.name, p.flag_active, p.product_id, p.percentage_sale))
      returning promotion.map(_.id)
      into { case ((name, flag_active, product_id, percentage_sale), id) => Promotion(id, name, flag_active, product_id, percentage_sale) }
      ) += (name, flag_active, product_id, percentage_sale)
  }

  def list(): Future[Seq[Promotion]] = db.run {
    promotion.result
  }

  def details(id: Int): Future[Option[Promotion]] = db.run { //getById
    promotion.filter(_.id === id).result.headOption
  }

  def promoActiveProduct(product_id: Int): Future[Option[Promotion]] = db.run { //getById, in this case product id, promo must be active, getting the first result
    promotion.filter(_.product_id === product_id).filter(_.flag_active === 1).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    promotion.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Promotion): Future[Promotion] = {
    val toUpdate: Promotion = updated.copy(id)
    db.run {
      promotion.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}