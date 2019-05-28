package calendar_sync.infrastracture.google.calendar

import java.time.{LocalDate, LocalDateTime}

import calendar_sync.domain.event._

case class GoogleEvent(id: String, summary: String, start: GoogleEventDateTime, end: GoogleEventDateTime) {
  def toEvent: Event = {
    new Event(EventId(id), summary, start.toEventDateAndTime, end.toEventDateAndTime)
  }
}

object GoogleEvent {
  def of(event: Event) = {
    val start = event.start match {
      case EventDate(value) => GoogleEventDateTime.of(value)
      case EventDateTime(value) => GoogleEventDateTime.of(value)
    }

    val end = event.end match {
      case EventDate(value) => GoogleEventDateTime.of(value)
      case EventDateTime(value) => GoogleEventDateTime.of(value)
    }

    GoogleEvent(event.id.value, event.title, start, end)
  }
}
