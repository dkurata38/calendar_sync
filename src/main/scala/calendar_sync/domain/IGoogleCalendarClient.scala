package calendar_sync.domain

import java.time.LocalDate

trait IGoogleCalendarClient {
  def getEventsByCalendarId(calendarId: String, startDateTime: LocalDate, endDateTime: LocalDate): Seq[Event]

  def create(event: Event)

  def delete(calendarId: String, eventId: String)
}
