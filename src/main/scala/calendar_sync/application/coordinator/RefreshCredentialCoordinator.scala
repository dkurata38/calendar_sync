package calendar_sync.application.coordinator

import calendar_sync.application.service.{CredentialCommandService, CredentialQueryService}
import javax.inject.{Inject, Singleton}

@Singleton
class RefreshCredentialCoordinator @Inject() (private val credentialQueryService: CredentialQueryService,
                                              private val credentialCommandService: CredentialCommandService) {
  def refresh: Either[Throwable, Unit] = {
    val maybeCredential = credentialQueryService.get
      .flatMap(maybeCredential => maybeCredential.toRight(new RuntimeException("認証情報が存在しません.")))
    for {
      credential <- maybeCredential
      refreshedToken <- credentialCommandService.refreshAccessToken(credential)
    } yield credentialCommandService.update(credential.refresh(refreshedToken))
  }
}
