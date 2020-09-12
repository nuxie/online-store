package services

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.{AppUserDao, LoginInfoDao, UserIdentity}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl @Inject()(appUserDao: AppUserDao,
                                loginInfoDao: LoginInfoDao)
                               (implicit ec: ExecutionContext) extends UserService {

  override def retrieve(loginInfo: LoginInfo): Future[Option[UserIdentity]] = {
    appUserDao.find(loginInfo)
  }

  def save(user: UserIdentity, loginInfo: LoginInfo): Future[UserIdentity] = {
    appUserDao.find(loginInfo).flatMap {
      case Some(foundUser) => appUserDao.update(user.copy(role = foundUser.role))
      case None => add(user, loginInfo)
    }
  }

  def add(user: UserIdentity, loginInfo: LoginInfo): Future[UserIdentity] = {
    for {
      savedUser <- appUserDao.save(user)
      _ <- loginInfoDao.saveUserLoginInfo(savedUser.id, loginInfo)
    } yield savedUser
  }
}