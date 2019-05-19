package calendar_sync.infrastracture

import java.io.{File, InputStreamReader}

import calendar_sync.domain.{Credential, GoogleOAuthClient}
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.util.store.FileDataStoreFactory
import javax.inject.Singleton

import scala.util.Try

@Singleton
class GoogleOAuthClientImpl extends AbstractGoogleCalendarClient with GoogleOAuthClient{
  override def getCredential: Try[Credential] = {
    Try(GoogleClientSecrets.load(jsonFactory,
      new InputStreamReader(getClass.getResourceAsStream("/token/client_secrets.json"))))
      .map(clientSecret =>
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecret, scopes)
          .setAccessType("offline")
          .setDataStoreFactory(new FileDataStoreFactory(new File(getClass.getClassLoader.getResource("token").toURI)))
          .build())
      .map(authFlow => new AuthorizationCodeInstalledApp(authFlow, new LocalServerReceiver()).authorize("user"))
      .map(credential => Credential(credential.getAccessToken, "", credential.getExpirationTimeMilliseconds, credential.getRefreshToken))
  }
}
