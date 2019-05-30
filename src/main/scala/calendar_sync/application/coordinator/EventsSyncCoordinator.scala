package calendar_sync.application.coordinator

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.application.service.{CredentialQueryService, EventsCommandService, EventsQueryService}
import calendar_sync.domain.Duration
import calendar_sync.domain.event.Event
import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventsSyncCoordinator @Inject() (private val eventsQueryService: EventsQueryService,
                                       private val eventsCommandService: EventsCommandService,
                                       private val credentialQueryService: CredentialQueryService) {
  def sync(sourceCalendarIds: Seq[String], targetCalendarId: String, duration: Duration)
          (implicit actorSystem: ActorSystem, materializer: ActorMaterializer, executionContext: ExecutionContext):Future[Seq[Event]]= {
    credentialQueryService.get
        .map(maybeCredential =>
          maybeCredential.map{credential =>

            eventsQueryService.query(targetCalendarId, duration, credential)
              .map(_.filter(_.masked))
              .map(events => events.foreach(event => eventsCommandService.delete(targetCalendarId, event.id, credential)))
              .flatMap(_ => eventsQueryService.query(sourceCalendarIds, duration, credential))
              .map(events => events.map(_.mask))
              .map(events => events.map(eventsCommandService.create(targetCalendarId, _, credential)))
              .flatMap(maybeEvents => maybeEvents.foldLeft(Future.successful(Seq.empty[Event])){ case(maybe1, maybe2) =>
                for{
                  _1 <- maybe1
                  _2 <- maybe2
                } yield _1 :+ _2
              })
          }.getOrElse(Future.failed[Seq[Event]](new RuntimeException("認証情報を取得できませんでした.")))
        ).left.map(Future.failed[Seq[Event]](_))
        .merge
  }
}
