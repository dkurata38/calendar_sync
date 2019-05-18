package calendar_sync.application.coordinator

import calendar_sync.application.service.{EventsCommandService, EventsQueryService}
import calendar_sync.domain.Duration
import javax.inject.{Inject, Singleton}

@Singleton
class EventsSyncCoordinator @Inject() (private val queryService: EventsQueryService,
                                       private val commandService: EventsCommandService) {
  def sync(sourceCalendarIds: Seq[String], targetCalendarId: String, duration: Duration, dummyText: String) = {
    queryService.query(targetCalendarId, duration).map(events =>
      events.filter(event => event.masked(dummyText))
        .map(event => commandService.delete(targetCalendarId, event.id))
        .foreach{
          case Left(value) => value.printStackTrace()
          case _ => ()
        }
    )
    queryService.query(sourceCalendarIds, duration).map(events =>
      events
        .map(event => event.mask(dummyText))
        .map(event => commandService.create(targetCalendarId, event))
        .foreach{
          case Left(value) => value.printStackTrace()
          case _ => ()
        })
      .left.foreach(exception => exception.printStackTrace())
  }
}
