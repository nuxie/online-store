package models

import java.util.UUID
import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LoginInfoDaoImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends LoginInfoDao with AuthTables {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  override def saveUserLoginInfo(userID: String, loginInfo: LoginInfo): Future[Unit] = {
    val id = UUID.randomUUID().toString
    val dbLoginInfo = LoginInfoDb(id, loginInfo.providerID, loginInfo.providerKey)

    val actions = for {
      _ <- loginInfoTable += dbLoginInfo
      userLoginInfo = UserLoginInfoDb(userID, dbLoginInfo.id)
      _ <- userLoginInfoTable += userLoginInfo
    } yield ()
    db.run(actions)
  }

  def checkEmailIsAlreadyInUse(email: String): Future[Boolean] = db.run {
    userTable.filter(_.email === email)
      .exists
      .result
  }

  def getAuthenticationProviders(email: String): Future[Seq[String]] = {
    val action = for {
      ((_, _), loginInfo) <- userTable.filter(_.email === email)
        .join(userLoginInfoTable).on(_.id === _.userId)
        .join(loginInfoTable).on(_._2.loginInfoId === _.id)
    } yield loginInfo.providerId
    db.run(action.result)
  }
}