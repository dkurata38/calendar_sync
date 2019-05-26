package calendar_sync.infrastracture.google.calendar

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import calendar_sync.domain.event.{Event, EventDate, EventDateAndTime, EventDateTime, EventId}

case class GoogleEvent(id: String, summary: String, start: GoogleEventDateTime, end: GoogleEventDateTime) {
  def toEvent: Event = this match {
    case GoogleEvent(_1, _2, _3, _4) => new Event(EventId(_1), _2, _3, _4)
  }
}

object GoogleEvent {
  def unapply(arg: GoogleEvent): Option[(String, String, EventDateAndTime, EventDateAndTime)] = {
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val start = arg.start match {
      case GoogleEventDateTime(Some(date), None) => EventDate(LocalDate.parse(date, dateFormatter))
      case GoogleEventDateTime(None, Some(dateTime)) => EventDateTime(LocalDateTime.parse(dateTime, dateTimeFormatter))
    }
    val end = arg.end match {
      case GoogleEventDateTime(Some(date), None) => EventDate(LocalDate.parse(date, dateFormatter))
      case GoogleEventDateTime(None, Some(dateTime)) => EventDateTime(LocalDateTime.parse(dateTime, dateTimeFormatter))
    }
    Some(arg.id, arg.summary, start, end)
  }
}
