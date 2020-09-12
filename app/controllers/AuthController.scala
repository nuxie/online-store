package controllers

import akka.actor.{Actor, Props}
import play.api.i18n.I18nSupport
import com.mohiva.play.silhouette.api.{Env, Environment, EventBus, LoginEvent, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.api.actions._
import models.UserIdentity
import play.api.mvc.{AbstractController, AnyContent, InjectedController, MessagesAbstractController, MessagesControllerComponents}
import play.mvc.Controller
import silhouette.DefaultEnv

trait AuthController[E <: Env] extends InjectedController with I18nSupport {
  def silhouette: Silhouette[E]

  val system = akka.actor.ActorSystem("system")
  val listener = system.actorOf(Props(new Actor {
    def receive = {
      case e @ LoginEvent(identity: UserIdentity, request) => println(e)
      case e @ LogoutEvent(identity: UserIdentity, request) => println(e)
    }
  }))

  silhouette.env.eventBus.subscribe(listener, classOf[LoginEvent[UserIdentity]])
  silhouette.env.eventBus.subscribe(listener, classOf[LogoutEvent[UserIdentity]])

  def SecuredAction = silhouette.SecuredAction
  def UnsecuredAction = silhouette.UnsecuredAction
  def UserAwareAction = silhouette.UserAwareAction

  implicit def securedRequest2User[A](implicit request: SecuredRequest[E, A]): E#I = request.identity
  implicit def userAwareRequest2UserOpt[A](implicit request: UserAwareRequest[E, A]): Option[E#I] = request.identity
}