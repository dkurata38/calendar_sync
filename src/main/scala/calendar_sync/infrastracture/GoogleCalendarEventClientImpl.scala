package calendar_sync.infrastracture

import java.io.{File, InputStreamReader}
import java.time.ZoneId
import java.util.Collections

import calendar_sync.domain.{Date, Duration, Event, GoogleCalendarEventClient}
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets, GoogleCredential}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.model.Events
import com.google.api.services.calendar.{Calendar, CalendarScopes, model}
import com.typesafe.config.ConfigFactory
import javax.inject.Singleton

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

@Singleton
class GoogleCalendarEventClientImpl extends GoogleCalendarEventClient{
  private val jsonFactory = JacksonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
  private val scopes = Collections.singletonList(CalendarScopes.CALENDAR)
  /**
    * https://developers.google.com/calendar/v3/reference/events/list
    * calendarIdで指定されたCalendarについて, startDateで指定した日付からendDateで指定した日付までの予定を取得する.
    * @param calendarId 予定を取得するCalendarのID
    * @param duration 取得する予定の範囲
    * @return
    */
  def getEventsByCalendarId(calendarId: String, duration: Duration): Try[Seq[Event]] = {
    val maybeCredential = getCredentials
    val maybeApplicationName = Try(ConfigFactory.load().getString("api.application-name"))

    val maybeService = for {
      credential <- maybeCredential
      applicationName <- maybeApplicationName
    } yield {
      new Calendar.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(applicationName)
        .build()
    }

    maybeService.flatMap{service =>

      @scala.annotation.tailrec
      def extractEventsRecursive(accum: Seq[Event] = Nil, maybePageToken: Option[String] = None): Try[Seq[Event]] = {
        val maybeResponse = Try(service.events()
          .list(calendarId)
          .setTimeMin(duration.start.toGoogleDateTime)
          .setTimeMax(duration.end.toGoogleDateTime)
          .setPageToken(maybePageToken.orNull)
          .execute())

        maybeResponse match {
          case Success(GoogleEventResponse(events, None)) => Success(accum ++ events.map(Event))
          case Success(GoogleEventResponse(events, nextPageToken)) => extractEventsRecursive(accum ++ events.map(Event), nextPageToken)
          case Failure(exception) => Failure(exception)
        }
      }
      extractEventsRecursive()
    }
  }

  private def getCredentials: Try[Credential] = {
    Try(GoogleClientSecrets.load(jsonFactory,
      new InputStreamReader(getClass.getResourceAsStream("/token/client_secrets.json"))))
      .map(clientSecret =>
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecret, scopes)
          .setAccessType("offline")
          .setDataStoreFactory(new FileDataStoreFactory(new File(getClass.getClassLoader.getResource("token").toURI)))
          .build())
      .map(authFlow => new AuthorizationCodeInstalledApp(authFlow, new LocalServerReceiver()).authorize("user"))
  }

  implicit class GoogleDateTime(date: Date) {
    def toGoogleDateTime = new DateTime(date.value.atStartOfDay(ZoneId.systemDefault()).toInstant.toEpochMilli)
  }

  
  object GoogleEventResponse {
    def unapply(events: Events): Option[(Seq[model.Event], Option[String])] = {
      Some((events.getItems.asScala, Option(events.getNextPageToken)))
    }
  }

  /**
    * https://developers.google.com/calendar/v3/reference/events/insert
    * @param calendarId 予定を追加する対象のカレンダーID
    * @param event 追加する予定
    */
  def create(calendarId: String, event: Event) = {
    val maybeCredential = getCredentials
    val maybeApplicationName = Try(ConfigFactory.load().getString("api.application-name"))

    val maybeService = for {
      credential <- maybeCredential
      applicationName <- maybeApplicationName
    } yield {
      new Calendar.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(applicationName)
        .build()
    }
    maybeService.map(service => Event(service.events().insert(calendarId, event.value).execute()))
  }

  def delete(calendarId: String, eventId: String) = {
    val maybeCredential = getCredentials
    val maybeApplicationName = Try(ConfigFactory.load().getString("api.application-name"))

    val maybeService = for {
      credential <- maybeCredential
      applicationName <- maybeApplicationName
    } yield {
      new Calendar.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(applicationName)
        .build()
    }

    maybeService.map(service => service.events().delete(calendarId, eventId).execute())
  }
}