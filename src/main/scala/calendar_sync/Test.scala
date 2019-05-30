package calendar_sync

import java.time.LocalDate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.application.coordinator.RefreshCredentialCoordinator
import calendar_sync.domain.credential.Credential
import calendar_sync.domain.{Date, Duration}
import calendar_sync.infrastracture.google.calendar.GoogleCalendarEventClient
import com.google.inject.Guice

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


object Test extends App {
  val injector = Guice.createInjector(new CalendarSyncModule())
  val refreshCredentialCoordinator = injector.getInstance(classOf[RefreshCredentialCoordinator])
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val mateliarizer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  refreshCredentialCoordinator.refresh.onComplete{
    case Success(_) => println("完了しました.")
    case Failure(exception) => exception.printStackTrace()
  }

}
