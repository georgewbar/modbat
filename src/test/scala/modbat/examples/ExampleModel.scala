package modbat.examples

import modbat.dsl._

class ExampleModel extends Model {
  var x = 1

  "reset" -> "zero" := {
    assert(false)
  }

//  "reset" -> "one" := skip

  // transitions
//  "reset" -> "zero" := {
//    if (true) {
//      throw new IllegalArgumentException("")
//    } else {
//
//    }
//  }

//  "reset" -> "zero" := {
//    if (true) {
//      throw new IllegalArgumentException("")
//    } else {
//
//    }
//  } throws ("IllegalArgumentException")

//  "reset" -> "end" := {
//    if (false) {
//      throw new IllegalArgumentException("")
//    }
//    x = x + 1
//  } catches("IllegalArgumentException" -> "errState")
}
