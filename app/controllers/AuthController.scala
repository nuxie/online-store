package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.{Env, LoginEvent, LogoutEvent, Silhouette}
import models.UserIdentity
import play.api.i18n.I18nSupport
import play.api.mvc.{AnyContent, InjectedController}

trait AuthController[E <: Env] extends InjectedController with I18nSupport {
  def silhouette: Silhouette[E]

  val system: ActorSystem = akka.actor.ActorSystem("system")
  val listener: ActorRef = system.actorOf(Props(new Actor {
    def receive: Receive = {
      case e @ LoginEvent(i: UserIdentity, _) => {
        println("Somebody logged in:")
        println(e)
        println(i)
      }
      case e @ LogoutEvent(i: UserIdentity, _) => {
        println("Somebody logged out:")
        println(e)
        println(i)
      }
    }
  }))

  silhouette.env.eventBus.subscribe(listener, classOf[LoginEvent[UserIdentity]])
  silhouette.env.eventBus.subscribe(listener, classOf[LogoutEvent[UserIdentity]])

  def securedAction: SecuredActionBuilder[E, AnyContent] = silhouette.SecuredAction
  def unsecuredAction: UnsecuredActionBuilder[E, AnyContent] = silhouette.UnsecuredAction
  def userAwareAction: UserAwareActionBuilder[E, AnyContent] = silhouette.UserAwareAction

  implicit def securedRequest2User[A](implicit request: SecuredRequest[E, A]): E#I = request.identity
  implicit def userAwareRequest2UserOpt[A](implicit request: UserAwareRequest[E, A]): Option[E#I] = request.identity
}