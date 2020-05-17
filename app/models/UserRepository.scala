package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def e_mail = column[String]("e_mail")
    def tax_number = column[Option[Int]]("tax_number")

    def * = (id, name, e_mail, tax_number) <> ((User.apply _).tupled, User.unapply)
  }

  private val user = TableQuery[UserTable]

  def create(name: String, e_mail: String, tax_number: Option[Int]): Future[User] = db.run {
    (user.map(u => (u.name, u.e_mail, u.tax_number))
      returning user.map(_.id)
      into { case ((name, e_mail, tax_number), id) => User(id, name, e_mail, tax_number) }
      ) += (name, e_mail, tax_number)
  }

  def list(): Future[Seq[User]] = db.run {
    user.result
  }

  def details(id: Int): Future[Option[User]] = db.run { //getById
    user.filter(_.id === id).result.headOption
  }

  def delete(id: Int): Future[Unit] = db.run {
    user.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def update(id: Int, updated: User): Future[User] = {
    val toUpdate: User = updated.copy(id)
    db.run {
      user.filter(_.id === id)
        .update(toUpdate)
        .map(_ => toUpdate)
    }
  }
}