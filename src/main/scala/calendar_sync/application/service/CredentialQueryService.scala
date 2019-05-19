package calendar_sync.application.service

import calendar_sync.domain.{Credential, GoogleOAuthClient}

import scala.util.Either
import javax.inject.{Inject, Singleton}

@Singleton
class CredentialQueryService @Inject() (private val client: GoogleOAuthClient) {
    def getCredentialWithOAuth: Either[Throwable, Credential] = client.getCredential.toEither
}
