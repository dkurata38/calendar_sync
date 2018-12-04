package calendar_sync.domain

import java.time.{LocalDate, LocalDateTime}

abstract class Event (val calendarId: String, val eventId: Option[String], var title: String) {
  def mask(newTitle: String): Event
}

object Event {
  def apply (calendarId: String, eventId: Option[String], title: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime): NormalEvent
    = NormalEvent(calendarId, eventId, title, startDateTime, endDateTime)
  def apply(calendarId: String, eventId: Option[String], title: String, startDate: LocalDate, endDate: LocalDate)
    = AllDayEvent(calendarId, eventId, title, startDate, endDate)
}