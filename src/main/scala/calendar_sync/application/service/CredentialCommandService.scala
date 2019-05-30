package calendar_sync.application.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.domain.credential.{Credential, CredentialRepository, OAuthClient, RefreshedAccessToken}
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CredentialCommandService @Inject() (private val credentialRepository: CredentialRepository, private val oauthClient: OAuthClient) {
  def refresh(credential: Credential)
             (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[RefreshedAccessToken] =
    oauthClient.refreshAccessToken(credential)

  def update(credential: Credential): Either[Throwable, Unit] =
    credentialRepository.update(credential).toEither
}
