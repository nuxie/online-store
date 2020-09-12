package models

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.Tag

case class UserLoginInfoDb(userId: String, loginInfoId: String)

class UserLoginInfoTable(tag: Tag) extends Table[UserLoginInfoDb](tag, "UserLoginInfo") {
  def userId = column[String]("UserId")

  def loginInfoId = column[String]("LoginInfoId")

  override def * = (userId, loginInfoId) <> ((UserLoginInfoDb.apply _).tupled, UserLoginInfoDb.unapply)
}