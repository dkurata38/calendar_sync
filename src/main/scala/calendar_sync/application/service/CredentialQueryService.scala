package calendar_sync.application.service

import calendar_sync.domain.credential.{Credential, CredentialRepository}
import javax.inject.{Inject, Singleton}

@Singleton
class CredentialQueryService @Inject() (private val credentialRepository: CredentialRepository){
  def get: Either[Throwable, Option[Credential]] = credentialRepository.get.toEither
}
