package calendar_sync.infrastracture.google.client_secrets

import com.typesafe.config.ConfigFactory

case class ClientSecrets(clientId: String, clientSecret: String)

object ClientSecrets {
  private val config = ConfigFactory.load()
  def load: ClientSecrets = {
    ClientSecrets(config.getString("api.client-id"), config.getString("api.client-secrets"))
  }
}
