/*
 * Copyright 2014 Y12STUDIO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		Home, TwdAdjust, AlertInfo, PrefEdit, TxCheck, TxTimer
	}

	public enum FsmEvent {
		Open, Close, ValueError, ValueOk, Stop, Back, BackHome, Start, OpenTwdAdjust
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
				.on(FsmEvent.OpenTwdAdjust);
		builder.externalTransition().from(FsmState.TwdAdjust)
				.to(FsmState.TxCheck).on(FsmEvent.Close);
		builder.externalTransition().from(FsmState.TxCheck).to(FsmState.Home)
				.on(FsmEvent.Stop);
		builder.externalTransition().from(FsmState.TwdAdjust).to(FsmState.Home)
				.on(FsmEvent.BackHome);

		builder.externalTransition().from(FsmState.TxCheck)
				.to(FsmState.TxTimer).on(FsmEvent.Start);
		builder.externalTransition().from(FsmState.TxTimer)
				.to(FsmState.TxCheck).on(FsmEvent.Stop);
		
		builder.externalTransition().from(FsmState.TxTimer)
		.to(FsmState.AlertInfo).on(FsmEvent.ValueError);
		builder.externalTransition().from(FsmState.AlertInfo)
		.to(FsmState.TxCheck).on(FsmEvent.Back);

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
