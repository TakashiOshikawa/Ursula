package com.ursula

import akka.actor.Actor
import com.redisconnect.RedisConnect
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.routing._


class UrsulaServiceActor extends Actor with MainService {

  def actorRefFactory = context
  def receive = runRoute(route)

}


trait MainService extends HttpService with Actor {


  val route =

    path("special-keys") {
      get {
        respondWithMediaType(`text/html`) {
          respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
            complete {
              <html>
                <body>
                  <p>Welcome to Ursulla.</p>
                  <h4>{RedisConnect.getKeysString}</h4>
                </body>
              </html>
            }
          }
        }
      }
    } ~
    path(Segment) { log_path =>
      get {
        respondWithMediaType(`text/html`) {
          respondWithHeader(RawHeader("Access-Control-Allow-Origin", "localhost:8080")) {
            complete {
              <html>
                <body>
                  <h3>{RedisConnect.get(log_path)}</h3>
                </body>
              </html>
            }
          }
        }
      } ~
      formFields('log_msg ? "") { log_msg =>
        validate(log_msg.nonEmpty, s"Invalid Request") {
          post {
            RedisConnect.setex(log_path, 20, log_msg)
            complete("OK")
          }
        }
      }
    }

}


object Redis {
  case class GetValue(key: String)
  case class SetValue(key: String, value: String)
}