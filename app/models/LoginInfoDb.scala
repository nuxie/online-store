package models
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.Tag

case class LoginInfoDb(id: String,
                       providerId: String,
                       providerKey: String)

class LoginInfoTable(tag: Tag) extends Table[LoginInfoDb](tag, "LOGININFO") {
  def id = column[String]("ID", O.PrimaryKey, O.Unique)
  def providerId = column[String]("PROVIDER_ID")
  def providerKey = column[String]("PROVIDER_KEY")

  override def * = (id, providerId, providerKey) <> ((LoginInfoDb.apply _).tupled, LoginInfoDb.unapply)
}
