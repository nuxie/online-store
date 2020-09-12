package models

import slick.jdbc.SQLiteProfile.api._
import slick.lifted.{Tag}

case class LoginInfoDb(id: String,
                       providerId: String,
                       providerKey: String)

class LoginInfoTable(tag: Tag) extends Table[LoginInfoDb](tag, "LoginInfo") {
  def id = column[String]("Id", O.PrimaryKey, O.Unique)

  def providerId = column[String]("ProviderId")

  def providerKey = column[String]("ProviderKey")

  override def * = (id, providerId, providerKey) <> ((LoginInfoDb.apply _).tupled, LoginInfoDb.unapply)
}
