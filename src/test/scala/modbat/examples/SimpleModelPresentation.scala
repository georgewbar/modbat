package modbat.examples

import modbat.dsl._

class SimpleModelPresentation extends Model {
  var counter: SimpleCounter = _
  // transitions
  "reset" -> "zero" := {
    counter = new SimpleCounter()
  }
  "zero" -> "zero" := {
    counter.toggleSwitch
  }
  "zero" -> "one" := {
    require(counter.isActive)
    counter.inc
  }
  "one" -> "two" := {
    counter.inc
  }
  "one" -> "end" := {
    assert (counter.value == 1)
  }
  "two" -> "end" := {
    assert (counter.value == 2)
  }
}
