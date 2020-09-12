package models

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext

@Singleton
class AppUserDaoImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                              (implicit ec: ExecutionContext)
  extends AppUserDao with AuthenticationsTables {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def save(user: UserIdentity) = db.run {
    val dbUser = User(user.id, user.email, user.firstName, user.lastName, user.role)
    userTable.insertOrUpdate(dbUser).map(_ => user)
  }

  def update(user: UserIdentity) = db.run {
    val dbUser = User(user.id, user.email, user.firstName, user.lastName, user.role)
    userTable.filter(_.id === user.id).update(dbUser).map(_ => user)
  }

  def find(loginInfo: LoginInfo) = {
    val findLoginInfoQuery = loginInfoTable.filter(dbLoginInfo =>
      dbLoginInfo.providerId === loginInfo.providerID &&  dbLoginInfo.providerKey === loginInfo.providerKey)
    val query = for {
      dbLoginInfo <- findLoginInfoQuery
      dbUserLoginInfo <- userLoginInfoTable.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- userTable.filter(_.id === dbUserLoginInfo.userId)
    } yield dbUser
    db.run(query.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        UserIdentity(user.id, user.email, user.firstName, user.lastName, user.role)
      }
    }
  }
}