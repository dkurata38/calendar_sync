package calendar_sync.application.coordinator

import calendar_sync.application.service.{CredentialCommandService, CredentialQueryService}
import calendar_sync.domain.credential.RefreshedAccessToken
import javax.inject.{Inject, Singleton}

@Singleton
class RefreshCredentialCoordinator @Inject() (private val credentialQueryService: CredentialQueryService,
                                              private val credentialCommandService: CredentialCommandService) {
  def refresh: Either[Throwable, RefreshedAccessToken] = {
      credentialQueryService.get
        .flatMap(maybeCredential => maybeCredential.toRight(new RuntimeException("認証情報が存在しません.")))
        .flatMap{credential =>
          credentialCommandService.refresh(credential)
        }
  }
}
