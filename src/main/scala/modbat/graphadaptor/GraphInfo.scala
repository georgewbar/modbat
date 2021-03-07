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
      this.state.name.equals(that.state.name)
    }
  }

  override def hashCode(): Int = {
    state.name.hashCode()
  }

  override def toString: String = {
    state.toString
  }
}

class EdgeData(val transitionLabel: String, val transitionType: TransitionType, val transition: Transition) {
  val originState: Node[StateData] = new Node(new StateData(transition.origin))
  val destinationState: Node[StateData] = new Node(new StateData(transition.dest))
  val transitionId: Int = transition.idx

  override def equals(other: Any): Boolean = {
    if (other == null || !other.isInstanceOf[EdgeData]) {
      false
    } else {
      val that: EdgeData = other.asInstanceOf[EdgeData]
      this.transitionId.equals(that.transitionId)
    }
  }

  override def hashCode(): Int = {
    transitionId.hashCode()
  }

  override def toString: String = {
    transition.toString()
  }
}


