package calendar_sync.application.service

import calendar_sync.domain.credential.{Credential, CredentialRepository, OAuthClient, RefreshedAccessToken}
import javax.inject.{Inject, Singleton}

@Singleton
class CredentialCommandService @Inject() (private val credentialRepository: CredentialRepository, private val oauthClient: OAuthClient) {
  def refresh(credential: Credential): Either[Throwable, RefreshedAccessToken] =
    oauthClient.refreshAccessToken(credential).toEither

  def update(credential: Credential): Either[Throwable, Unit] =
    credentialRepository.update(credential).toEither
}
