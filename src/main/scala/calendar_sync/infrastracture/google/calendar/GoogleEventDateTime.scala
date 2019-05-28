package calendar_sync.infrastracture.google.calendar

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import calendar_sync.domain.event.{EventDate, EventDateTime}

case class GoogleEventDateTime(date: Option[String], dateTime: Option[String]) {

  def toEventDateAndTime = this match {
    case GoogleEventDateTime(Some(value), None) => EventDate(LocalDate.parse(value, GoogleEventDateTime.dateFormatter))
    case GoogleEventDateTime(None, Some(value)) => EventDateTime(LocalDateTime.parse(value, GoogleEventDateTime.dateTimeFormatter))
    case _ => ???
  }
}

object GoogleEventDateTime {
  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
  private val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  def of(localDate: LocalDate): GoogleEventDateTime =
    GoogleEventDateTime(Some(localDate.format(dateFormatter)), None)

  def of(localDateTime: LocalDateTime) =
    GoogleEventDateTime(None, Some(localDateTime.format(dateTimeFormatter)))
}
