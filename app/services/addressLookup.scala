/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import com.typesafe.config.ConfigFactory
import controllers.{InvalidPostcode, NoMatchesFound, AddressTypedDetails}
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.ws.WS
import views.html.addresslookup.address_lookup
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.functional.syntax._

import scala.concurrent.Future


object AddressLookupService {
  import play.api.Play.current
  import scala.concurrent.ExecutionContext.Implicits.global

  val conf = ConfigFactory.load()
  val lookupServer = conf.getString("address-lookup-server")
  val url = s"https://$lookupServer/address-lookup/v1/uk/addresses.json"


  implicit val addrReader: Reads[Address] = (
    (JsPath \ "id").read[String] and
      (JsPath \\ "lines").read[Array[String]] and
      (JsPath \\ "town").read[String] and
      (JsPath \\ "postcode").read[String]
    )( Address.apply _ )


  def findAddresses(postcode:String, filter:Option[String]): Future[Either[Status, Option[List[Address]]]] = {
    val query: Seq[(String, String)] = Seq(
      ("postcode", postcode)) ++ filter.map( name =>  "filter" -> name )

    WS.url(url).withHeaders("X-Hmrc-Origin" -> "addressLookupDemo").withQueryString(query : _*).get().map {
      case response if response.status == 200 =>
        response.json match {
          case addrs:play.api.libs.json.JsArray =>
            val addressList = Right(Some(addrs.value.map { i => i.as[Address] }.toList))
            addressList
          case err => Left(ServiceUnavailable)
        }
      case _ => Left(ServiceUnavailable)
    }
  }
}




case class Address(uprn: String, lines: Array[String], town: String, postcode: String) {
  def toAddrString:String = {
    val lineStr = lines.mkString(" ")
    s"$lineStr $town $postcode"
  }
}