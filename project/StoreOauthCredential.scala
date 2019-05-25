import sbt._
import java.io.File

import sbt.Keys.{clean, resourceDirectory, update}
import sbt.{settingKey, taskKey}

object StoreOauthCredential {
  val google = taskKey[Unit]("Create json file for put access token to dyanmodb.")
  val clientSecretsJson = settingKey[File]("Google API consoleで生成したOAuthClientのJSONファイルのパス")
  val scopes = settingKey[Seq[String]]("OAuthで取得する権限のスコープ")

  google := {
    import java.io.PrintWriter
    import java.nio.file.{Files, Paths}

    import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
    import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
    import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
    import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
    import com.google.api.client.http.HttpTransport
    import com.google.api.client.json.JsonFactory
    import com.google.api.client.json.jackson2.JacksonFactory
    import com.google.api.client.util.store.FileDataStoreFactory
    import scala.collection.JavaConverters.asJavaCollectionConverter
    import scala.io.{Codec, Source}
    import scala.util.Try


    update.value
    clean.value
    val tmpPath = (resourceDirectory in Compile).value / "token"
    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance
    val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    Try(GoogleClientSecrets.load(jsonFactory, Files.newBufferedReader(Paths.get(clientSecretsJson.value.toURI))))
      .map(clientSecret =>
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecret, scopes.value.asJavaCollection)
          .setAccessType("offline")
          .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tmpPath.toURI)))
          .build())
      .map(authFlow => new AuthorizationCodeInstalledApp(authFlow, new LocalServerReceiver()).authorize("user"))
      .map{credential =>

        val templatePath = (resourceDirectory in Compile).value / "store_token_template.json"

        val templateLines = Source
          .fromFile(templatePath.toURI, Codec.UTF8.name)
          .getLines()

        val lines = templateLines
          .map(templateLine => templateLine.replace("${id}", "user"))
          .map(templateLine => templateLine.replace("${access_token}", credential.getAccessToken))
          .map(templateLine => templateLine.replace("${expires_in}", credential.getExpiresInSeconds.toString))
          .map(templateLine => templateLine.replace("${refresh_token}", credential.getRefreshToken))

        val savePath = (resourceDirectory in Compile).value / "token" / "access_token.json"
        val accessTokenFile = new File(savePath.toURI)

        if (accessTokenFile.exists()) {
          println(accessTokenFile.getAbsolutePath + "を削除します.")
          accessTokenFile.delete()
        }
        accessTokenFile.createNewFile()
        val writer = new PrintWriter(accessTokenFile)
        lines.foreach(line => writer.write(line + System.lineSeparator()))
        writer.close()
        println(accessTokenFile.getAbsolutePath + "に認証情報を保存しました.")
      }
      .failed.foreach(exception => exception.printStackTrace())
  }
}
