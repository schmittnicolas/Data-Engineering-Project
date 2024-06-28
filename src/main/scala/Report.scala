import java.time.LocalDateTime



case class Report(latitude: Double, longitude: Double, date: LocalDateTime, alcoholLevel: Float){

  override def toString: String = {
    s"latitude: $latitude, longitude: $longitude, dayOfWeek: $date, alcoholLevel: $alcoholLevel"
  }
}

object ReportParser {
  def parseReport(s: String): Option[Report] = {
    try {
      val parts = s.stripPrefix("latitude: ").split(", longitude: |, dayOfWeek: |, alcoholLevel: ")
      if (parts.length != 4) return None
      val report = Report(
        latitude = parts(0).toDouble,
        longitude = parts(1).toDouble,
        date = LocalDateTime.parse(parts(2)),
        alcoholLevel = parts(3).toFloat
      )
      Some(report)
    } catch {
      case _: Exception => None
    }
  }
}
