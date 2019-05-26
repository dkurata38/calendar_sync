package calendar_sync.domain.event

import com.typesafe.config.ConfigFactory

class Event(val id: EventId, val title: String, val start: EventDateAndTime, val end: EventDateAndTime) {
  private val dummyText = ConfigFactory.load().getString("app.dummyText")

  def masked: Boolean = title == dummyText

  def mask = new Event(id, dummyText, start, end)

  def canEqual(other: Any): Boolean = other.isInstanceOf[Event]

  override def equals(other: Any): Boolean = other match {
    case that: Event =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
