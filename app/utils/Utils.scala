package utils

import java.text.SimpleDateFormat
import java.util.Date

object Utils {

  val DATE_FORMAT = "yyyy-MM-dd"

  def dateToString(d: Date): String = {
    val dateFormat = new SimpleDateFormat(DATE_FORMAT)
    dateFormat.format(d)
  }

  def stringToDate(s: String): Date = {
    val dateFormat = new SimpleDateFormat(DATE_FORMAT)
    dateFormat.parse(s)
  }

}
