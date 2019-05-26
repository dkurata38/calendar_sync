package calendar_sync.domain.event

import java.time.temporal.Temporal

trait EventDateAndTime {
  type Value <: Temporal
  def value: Value
}
