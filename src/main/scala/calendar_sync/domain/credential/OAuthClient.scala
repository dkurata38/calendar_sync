package calendar_sync.domain.credential

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

trait OAuthClient {
  def refreshAccessToken(credential: Credential)
                        (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[RefreshedAccessToken]
}
