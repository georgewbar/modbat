package modbat.examples

import modbat.dsl._

class SimpleModel2 extends Model {
  var counter: SimpleCounter = _

  // transitions
  "reset" -> "zero" := {
    counter = new SimpleCounter()
  }
  "reset" -> "ran_num" := {
    counter = new SimpleCounter()
    counter.setValue(choose(0, 5))
  }
  "zero" -> "zero" := {
    counter.toggleSwitch
  }
  "zero" -> "one" := {
    require(counter.isActive)
    counter.inc
  }
  "one" -> "two" := {
    require(counter.isActive)
    counter.inc
  }
  "zero" -> "two" := {
    counter.inc2
  }
  "two" -> "end" := {
    assert (counter.value == 2)
  }
  "ran_num" -> "ran_num_p_2" := {
    require(counter.value == 3)
    counter.inc2
  }
  "ran_num_p_2" -> "end" := {
    assert(counter.value == 5)
  }
}
