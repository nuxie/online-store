package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class InvoiceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class InvoiceTable(tag: Tag) extends Table[Invoice](tag, "INVOICES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def orderId = column[Int]("ORDER_ID")
    def paymentDue = column[String]("PAYMENT_DUE")
    def * = (id, orderId, paymentDue) <> ((Invoice.apply _).tupled, Invoice.unapply)
  }

  private val invoice = TableQuery[InvoiceTable]

  def create(orderId: Int, paymentDue: String): Future[Invoice] = db.run {
    (invoice.map(i => (i.orderId, i.paymentDue))
      returning invoice.map(_.id)
      into { case ((orderId, paymentDue), id) => Invoice(id, orderId, paymentDue) }
      ) += (orderId, paymentDue)
  }

  def list(): Future[Seq[Invoice]] = db.run {
    invoice.result
  }

  def details(id: Int): Future[Option[Invoice]] = db.run {
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