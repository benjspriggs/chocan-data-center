package interactive.parser

import interactive.Tokens._
import interactive.fixtures.InteractiveModeParserFixtures

import scala.collection.mutable.ArrayBuffer

/**
  * Created by bspriggs on 11/13/2016.
  */
class Parser$UnitTest extends InteractiveModeParserFixtures {

  behavior of "interactive.parser.Parser$UnitTest"

  behavior of "_help"

  {
    val p = parser._help

    "_help" should "parse a help interactive.token" in {
      forAll(f.helpRequests) { word: String => doesParseToA(word, Help(None), p) }
    }

    "_help" should "parse a help interactive.token and some query, with whitespace" in {
      forAll(f.helpRequests) { word: String =>
        doesParseToA(word + " another word", Help(Some("another word")), p)
      }
    }

    "_help" should "not parse tokens with missing whitespace" in {
      forAll(f.helpRequests) { word: String => doesNotParseToA(word + "asdfad", Help(Some("asdfad")), parser._help)}
    }
  }

  behavior of "_stop"

  {
    val p = parser._stop

    "_stop" should "parse a stop interactive.token" in {
      forAll(f.stopRequests) { word: String => doesParseToA(word, Stop, p)}
    }

    "_stop" should "not parse if there's something after the stop interactive.token" in {
      forAll(f.stopRequests) { word: String => doesNotParseToA(word + "asdfa", Stop, parser._stop)}
    }
  }

  behavior of "_superobject"

  {
    val p = parser._superobject

    "_superobject" should "parse a plural type and a payload" in {
      forAll(f.typeMany) { word: String =>
        doesParseToA(word + " " + f.validJson, SuperObj(Type.Many(word), f.optionJson()), p)
      }
    }

    "_superobject" should "parse a universal modifier with a type" in {
      forAll(f.typeMany) { word: String =>
        doesParseToA("all " + word, SuperObj(Type.Many(word), None), p)
      }
    }

    "_superobject" should "parse a universal modifier with a type and clarifying JSON" in {
      forAll(f.typeMany) { word: String =>
        doesParseToA("all " + word + " " + f.validJson, SuperObj(Type.Many(word), f.optionJson()), p)
      }
    }

    "_superobject" should "parse a plural type and single JSON object" in {
      forAll(f.typeMany) {
        word: String => doesParseToA(word + f.validJson, SuperObj((Type.Many(word), f.optionJson())), p)
      }
    }

    "_superobject" should "not parse a plural type and multiple JSON objects" in {
      forAll(f.typeMany) {
        word: String => doesNotParseToA(word + f.validJson + f.validJson,
          SuperObj(Type.Many(word), f.optionJson()), p)
      }
    }


    "_superobject" should "not parse a universal modifier without a type" in {
      doesNotParseToA("all " + f.validJson, SuperObj((Type.Many(""), f.optionJson())), p)
    }
  }

  behavior of "_request"

  {
    val p = parser._request

    "_request" should "parse a request interactive.token" in {
      forAll(f.requests) { word: String => doesParseToA(word, Request(word), p) }
    }

    "_request" should "not parse a non-request interactive.token" in {
      forAll(f.typeSingle) { word: String => doesNotParseToA(word + "n", Request(word), p) }
    }
  }

  // behavior of "_payload"
  // should be a JSON object, tested in JsonParser$UnitTest

  behavior of "_singleType"

  {
    val p = parser._singleType

    "_singleType" should "parse a singular type" in {
      forAll(f.typeSingle) {
        word: String => doesParseToA(word, Type.One(word), p) }
    }

    "_singleType" should "not parse a plural type" in {
      forAll(f.typeMany) {
        word: String => doesNotParseToA(word, Type.One(word), p) }
    }
  }

  behavior of "_manyType"

  {
    val p = parser._manyType

    "_manyType" should "parse a plural type" in {
      forAll(f.typeMany) {
        word: String => doesParseToA(word, Type.Many(word), p) }
    }

    "_manyType" should "not parse a singular type" in {
      forAll(f.typeSingle) {
        word: String => doesNotParseToA(word, Type.Many(word), p) }
    }
  }

  behavior of "_object"

