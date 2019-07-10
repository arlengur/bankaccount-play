package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.Status.FORBIDDEN
import play.api.test.Helpers._
import play.api.test._
import exception.AccountException._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class ApiControllerTest extends PlaySpec with GuiceOneAppPerTest {
  val withdraw = """{"amount": 1000}"""
  val exceededMaxAmountWithdraw = """{"amount": 5000}"""
  val exceededMaxWithdrawPerDay = """{"amount": 50001}"""
  val exceededMaxWithdrawPerTrans = """{"amount": 20001}"""
  val deposit = """{"amount": 1000}"""
  val exceededMaxDepositPerDay = """{"amount": 150001}"""
  val exceededMaxDepositPerTrans = """{"amount": 40001}"""
  
  "Application" should {

    "return balance by request" in {
      val balance = FakeRequest(GET, "/balance")
      val Some(response) = route(app, balance)
      status(response) mustEqual OK
      contentType(response) mustBe Some(TEXT)
      contentAsString(response) must include("0")
    }

    "successful deposit" in {
      val req = FakeRequest(PUT, "/deposit")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(deposit)
      val Some(response) = route(app, req)
      status(response) mustEqual OK

      val balance = FakeRequest(GET, "/balance")
      val Some(resp) = route(app, balance)
      status(resp) mustEqual OK
      contentType(resp) mustBe Some(TEXT)
      contentAsString(resp) must include("1000")
    }

    "reject deposit if amount exceeded max per day" in {
      val req = FakeRequest(PUT, "/deposit")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(exceededMaxDepositPerDay)
      val Some(response) = route(app, req)
      status(response) mustEqual FORBIDDEN
      contentType(response) mustBe Some(TEXT)
      contentAsString(response) must include(ExceededMaxDepositPerDay().getMessage())

    }

    "reject deposit if amount exceeded max per trans" in {
      val req = FakeRequest(PUT, "/deposit")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(exceededMaxDepositPerTrans)
      val Some(response) = route(app, req)
      status(response) mustEqual FORBIDDEN
      contentType(response) mustBe Some(TEXT)
      contentAsString(response) must include(ExceededMaxDepositPerTrans().getMessage())
    }

    "reject deposit if exceeded max frequency per day" in {
      val req = FakeRequest(PUT, "/deposit")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(deposit)

      for {
        _ <- route(app, req)
        _ = Thread.sleep(1000)
        _ <- route(app, req)
        _ = Thread.sleep(1000)
        _ <- route(app, req)
        _ = Thread.sleep(1000)
        r <- route(app, req)
      } yield {
        status(r) mustEqual FORBIDDEN
        contentType(r) mustBe Some(TEXT)
        contentAsString(r) must include(ExceededMaxDepositFrequencyPerDay().getMessage())
      }
    }
    "successful withdraw" in {
      val req = FakeRequest(PUT, "/withdrawal")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(withdraw)
      val Some(response) = route(app, req)
      status(response) mustEqual OK
    }

    "reject withdraw if amount exceeded max per day" in {
      val req = FakeRequest(PUT, "/withdrawal")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(exceededMaxWithdrawPerDay)
      val Some(response) = route(app, req)
      status(response) mustEqual FORBIDDEN
      contentType(response) mustBe Some(TEXT)
      contentAsString(response) must include(ExceededMaxWithdrawPerDay().getMessage())

    }

    "reject withdraw if amount exceeded max per trans" in {
      val req = FakeRequest(PUT, "/withdrawal")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(exceededMaxWithdrawPerTrans)
      val Some(response) = route(app, req)
      status(response) mustEqual FORBIDDEN
      contentType(response) mustBe Some(TEXT)
      contentAsString(response) must include(ExceededMaxWithdrawPerTrans().getMessage())
    }

    "reject withdraw if amount exceeded balance" in {
      val req = FakeRequest(PUT, "/withdrawal")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(exceededMaxAmountWithdraw)
      val Some(response) = route(app, req)
      status(response) mustEqual FORBIDDEN
      contentType(response) mustBe Some(TEXT)
      contentAsString(response) must include(ExceededAccountBalance().getMessage())
    }

    "reject withdraw if exceeded max frequency per day" in {
      val req = FakeRequest(PUT, "/withdrawal")
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(withdraw)

      for {
        _ <- route(app, req)
        _ = Thread.sleep(1000)
        _ <- route(app, req)
        _ = Thread.sleep(1000)
        r <- route(app, req)
      } yield {
        status(r) mustEqual FORBIDDEN
        contentType(r) mustBe Some(TEXT)
        contentAsString(r) must include(ExceededMaxWithdrawFrequencyPerDay().getMessage())
      }
    }
  }
}
