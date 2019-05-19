package calendar_sync.domain

case class Event(value: com.google.api.services.calendar.model.Event) {

  def id = value.getId
  def mask(dummyText: String) = Event(
    new com.google.api.services.calendar.model.Event()
      .setSummary(dummyText)
      .setStart(value.getStart)
      .setEnd(value.getEnd)
  )

  def masked(dummyText: String) = value.getSummary == dummyText
}
