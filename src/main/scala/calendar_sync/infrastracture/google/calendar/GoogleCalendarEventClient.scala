package calendar_sync.infrastracture.google.calendar

import java.time.ZoneId
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes, Uri}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import calendar_sync.domain.Duration
import calendar_sync.domain.credential.Credential
import calendar_sync.domain.event.{CalendarEventClient, Event, EventId}
import calendar_sync.infrastracture.google.GoogleApiClient
import javax.inject.Singleton
import spray.json.enrichAny

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class GoogleCalendarEventClient extends Directives with CalendarEventClient with GoogleApiClient with GoogleEventResponseSupport {
  override def getEventsByCalendarId(calendarId: String, duration: Duration, credential: Credential)
                                    (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Seq[Event]] = {
    val timeMin = duration.start.value.atStartOfDay(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val timeMax = duration.end.value.atStartOfDay(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val getEndPoint = s"${resources(calendarId)}?timeMin=$timeMin&timeMax=$timeMax"

    val request = HttpRequest(HttpMethods.GET)
      .withUri(Uri(getEndPoint))
      .withHeaders(Authorization(OAuth2BearerToken(credential.accessToken)))

    Http().singleRequest(request)
      .transformWith{
        case Success(Response(StatusCodes.OK, maybeJsonString)) => maybeJsonString.flatMap(Unmarshal(_).to[GoogleEventResponse])
        case Success(Response(_, maybeJsonString)) => maybeJsonString.flatMap(jsonString => Future.failed(new RuntimeException(jsonString)))
        case Failure(exception) => Future.failed(exception)
      }
      .map(googleEventResponse => googleEventResponse.items)
      .map(googleEvents => googleEvents.map(googleEvent => googleEvent.toEvent))
  }

  override def create(calendarId: String, event: Event, credential: Credential)
                     (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Event] = {
    GoogleEvent.of(event).toJson
    val request = HttpRequest(HttpMethods.POST)
        .withUri(Uri(resources(calendarId)))

    Http().singleRequest(request)
        .transformWith{
          case Success(Response(StatusCodes.OK, maybeJsonString)) => maybeJsonString.flatMap(Unmarshal(_).to[GoogleEventResponse])
          case Success(Response(_, maybeJsonString)) => maybeJsonString.flatMap(jsonString => Future.failed(new RuntimeException(jsonString)))
          case Failure(exception) => Future.failed(exception)
        }
      .map(_.items.head.toEvent)
  }

  override def delete(calendarId: String, eventId: EventId, credential: Credential)
                     (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Unit] = {
    val request = HttpRequest(HttpMethods.DELETE)
        .withUri(Uri(resource(calendarId, eventId.value)))

    Http().singleRequest(request)
        .transformWith{
          case Success(Response(StatusCodes.OK, maybeJsonString)) => Future.successful(())
          case Success(Response(_, maybeJsonString)) => maybeJsonString.flatMap(jsonString => Future.failed(new RuntimeException(jsonString)))
          case Failure(exception) => Future.failed(exception)
        }
  }

  private def resources(calendarId: String): String = s"https://www.googleapis.com/calendar/v3/calendars/$calendarId/events"
  private def resource(calendarId: String, eventId: String): String = s"https://www.googleapis.com/calendar/v3/calendars/$calendarId/events/$eventId"
}
