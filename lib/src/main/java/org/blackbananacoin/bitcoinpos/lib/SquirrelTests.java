package org.blackbananacoin.bitcoinpos.lib;

import java.util.Map;

import org.squirrelframework.foundation.fsm.ImmutableUntypedState;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

public class SquirrelTests {
	
	// 1. Define State Machine Event
	public enum FSMEvent {
		ToA, ToB, ToC, ToD
	}
	
	public enum FSMState {
		A, B, C, D
	}

	// 2. Define State Machine Class
	@StateMachineParameters(stateType = FSMState.class, eventType = FSMEvent.class, contextType = Integer.class)
	public static class StateMachineSample extends AbstractUntypedStateMachine {
		protected StateMachineSample(ImmutableUntypedState initialState,
				Map<Object, ImmutableUntypedState> states) {
			super(initialState, states);
		}

		protected void fromAToB(FSMState from, FSMState to, FSMEvent event,
				Integer context) {
			System.out.println("Transition from '" + from + "' to '" + to
					+ "' on event '" + event + "' with context '" + context
					+ "'.");
		}
		
		protected void fromBToC(FSMState from, FSMState to, FSMEvent event,
				Integer context) {
			System.out.println("Transition from '" + from + "' to '" + to
					+ "' on event '" + event + "' with context '" + context
					+ "'.");
		}

		protected void ontoB(FSMState from, FSMState to, FSMEvent event,
				Integer context) {
			System.out.println("Entry State \'" + to + "\'.");
		}
	}

}
