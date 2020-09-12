package controllers

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, SocialProviderRegistry}
import models.LoginInfoDao
import javax.inject.{Inject, Singleton}
import models.UserIdentity
import play.api.i18n.I18nSupport
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import services.UserService
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegisterController @Inject()(cc: MessagesControllerComponents,
                                   silhouette: Silhouette[DefaultEnv],
                                   userService: UserService,
                                   authInfoRepository: AuthInfoRepository,
                                   loginInfoDao: LoginInfoDao,
                                   passwordHasherRegistry: PasswordHasherRegistry)
                                  (implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc)
    with I18nSupport {

  val emptyStringMsg = "cannot be empty"

  def submit() = silhouette.UnsecuredAction(parse.json).async { implicit request =>
    implicit val signUpRead = (
      (JsPath \ "email").read[String].filter(JsonValidationError(emptyStringMsg))(x => x != null && !x.isEmpty) and
        (JsPath \ "firstName").read[String].filter(JsonValidationError(emptyStringMsg))(x => x != null && !x.isEmpty) and
        (JsPath \ "lastName").read[String].filter(JsonValidationError(emptyStringMsg))(x => x != null && !x.isEmpty) and
        (JsPath \ "password").read[String].filter(JsonValidationError(emptyStringMsg))(x => x != null && !x.isEmpty)
      ) (SignUpRequest.apply _)

    val validation = request.body.validate[SignUpRequest](signUpRead)
    validation match {
      case e: JsError => Future(Status(BAD_REQUEST)(JsError.toJson(e)))
      case s: JsSuccess[SignUpRequest] => {
        val data = s.value
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)

        loginInfoDao.checkEmailIsAlreadyInUse(data.email).flatMap(isInUse => {
          if(isInUse) Future(Status(CONFLICT)(s"email '${data.email}' is already in use"))
          else {
            val userToCreate = UserIdentity(email = s.value.email, firstName = s.value.firstName,
              lastName = s.value.lastName, role = "USER")
            for {
              user <- userService.save(userToCreate, loginInfo)
              authInfo = passwordHasherRegistry.current.hash(data.password)
              _ <- authInfoRepository.add(loginInfo, authInfo)

              authenticator <- silhouette.env.authenticatorService.create(loginInfo)
              token <- silhouette.env.authenticatorService.init(authenticator)
              result <- silhouette.env.authenticatorService.embed(
                token, Ok(Json.obj(
                  "token" -> token,
                  "email" -> data.email,
                  "expiryDatetime" -> authenticator.expirationDateTime.toString()
                )))
            } yield {
              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              result
            }
          }
        })
      }
    }
  }

  case class SignUpRequest(email: String, firstName: String, lastName: String, password: String)

}