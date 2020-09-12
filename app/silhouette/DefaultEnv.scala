package silhouette

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.UserIdentity

trait DefaultEnv extends Env {
  type I = UserIdentity
  type A = JWTAuthenticator
}
