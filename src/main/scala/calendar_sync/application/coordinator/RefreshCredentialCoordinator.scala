package calendar_sync.application.coordinator

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.application.service.{CredentialCommandService, CredentialQueryService}
import calendar_sync.domain.credential.RefreshedAccessToken
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RefreshCredentialCoordinator @Inject() (private val credentialQueryService: CredentialQueryService,
                                              private val credentialCommandService: CredentialCommandService) {
  def refresh(implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[RefreshedAccessToken] = {
    Future{
      credentialQueryService.get
        .flatMap(maybeCredential => maybeCredential.toRight(new RuntimeException("認証情報が存在しません.")))
        .map{credential =>
          credentialCommandService.refresh(credential)
        }
        .left.map(exception => Future.failed(exception))
        .merge
    }.flatten
  }
}
