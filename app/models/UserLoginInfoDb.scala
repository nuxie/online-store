package models

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.Tag

case class UserLoginInfoDb(userId: String, loginInfoId: String)

class UserLoginInfoTable(tag: Tag) extends Table[UserLoginInfoDb](tag, "USERLOGININFO") {
  def userId = column[String]("USER_ID")
  def loginInfoId = column[String]("LOGININFO_ID")
  override def * = (userId, loginInfoId) <> ((UserLoginInfoDb.apply _).tupled, UserLoginInfoDb.unapply)
}