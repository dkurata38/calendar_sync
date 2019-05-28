package calendar_sync.infrastracture.google.calendar

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait GoogleEventResponseSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val googleEventDateTimeFormat: RootJsonFormat[GoogleEventDateTime] = jsonFormat(GoogleEventDateTime.apply, "date", "dateTime")
  implicit val googleEventFormat: RootJsonFormat[GoogleEvent] = jsonFormat(GoogleEvent.apply, "id", "summary", "start", "end")
  implicit val googleEventResponseFormat: RootJsonFormat[GoogleEventResponse] = jsonFormat(GoogleEventResponse.apply, "items")
}
