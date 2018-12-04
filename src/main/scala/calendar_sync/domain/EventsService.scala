package calendar_sync.domain

import java.time.LocalDate

import calendar_sync.infrastracture.GoogleCalendarClient

class EventsService extends IEventsService {
  override def sync(startDate: LocalDate, endDate: LocalDate): Unit = {
    val toCalendarId: String = ""
    val calendarClient = new GoogleCalendarClient
    val mainCalendarEvents = calendarClient.getEventsByCalendarId(toCalendarId, startDate, endDate)
    mainCalendarEvents.foreach(
      event => event.eventId.foreach(
        id => calendarClient.delete(event.calendarId, id)
      )
    )

    val fromCalendarIds: Seq[String] = Nil
    fromCalendarIds.foreach{calendarId =>
      val fromCalendarEvents = calendarClient.getEventsByCalendarId(calendarId, startDate, endDate)
      fromCalendarEvents.map(event => event.mask("私用")).foreach(e => calendarClient.create(e))
    }
  }
}
