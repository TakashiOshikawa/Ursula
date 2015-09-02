package com.redisconnect

import akka.actor.{Actor, Props}
import com.ursula.Redis


/**
 * Created by oshikawatakashi on 2015/08/31.
 */
class RedisActor extends Actor {

  def receive = {
    case Redis.GetValue(key)        => {
      val value = RedisConnect.get(key)
      sender() ! value
    }
    case Redis.SetValue(key, value) => {
      val isSet = RedisConnect.set(key, value)
      isSet match {
        case true  => sender() ! RedisConnect.get(key)
        case false => "value set failed"
      }
    }
  }

}


object RedisActor {

  val props = Props[RedisActor]
  case class ResponseSetValue(value: String)
  case class ResponseGetValue(value: String)

}


object RedisConnect {
  import com.redis._

  val red = new RedisClient("127.0.0.1", 6379)
  def set(key: String, value: String): Boolean = red.set(key,value)
  def setex(key: String, time: Int, value: String): Boolean = red.setex(key, time, value)
  def get(key: String): String = red.get(key).getOrElse("")
  def keys = red.keys()

  def getKeysString: String = {
    val someKeys = RedisConnect.keys
    someKeys match {
      case Some(keyList) => keyList.filter( _.getOrElse("---bad") != "---bad" ).
                            foldLeft("")( (a: String, b: Option[String]) => a + "\n・" + b.get)
      case None          => "no keys"
    }
  }

}