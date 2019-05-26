package calendar_sync.domain.event

import java.time.LocalDateTime

case class EventDateTime(value: LocalDateTime) extends EventDateAndTime {
  override type Value = LocalDateTime
}
