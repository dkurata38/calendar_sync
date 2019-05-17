package calendar_sync.application.coordinator

import calendar_sync.application.service.{EventsCommandService, EventsQueryService}
import calendar_sync.domain.Duration

class EventsSyncCoordinator {
  def sync(sourceCalendarIds: Seq[String], targetCalendarId: String, duration: Duration, dummyText: String) = {
    val queryService = new EventsQueryService
    val commandService = new EventsCommandService

    queryService.query(targetCalendarId, duration).map(events =>
      events.filter(event => event.masked(dummyText))
        .map(event => commandService.delete(targetCalendarId, event.id))
        .foreach{case Left(value) => value.printStackTrace()}
    )
    queryService.query(sourceCalendarIds, duration).map(events =>
      events
        .map(event => event.mask(dummyText))
        .map(event => commandService.create(targetCalendarId, event))
        .foreach{case Left(value) => value.printStackTrace()})
      .left.foreach(exception => exception.printStackTrace())
  }
}
