package controllers

import javax.inject._
import models.{User, UserRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: MessagesControllerComponents, userRepo: UserRepository)
                              (implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "e_mail" -> email,
      "tax_number" -> optional(number),
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }
  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "e_mail" -> email,
      "tax_number" -> optional(number),
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def create: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.user.add(userForm))
  }

  def createHandle: Action[AnyContent] = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user.add(errorForm))
        )
      },
      user => {
        userRepo.create(user.name, user.e_mail, user.tax_number).map { _ =>
          Redirect(routes.UserController.create()).flashing("success" -> "User created")
        }
      }
    )
  }

  def list: Action[AnyContent] = Action.async { implicit request =>
    userRepo.list().map(u => Ok(views.html.user.list(u)))
  }

  def details(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val usr: Future[Option[User]] = userRepo.details(id)
    usr.map {
      case Some(u) => Ok(views.html.user.details(u))
      case None => Redirect("/users/all")
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    userRepo.delete(id)
    Redirect("/users/all")
  }

  def update(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepo.details(id).map {
      case Some(u) => Ok(views.html.user.update(updateUserForm.fill(UpdateUserForm(u.id, u.name, u.e_mail, u.tax_number))))
      case None => Redirect("/users/all")
    }
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateUserForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.user.update(errorForm))
        )
      },
      user => {
        userRepo.update(user.id, User(user.id, user.name, user.e_mail, user.tax_number)).map { _ =>
          Redirect(routes.UserController.update(user.id: Int)).flashing("success" -> "User updated")
        }
      }
    )
  }
}

case class CreateUserForm(name: String, e_mail: String, tax_number: Option[Int])
case class UpdateUserForm(id: Int, name: String, e_mail: String, tax_number: Option[Int])