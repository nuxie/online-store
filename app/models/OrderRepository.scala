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

  class OrderTable(tag: Tag) extends Table[Order](tag, "ORDERS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def * = (id, userId) <> ((Order.apply _).tupled, Order.unapply)
  }

  val order = TableQuery[OrderTable]

  def create(userId: Int): Future[Order] = db.run {
    (order.map(o => o.userId)
      returning order.map(_.id)
      into { case (userId, id) => Order(id, userId) }
      ) += userId
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