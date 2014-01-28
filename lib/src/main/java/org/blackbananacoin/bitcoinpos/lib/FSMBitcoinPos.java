package org.blackbananacoin.bitcoinpos.lib;

import java.util.Map;

import org.squirrelframework.foundation.component.SquirrelProvider;
import org.squirrelframework.foundation.fsm.DotVisitor;
import org.squirrelframework.foundation.fsm.ImmutableState;
import org.squirrelframework.foundation.fsm.StateMachineBuilder;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.impl.AbstractStateMachine;
import org.squirrelframework.foundation.util.TypeReference;

public class FSMBitcoinPos {

	public enum FsmState {
		Home, TwdAdjust, AlertInfo, PrefEdit, TxCheck
	}

	public enum FsmEvent {
		Open, Close, ValueError, ValueOk, Stop, Back
	}

	public static class StateMachineUi extends
			AbstractStateMachine<StateMachineUi, FsmState, FsmEvent, Void> {

		protected StateMachineUi(
				ImmutableState<StateMachineUi, FsmState, FsmEvent, Void> initialState,
				Map<FsmState, ImmutableState<StateMachineUi, FsmState, FsmEvent, Void>> states) {
			super(initialState, states);
		}
	}

	public static StateMachineUi createStateMachine() {
		StateMachineBuilder<StateMachineUi, FsmState, FsmEvent, Void> builder = StateMachineBuilderFactory
				.create(StateMachineUi.class, FsmState.class, FsmEvent.class,
						Void.class);
		builder.externalTransition().from(FsmState.Home).to(FsmState.TwdAdjust)
				.on(FsmEvent.Open);
		builder.externalTransition().from(FsmState.TwdAdjust)
				.to(FsmState.TxCheck).on(FsmEvent.Close);
		builder.externalTransition().from(FsmState.TxCheck).to(FsmState.Home)
				.on(FsmEvent.Stop);
		builder.externalTransition().from(FsmState.TwdAdjust).to(FsmState.Home)
				.on(FsmEvent.Back);

		builder.externalTransition().from(FsmState.Home).to(FsmState.PrefEdit)
				.on(FsmEvent.Open);
		builder.externalTransition().from(FsmState.PrefEdit).to(FsmState.Home)
				.on(FsmEvent.Back);

		builder.externalTransition().from(FsmState.TwdAdjust)
				.to(FsmState.AlertInfo).on(FsmEvent.ValueError);

		builder.externalTransition().from(FsmState.AlertInfo)
				.to(FsmState.TwdAdjust).on(FsmEvent.ValueOk);
		builder.externalTransition().from(FsmState.AlertInfo).to(FsmState.Home)
				.on(FsmEvent.Back);

		StateMachineUi stateMachine = builder.newStateMachine(FsmState.Home);
		return stateMachine;
	}

	public static void export(String pathname) {
		StateMachineUi fsm = createStateMachine();
		DotVisitor<StateMachineUi, FsmState, FsmEvent, Void> visitor = SquirrelProvider
				.getInstance()
				.newInstance(
						new TypeReference<DotVisitor<StateMachineUi, FsmState, FsmEvent, Void>>() {
						});
		fsm.accept(visitor);
		visitor.convertDotFile(pathname);
	}
	
	public static void main(String[] args) {
		export("fsm");
	}

}
