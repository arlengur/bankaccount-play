package utils

import java.util.Date
import org.scalatestplus.play.PlaySpec

class UtilsTest extends PlaySpec {
  "Utils" should {
    "convert string to date" in {
      val initDate = new Date
      val dateStr = Utils.dateToString(initDate)
      val date = Utils.stringToDate(dateStr)
      date.getDay mustEqual initDate.getDay
      date.getMonth mustEqual initDate.getMonth
      date.getYear mustEqual initDate.getYear
    }
  }
}
