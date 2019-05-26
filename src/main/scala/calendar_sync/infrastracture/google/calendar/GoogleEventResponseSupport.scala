package calendar_sync.infrastracture.google.calendar

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait GoogleEventResponseSupport extends DefaultJsonProtocol {
  implicit val googleEventDateTimeFormat: RootJsonFormat[GoogleEventDateTime] = jsonFormat(GoogleEventDateTime, "date", "dateTime")
  implicit val googleEventFormat: RootJsonFormat[GoogleEvent] = jsonFormat(GoogleEvent, "id", "summary", "start", "end")
  implicit val googleEventResponseFormat: RootJsonFormat[GoogleEventResponse] = jsonFormat(GoogleEventResponse, "items")
}
