package calendar_sync.infrastracture

import java.time.LocalDate

import calendar_sync.domain.{Event, IGoogleCalendarClient}

class GoogleCalendarClient extends IGoogleCalendarClient{
  override def getEventsByCalendarId(calendarId: String, startDateTime: LocalDate, endDateTime: LocalDate): Seq[Event] = ???

  override def create(event: Event): Unit = ???

  override def delete(calendarId: String, eventId: String): Unit = ???
}
