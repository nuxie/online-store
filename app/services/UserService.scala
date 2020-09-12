package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.UserIdentity

import scala.concurrent.Future

trait UserService extends IdentityService[UserIdentity] {
  def save(user: UserIdentity, loginInfo: LoginInfo): Future[UserIdentity]
}