package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "orders") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Int]("user_id")

    def * = (id, user_id) <> ((Order.apply _).tupled, Order.unapply)
  }

  val order = TableQuery[OrderTable]

  def create(user_id: Int): Future[Order] = db.run {
    (order.map(o => o.user_id)
      returning order.map(_.id)
      into { case (user_id, id) => Order(id, user_id) }
      ) += user_id
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }

  def details(id: Int): Future[Option[Order]] = db.run { //getById
    order.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    order.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: Order): Future[Order] = {
    val toUpdate: Order = updated.copy(id)
    db.run {
      order.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}