package models
import slick.lifted.TableQuery

trait AuthTables {
  val userTable = TableQuery[UserTable]
  val passwordInfoTable = TableQuery[PasswordInfoTable]
  val loginInfoTable = TableQuery[LoginInfoTable]
  val userLoginInfoTable = TableQuery[UserLoginInfoTable]
  val oauth2InfoTable = TableQuery[OAuth2InfoTable]
}