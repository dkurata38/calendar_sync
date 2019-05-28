package calendar_sync.infrastracture.google.calendar

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

case class GoogleEventDateTime(date: Option[String], dateTime: Option[String])

object GoogleEventDateTime {
  private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
  private val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  def of(localDate: LocalDate): GoogleEventDateTime =
    GoogleEventDateTime(Some(localDate.format(dateFormatter)), None)

  def of(localDateTime: LocalDateTime) =
    GoogleEventDateTime(None, Some(localDateTime.format(dateTimeFormatter)))

  def unapply(arg: GoogleEventDateTime): Option[(Option[LocalDate], Option[LocalDateTime])] =
    Some(arg.date.map(date => LocalDate.parse(date, dateFormatter)),
      arg.dateTime.map(dateTime => LocalDateTime.parse(dateTime, dateTimeFormatter)))
}
