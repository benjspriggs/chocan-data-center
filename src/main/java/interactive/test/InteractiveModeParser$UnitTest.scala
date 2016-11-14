import InteractiveMode._

/**
  * Created by bspriggs on 11/13/2016.
  */
class InteractiveModeParser$UnitTest extends InteractiveModeParserFixtures {

  behavior of "InteractiveModeParser$UnitTest"

  behavior of "_help"

  {
    val p = parser._help

    "_help" should "parse a help token" in {
      forAll(f.helpRequests) { word: String => doesParseToA(word, Help, p) }
    }

    "_help" should "parse a help token and some query, with whitespace" in {
      forAll(f.helpRequests) { word: String =>
        doesParseToA(word + " another word", Help, p)
      }
    }

    "_help" should "not parse tokens with missing whitespace" in {
      forAll(f.helpRequests) { word: String => doesNotParseToA(word + "asdfad", Help, parser._help)}
    }
  }

  behavior of "_stop"

  {
    val p = parser._stop

    "_stop" should "parse a stop token" in {
      forAll(f.stopRequests) { word: String => doesParseToA(word, Stop, p)}
    }

    "_stop" should "not parse if there's something after the stop token" in {
      forAll(f.stopRequests) { word: String => doesNotParseToA(word + "asdfa", Stop, parser._stop)}
    }
  }

  behavior of "_superobject"

  {
    val p = parser._superobject

    "_superobject" should "parse a plural type and a payload" in {
      forAll(f.typeMany) { word: String =>
        doesParseToA(word + " " + f.validJson, SuperObj, p)
      }
    }

    "_superobject" should "parse a universal modifier with a type" in {
      forAll(f.typeMany) { word: String =>
        doesParseToA("all " + word, SuperObj, p)
      }
    }

    "_superobject" should "parse a universal modifier with a type and clarifying JSON" in {
      forAll(f.typeMany) { word: String =>
        doesParseToA("all " + word + " " + f.validJson, SuperObj, p)
      }
    }

    "_superobject" should "not parse a universal modifier without a type" in {
      doesNotParseToA("all " + f.validJson, SuperObj, parser._superobject)
    }
  }

  behavior of "_request"

  {
    val p = parser._request

    "_request" should "parse a request token" in {
      forAll(f.requests) { word: String => doesParseToA(word, Request, p) }
    }

    "_request" should "not parse a non-request token" in {
      forAll(f.typeSingle) { word: String => doesNotParseToA(word + "n", Request, p) }
    }
  }

  // behavior of "_payload"
  // should be a JSON object, tested in JsonParser$UnitTest

  behavior of "_singleType"

  {
    val p = parser._singleType

    "_singleType" should "parse a singular type" in {
      forAll(f.typeSingle) {
        word: String => doesParseToA(word, Type.One, p) }
    }

    "_singleType" should "not parse a plural type" in {
      forAll(f.typeMany) {
        word: String => doesNotParseToA(word, Type.One, p) }
    }
  }

  behavior of "_manyType"

  {
    val p = parser._manyType

    "_manyType" should "parse a plural type" in {
      forAll(f.typeMany) {
        word: String => doesParseToA(word, Type.Many, p) }
    }

    "_manyType" should "not parse a singular type" in {
      forAll(f.typeSingle) {
        word: String => doesNotParseToA(word, Type.Many, p) }
    }
  }

  behavior of "_object"

  {
    val p = parser.`_object`

    "_object" should "parse a single type and JSON object" in {
      forAll(f.typeSingle) {
        word: String => doesParseToA(word + f.validJson, Obj, p)
      }
    }

    "_object" should "parse a plural type and single JSON object" in {
      forAll(f.typeMany) {
        word: String => doesParseToA(word + f.validJson, Obj, p)
      }
    }

    "_object" should "parse a plural type and multiple JSON objects" in {
      forAll(f.typeMany) {
        word: String => doesParseToA(word + f.validJson + f.validJson, Obj, p)
      }
    }

    "_object" should "not parse a JSON object without a type" in {
      doesNotParseToA(f.validJson, Obj, p)
    }

    "_object" should "not parse a type without a JSON object" in {
      forAll(f.typeSingle) {
        word: String => doesNotParseToA(word, Obj, p)
      }
      forAll(f.typeMany) {
        word: String => doesNotParseToA(word, Obj, p)
      }
    }
  }

  behavior of "_sql_literal"


  behavior of "_expr"

}