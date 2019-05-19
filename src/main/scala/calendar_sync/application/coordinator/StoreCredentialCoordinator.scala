package calendar_sync.application.coordinator

import calendar_sync.application.service.CredentialQueryService
import javax.inject.{Inject, Singleton}

@Singleton
class StoreCredentialCoordinator @Inject()(private val credentialQueryService: CredentialQueryService) {
  def store() = {
    credentialQueryService.getCredentialWithOAuth
      .map(credential => println(credential.toString))
      .left.foreach(exception => exception.printStackTrace())
  }
}
