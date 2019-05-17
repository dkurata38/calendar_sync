package calendar_sync.infrastracture

import java.time.ZoneId
import java.util.Collections

import calendar_sync.domain.{Date, Duration, Event}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Events
import com.google.api.services.calendar.{Calendar, CalendarScopes}
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import pureconfig.generic.auto._

class GoogleCalendarEventClientImpl {
  private val jsonFactory = JacksonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
  private val scopes = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS)
  /**
    * https://developers.google.com/calendar/v3/reference/events/list
    * calendarIdで指定されたCalendarについて, startDateで指定した日付からendDateで指定した日付までの予定を取得する.
    * @param calendarId 予定を取得するCalendarのID
    * @param duration 取得する予定の範囲
    * @return
    */
  def getEventsByCalendarId(calendarId: String, duration: Duration): Try[Seq[Event]] = {
    val credential = GoogleCredential
      .fromStream(this.getClass.getResourceAsStream("/token/source-owner.json"))
      .createScoped(scopes)
    pureconfig.loadConfig[GoogleApiConfig](ConfigFactory.load().getConfig("api.from"))
      .map(Success(_))
      .left.map(failures => Failure(new RuntimeException(failures.toList.mkString("¥¥n"))))
      .merge
      .flatMap{config =>
        val service = new Calendar.Builder(httpTransport, jsonFactory, credential)
          .setApplicationName(config.applicationName)
          .build()

        @scala.annotation.tailrec
        def extractEventsRecursive(accum: Seq[Event] = Nil, calendarId: String, service: Calendar,
                                   timeMin: DateTime, timeMax: DateTime, maybePageToken: Option[String] = None): Try[Seq[Event]] = {

          val maybeResponse = Try(service.events()
            .list(calendarId)
            .setTimeMin(timeMin)
            .setTimeMax(timeMax)
            .setPageToken(maybePageToken.orNull)
            .execute())

          maybeResponse match {
            case Success(response) if response.getNextPageToken == null => Success(accum ++ response.getItems.asScala.map(Event))
            case Success(response) if response.getNextPageToken != null =>
              extractEventsRecursive(accum ++ response.getItems.asScala.map(Event), calendarId, service, timeMin, timeMax, Option(response.getNextPageToken))
            case Failure(exception) => Failure(exception)
          }
        }
        extractEventsRecursive(calendarId = calendarId, service = service,
          timeMin = duration.start.toGoogleDateTime, timeMax = duration.end.toGoogleDateTime)
      }
  }

  implicit class GoogleDateTime(date: Date) {
    def toGoogleDateTime = new DateTime(date.value.atStartOfDay(ZoneId.systemDefault()).toInstant.toEpochMilli)
  }

  @scala.annotation.tailrec
  final def getEventsRecursive(calendarId: String, service: Calendar,timeMin: DateTime, timeMax: DateTime, pageToken: Option[String] = None, events: Seq[Event] = Seq.empty): Seq[calendar_sync.domain.Event] = {
    val response = service.events()
      .list(calendarId)
      .setPageToken(pageToken.orNull).setTimeMin(timeMin).setTimeMax(timeMax)
      .execute()

    val queriedEvents = response.getItems.asScala.map(event => Event(event))
    if (pageToken == null) events ++ queriedEvents
    else getEventsRecursive(calendarId, service, timeMin, timeMax, Option(response.getNextPageToken), events ++ queriedEvents)
  }


  /**
    * https://developers.google.com/calendar/v3/reference/events/insert
    * @param calendarId 予定を追加する対象のカレンダーID
    * @param event 追加する予定
    */
  def create(calendarId: String, event: Event) = {
    val credential =
      GoogleCredential.fromStream(this.getClass.getResourceAsStream("/token/target-owner.json"))
        .createScoped(scopes)

    pureconfig.loadConfig[GoogleApiConfig](ConfigFactory.load().getConfig("api.to")).map{config =>
      val service = new Calendar.Builder(
        httpTransport,
        jsonFactory,
        credential
      )
        .setApplicationName(config.applicationName)
        .build()

      service.events().insert(calendarId, event.value)
    }
  }

  def delete(calendarId: String, eventId: String) = {
    val credential =
      GoogleCredential.fromStream(this.getClass.getResourceAsStream("/token/target-owner.json"))
        .createScoped(scopes)

    pureconfig.loadConfig[GoogleApiConfig](ConfigFactory.load().getConfig("api.to")).map{config =>
      val service = new Calendar.Builder(
        httpTransport,
        jsonFactory,
        credential
      )
      .setApplicationName(config.applicationName)
      .build()

      service.events().delete(calendarId, eventId)
    }
  }
}