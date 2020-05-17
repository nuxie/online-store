package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InvoiceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class InvoiceTable(tag: Tag) extends Table[Invoice](tag, "invoices") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def order_id = column[Int]("order_id")
    def payment_due = column[String]("payment_due")

    def * = (id, order_id, payment_due) <> ((Invoice.apply _).tupled, Invoice.unapply)
  }

  private val invoice = TableQuery[InvoiceTable]

  def create(order_id: Int, payment_due: String): Future[Invoice] = db.run {
    (invoice.map(i => (i.order_id, i.payment_due))
      returning invoice.map(_.id)
      into { case ((order_id, payment_due), id) => Invoice(id, order_id, payment_due) }
      ) += (order_id, payment_due)
  }

  def list(): Future[Seq[Invoice]] = db.run {
    invoice.result
  }

  def details(id: Int): Future[Option[Invoice]] = db.run { //getById
    invoice.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    invoice.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Invoice): Future[Invoice] = {
    val toUpdate: Invoice = updated.copy(id)
    db.run {
      invoice.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}