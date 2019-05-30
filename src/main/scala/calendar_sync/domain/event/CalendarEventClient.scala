package calendar_sync.domain.event

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.domain.Duration
import calendar_sync.domain.credential.Credential

import scala.concurrent.{ExecutionContext, Future}

trait CalendarEventClient {
  def getEventsByCalendarId(calendarId: String, duration: Duration, credential: Credential)
                           (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Seq[Event]]

  def create(calendarId: String, event: Event, credential: Credential)
            (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Event]

  def delete(calendarId: String, eventId: EventId, credential: Credential)
            (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Unit]

}
