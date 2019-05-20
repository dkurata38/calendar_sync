package calendar_sync.application.coordinator

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import calendar_sync.application.service.CredentialQueryService
import javax.inject.{Inject, Singleton}

import scala.io.{Codec, Source}

@Singleton
class StoreCredentialCoordinator @Inject()(private val credentialQueryService: CredentialQueryService) {
  def store() = {
    credentialQueryService.getCredentialWithOAuth
      .map{credential =>

        val templateLines = Source
          .fromInputStream(getClass.getResourceAsStream("/access_token_template.json"))(Codec.UTF8)
          .getLines()

        val lines = templateLines
          .map(templateLine => templateLine.replace("${id}", "user"))
          .map(templateLine => templateLine.replace("${access_token}", credential.accessToken))
          .map(templateLine => templateLine.replace("${expires_in}", credential.expiresIn.toString))
          .map(templateLine => templateLine.replace("${token_type}", credential.tokenType))
          .map(templateLine => templateLine.replace("${refresh_token}", credential.refreshToken))

        val url = getClass.getResource("/token")
        val accessTokenFile = Paths.get(url.toURI)
          .resolve("access_token.json")
          .toFile

        if (accessTokenFile.exists()) {
          accessTokenFile.delete()
        }
        accessTokenFile.createNewFile()
        val writer = new PrintWriter(accessTokenFile)
        lines.foreach(line => writer.write(line + System.lineSeparator()))
        writer.close()
      }
      .left.foreach(exception => exception.printStackTrace())
  }
}
