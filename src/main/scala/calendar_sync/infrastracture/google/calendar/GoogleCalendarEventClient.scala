package calendar_sync.infrastracture.google.calendar

import java.time.ZoneId
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import calendar_sync.domain.Duration
import calendar_sync.domain.event.{CalendarEventClient, Event}
import calendar_sync.infrastracture.google.GoogleApiClient

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

class GoogleCalendarEventClient extends CalendarEventClient with GoogleApiClient{
  override def getEventsByCalendarId(calendarId: String, duration: Duration): Try[Seq[Event]] = {
    val timeMin = duration.start.value.atStartOfDay(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val timeMax = duration.end.value.atStartOfDay(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val getEndPoint = s"${resources(calendarId)}?timeMin=$timeMin&timeMax=$timeMax"

    val request = HttpRequest(HttpMethods.GET)
      .withUri(Uri(getEndPoint))
    val maybeEvents = Http().singleRequest(request)
      .transformWith{
        case Success(Response(StatusCodes.OK, maybeJsonString)) => maybeJsonString.flatMap(Unmarshal(_).to[GoogleEventResponse])
        case Success(Response(_, maybeJsonString)) => maybeJsonString.flatMap(jsonString => Future.failed(new RuntimeException(jsonString)))
        case Failure(exception) => Future.failed(exception)
      }

    Try(Await.result(maybeEvents, scala.concurrent.duration.Duration.Inf))
      .map(googleEventResponse => googleEventResponse.items)
      .map(googleEvents => googleEvents.map(googleEvent => googleEvent.toEvent))
  }

  override def create(calendarId: String, event: Event): Try[Event] = ???

  override def delete(calendarId: String, eventId: String): Try[Unit] = {
    val request = HttpRequest(HttpMethods.DELETE)
        .withUri(Uri(resource(calendarId, eventId)))

    val mayBeResult = Http().singleRequest(request)
        .transformWith{
          case Success(Response(StatusCodes.OK, maybeJsonString)) => Future.successful(())
          case Success(Response(_, maybeJsonString)) => maybeJsonString.flatMap(jsonString => Future.failed(new RuntimeException(jsonString)))
          case Failure(exception) => Future.failed(exception)
        }

    Try(Await.result(mayBeResult, scala.concurrent.duration.Duration.Inf))
  }

  private def resources(calendarId: String): String = s"https://www.googleapis.com/calendar/v3/calendars/$calendarId/events"
  private def resource(calendarId: String, eventId: String): String = s"https://www.googleapis.com/calendar/v3/calendars/$calendarId/events/$eventId"
}
