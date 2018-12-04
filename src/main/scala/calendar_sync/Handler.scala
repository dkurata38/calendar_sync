package calendar_sync

import calendar_sync.application.CalendarSyncUseCase
import com.amazonaws.services.lambda.runtime.Context

class Handler {
  def handleRequest(input: String, context: Context) = {
    val useCase = new CalendarSyncUseCase
    useCase.sync()
  }
}