package calendar_sync.domain.event

import java.time.LocalDate

case class EventDate(value: LocalDate) extends EventDateAndTime {
  override type Value = LocalDate
}
