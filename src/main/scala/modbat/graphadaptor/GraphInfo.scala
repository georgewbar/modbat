package modbat.graphadaptor

import modbat.dsl.{State, Transition}
import modbat.graph.Node

sealed trait TransitionType

case class NormalTransition(isDeterministic: Boolean) extends TransitionType

case class ExpectedExceptionTransition(isDeterministic: Boolean) extends TransitionType

//====================================

class StateData(val state: State) {

  override def equals(other: Any): Boolean = {
    if (other == null || !other.isInstanceOf[StateData]) {
      false
    } else {
      val that: StateData = other.asInstanceOf[StateData]

      // TODO GR_NR: either the state reference is compared (globally) or
      // state names locally
      this.state.name.equals(that.state.name)
//      this.state == that.state
    }
  }

  override def hashCode(): Int = {
    state.name.hashCode()
  }
}

class EdgeData(val transitionLabel: String, val transitionType: TransitionType, val transition: Transition) {
  val originState: Node[StateData] = new Node(new StateData(transition.origin))
  val destinationState: Node[StateData] = new Node(new StateData(transition.dest))
  val transitionId: Int = transition.idx // ask Cyrille if transition.idx is changed during the execution

  override def equals(other: Any): Boolean = {
    if (other == null || !other.isInstanceOf[EdgeData]) {
      false
    } else {
      val that: EdgeData = other.asInstanceOf[EdgeData]

      // TODO GR_NR:  within the model, transition-id is unique globally
      this.transitionId.equals(that.transitionId)
//      this.transition == that.transition
    }
  }

  override def hashCode(): Int = {
    transitionId.hashCode()
  }
}


