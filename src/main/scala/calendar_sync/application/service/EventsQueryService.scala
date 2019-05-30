package calendar_sync.application.service


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.domain.Duration
import calendar_sync.domain.credential.Credential
import calendar_sync.domain.event.{CalendarEventClient, Event}
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventsQueryService @Inject() (private val client: CalendarEventClient) {
  def query(calendarId: String, duration: Duration, credential: Credential)
           (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Seq[Event]]
    = client.getEventsByCalendarId(calendarId, duration, credential)

  def query(calendarIds: Seq[String], duration: Duration, credential: Credential)
           (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext): Future[Seq[Event]] = calendarIds
    .map(calendarId => query(calendarId, duration, credential))
    .reduce[Future[Seq[Event]]]{ case(maybe1, maybe2) =>
      for {
        _1 <- maybe1
        _2 <- maybe2
      } yield _1 ++ _2
    }
}
