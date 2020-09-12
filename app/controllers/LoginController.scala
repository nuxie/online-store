package controllers

import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.{Inject, Singleton}
import models.UserIdentity
import play.api.i18n.I18nSupport
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Cookie, InjectedController, MessagesAbstractController, MessagesControllerComponents}
import services.UserService
import silhouette.DefaultEnv

import scala.concurrent.{ExecutionContext, Future}

@Singleton
case class LoginController @Inject()(cc: MessagesControllerComponents,
                                silhouette: Silhouette[DefaultEnv],
                                userService: UserService,
                                credentialsProvider: CredentialsProvider)
                               (implicit ec: ExecutionContext)
  extends InjectedController with I18nSupport with AuthController[DefaultEnv] {

  def submit(): Action[JsValue] = silhouette.UnsecuredAction(parse.json).async { implicit request =>
    implicit val signInRead: Reads[SignInRequest] = (
      (__ \ "email").read[String] and
        (__ \ "password").read[String]
      ) (SignInRequest.apply _)
    println("submit ")
    val validation = request.body.validate[SignInRequest](signInRead)
    validation match {
      case e: JsError => Future(Status(BAD_REQUEST)(JsError.toJson(e)))
      case s: JsSuccess[SignInRequest] => {
        val data = s.value
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).map {
            case Some(user) => Success(user)
            case None => UserNotFound
          }
        }.recoverWith {
          case _: InvalidPasswordException => Future.successful(InvalidPassword)
          case _: IdentityNotFoundException => Future.successful(UserNotFound)
          case e => Future.failed(e)
        }.flatMap {
          case Success(user) => {
            val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            for {
              authenticator <- silhouette.env.authenticatorService.create(loginInfo)
              token <- silhouette.env.authenticatorService.init(authenticator)
              result <- silhouette.env.authenticatorService.embed(token, Ok(
                Json.obj(
                  "token" -> token,
                  "tokenExpiry" -> authenticator.expirationDateTime.getMillis,
                  "email" -> user.email,
                  "role" -> user.role,
                  "id" -> user.id
                )))
            } yield {
              silhouette.env.eventBus.publish(LoginEvent[UserIdentity](user, request))
              result
            }
          }
          case InvalidPassword =>
            Future.successful(Forbidden(Json.obj("msg" -> "invalid password")))
          case UserNotFound =>
            Future.successful(Forbidden(Json.obj("msg" -> "user not found")))
        }
      }
    }
  }

  def signOut: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.HomeController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  case class SignInRequest(email: String, password: String)
  sealed trait AuthenticateResult
  case class Success(user: UserIdentity) extends AuthenticateResult
  object InvalidPassword extends AuthenticateResult
  object UserNotFound extends AuthenticateResult

}
