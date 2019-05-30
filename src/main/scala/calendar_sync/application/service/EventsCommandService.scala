package calendar_sync.application.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.domain.credential.Credential
import calendar_sync.domain.event.{CalendarEventClient, Event, EventId}
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventsCommandService @Inject()(private val client: CalendarEventClient) {
  def delete(calendarId: String, eventId: EventId, credential: Credential)
            (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Unit] = client.delete(calendarId, eventId, credential)

  def create(calendarId: String, event: Event, credential: Credential)
            (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext):Future[Event]= client.create(calendarId, event, credential)
}
