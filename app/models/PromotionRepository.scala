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

  class PromotionTable(tag: Tag) extends Table[Promotion](tag, "PROMOTIONS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def flagActive = column[Int]("FLAG_ACTIVE")
    def productId = column[Int]("PRODUCT_ID")
    def percentageSale = column[Int]("PERCENTAGE_SALE")
    def * = (id, name, flagActive, productId, percentageSale) <> ((Promotion.apply _).tupled, Promotion.unapply)
  }
  private val promotion = TableQuery[PromotionTable]

  def add(name: String, flagActive: Int, productId: Int, percentageSale: Int): Future[Promotion] = db.run {
    (promotion.map(p => (p.name, p.flagActive, p.productId, p.percentageSale))
      returning promotion.map(_.id)
      into { case ((name, flagActive, productId, percentageSale), id) => Promotion(id, name, flagActive, productId, percentageSale) }
      ) += (name, flagActive, productId, percentageSale)
  }

  def list(): Future[Seq[Promotion]] = db.run {
    promotion.result
  }

  def details(id: Int): Future[Option[Promotion]] = db.run {
    promotion.filter(_.id === id).result.headOption
  }

  def promoActiveProduct(productId: Int): Future[Option[Promotion]] = db.run {
    //getById, in this case product id, promo must be active, getting the first result
    promotion.filter(_.productId === productId).filter(_.flagActive === 1).result.headOption
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