  {
    val p = parser.`_object`

    "_object" should "parse a single type and JSON object" in {
      forAll(f.typeSingle) {
        word: String => doesParseToA(word + f.validJson,
          Obj((Type.One(word), f.parsedJson())), p)
      }
    }

    "_object" should "not parse a JSON object without a type" in {
      doesNotParseToA(f.validJson, Obj((Type.One("type"), f.parsedJson())), p)
    }

    "_object" should "not parse a type without a JSON object" in {
      forAll(f.typeSingle) {
        word: String => doesNotParseToA(word, Obj((Type.One(word), f.parsedJson("{}"))), p)
      }
      forAll(f.typeMany) {
        word: String => doesNotParseToA(word, Obj((Type.One(word), f.parsedJson("{}"))), p)
      }
    }
  }

  behavior of "_sql_literal"

  {
    val p = parser._sql_literal

    "_sql_literal" should "parse a SQL request and query" in {
      doesParseToA("SQL " + f.literal_sql, SQL(f.literal_sql), p)
    }

    "_sql_literal" should "not parse a SQL query without a request" in {
      doesNotParseToA(f.literal_sql, SQL(f.literal_sql), p)
    }

    "_sql_literal" should "not parse a SQL request without a query" in {
      doesNotParseToA("SQL", SQL(""), p)
    }

    "_sql_literal" should "be able to take quoted queries" in {
      doesParseToA("SQL '" + f.literal_sql + "'",
        SQL("'" + f.literal_sql + "'"), p)
      doesParseToA("SQL \"" + f.literal_sql + "\"",
        SQL("\"" + f.literal_sql + "\""), p)
      doesParseToA("SQL `" + f.literal_sql + "`",
        SQL("`" + f.literal_sql + "`"), p)
    }
  }


  behavior of "_expr"

  {
    val p = parser._expression

    "_expr" should "parse a stop request" in {
      forAll(f.stopRequests) {
        word: String => doesParseToA(word, Stop, p)
      }
    }

    "_expr" should "parse a help request with any known keyword" in {
      forAll(f.helpRequests) { help: String =>
        forAll(f.requests) { word: String =>
          doesParseToA(help + " " + word, Help(Some(word)), p)
        }
        forAll(f.typeSingle) { word: String =>
          doesParseToA(help + " " + word, Help(Some(word)), p)
        }
        forAll(f.typeMany) { word: String =>
          doesParseToA(help + " " + word, Help(Some(word)), p)
        }
        forAll(f.stopRequests) { word: String =>
          doesParseToA(help + " " + word, Help(Some(word)), p)
        }
        doesParseToA(help + " any", Help(Some("any")), p)
      }
    }

    "_expr" should "parse a request with a singular object" in {
      forAll(f.requests) { word: String =>
        forAll(f.typeSingle) { `type`: String =>
          doesParseToA(word + " " + `type` + f.validJson,
            (Request(word), Obj((Type.One(`type`), f.parsedJson()))), p)
        }
      }
    }

    "_expr" should "parse a request with a singular implied type and multiple objects" in {
      forAll(f.requests) { word: String =>
        forAll(f.typeSingle) { `type`: String =>
          def o = Obj((Type.One(`type`), f.parsedJson()))
          doesParseToA(word + " " + `type` + f.validJson + f.validJson + f.validJson,
            (Request(word), ArrayBuffer(o, o, o)),
            p)
        }
      }
    }

    "_expr" should "parse a request with a plural type and singular object" in {
      forAll(f.requests) { word: String =>
        forAll(f.typeMany) { `type`: String =>
          doesParseToA(word + " " + `type` + f.validJson,
            (Request(word), SuperObj((Type.Many(`type`), f.optionJson()))), p)
        }
      }
    }

    "_expr" should "parse a generic request with a singular object" in {
      forAll(f.requests) { word: String =>
        forAll(f.typeMany) { `type`: String =>
          doesParseToA(word + " all " + `type` + f.validJson,
            (Request(word), SuperObj((Type.Many(`type`), f.optionJson()))), p)
        }
      }
    }

    "_expr" should "not parse a generic request with multiple objects" in {
      forAll(f.requests) { word: String =>
        forAll(f.typeMany) { `type`: String =>
          doesNotParseToA(word + " all " + `type` + f.validJson + f.validJson + f.validJson,
            (Request(word), SuperObj((Type.Many(`type`), f.optionJson()))), p)
        }
      }
    }
  }

}