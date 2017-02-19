package com.vicmatskiv.weaponlib.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.state.Permit.Status;

public class StateManager<S extends ManagedState<S>, E extends ExtendedState<S>> {
	
	public class RuleBuilder<EE extends E> {
		
		private static final long DEFAULT_REQUEST_TIMEOUT = 5000L;
		
		private Aspect<S, EE> aspect;
		private S fromState;
		private S toState;
		private Action<S, EE> action;
		private Predicate<EE> predicate;
		private BiFunction<S, EE, Permit<S>> permitProvider;
		private BiFunction<S, EE, Boolean> stateUpdater;
		private PermitManager permitManager;
		private long requestTimeout = DEFAULT_REQUEST_TIMEOUT;
		
		public RuleBuilder(Aspect<S, EE> aspect) {
			this.aspect = aspect;
		}

		public RuleBuilder<EE> change(S fromState) {
			this.fromState = fromState;
			return this;
		}

		public RuleBuilder<EE> to(S state) {
			this.toState = state;
			return this;
		}
		
		public RuleBuilder<EE> when(Predicate<EE> predicate) {
			this.predicate = predicate;
			return this;
		}
		
		public RuleBuilder<EE> withPermit(
				BiFunction<S, EE, Permit<S>> permitProvider,
				BiFunction<S, EE, Boolean> stateUpdater,
				PermitManager permitManager) {
			this.permitProvider = permitProvider;
			this.stateUpdater = stateUpdater;
			this.permitManager = permitManager;
			return this;
		}
		
//		public RuleBuilder<A, T> withAction(Action<S> action) {
//			this.action = action;
//			return this;
//		}
		
		public RuleBuilder<EE> withAction(VoidAction<S, EE> action) {
			this.action = (context, from, to, permit) -> { action.execute(context, from, to, permit); return null;};
			return this;
		}
		
		public StateManager<S, E> allowed() {
			
			LinkedHashSet<TransitionRule<S, E>> contextRules = StateManager.this.contextRules.computeIfAbsent(
					aspect, c -> new LinkedHashSet<>());
			
			if(predicate == null) {
				predicate = c -> true;
			}
			
			if(action == null) {
				action = (c, f, t, p) -> null;
			}
			
			if(permitProvider != null) {
				

				contextRules.add(new TransitionRule<>(fromState, toState.permitRequested(), 
						(originalState) -> predicate.test(safeCast(originalState)), 
						(s, f, t, p) -> { permitManager.request(
								permitProvider.apply(t, safeCast(s)), s, this::applyPermit);
								return null;
						}));

				Predicate<E> requestTimedout = c -> 
						System.currentTimeMillis() > c.getStateUpdateTimestamp() + requestTimeout;
				
				contextRules.add(new TransitionRule<>(toState.permitRequested(), fromState, 
						requestTimedout,
						(c, f, t, p) -> action.execute(safeCast(c), f, t, p)));
				
			} else {
				contextRules.add(new TransitionRule<>(fromState, toState,
						c -> predicate.test(safeCast(c)), (c, f, t, p) -> action.execute(safeCast(c), f, t, p)));
			}
			
			return StateManager.this;
		}

		private void applyPermit(Permit<S> processedPermit, E updatedState) {
			// This is a permit granted callback which sets state to the final toState
			
//			if(updatedState != stateUpdater.apply(safeCast(updatedState))) {
//				System.out.println("Failed to match updated state");
//				return;
//			}
			
			S updateToState = processedPermit.getStatus() == Status.GRANTED ? toState : fromState;
			System.out.println("Applying permit with status " + processedPermit.getStatus()
				+ ", changing state to " + toState);
			
			if(stateUpdater.apply(updateToState, safeCast(updatedState))) {
				action.execute(safeCast(updatedState), fromState, toState, processedPermit);
			}
			
//			switch(processedPermit.getStatus()) {
//			
//			case GRANTED: 
//				System.out.println("Applying permit with status " + processedPermit.getStatus()
//					+ ", changing state to " + toState);
//				updatedState.setState(toState);
//				action.execute(safeCast(updatedState), fromState, toState, processedPermit);
//				break;
//			
//			default: 
//				System.out.println("Applying permit with status " + processedPermit.getStatus()
//					+ ", reverting state back to " + fromState);
//				updatedState.setState(fromState);
//				action.execute(safeCast(updatedState), fromState, toState, processedPermit);
//				break;
//			}
		}
	}

	
	public static interface StateComparator<S extends ManagedState<S>> {
		public boolean compare(S state1, S state2);
	}
	
	public class Result {
		private boolean stateChanged;
		private S state;
		protected Object actionResult;

		private Result(boolean stateChanged, S targetState) {
			this.stateChanged = stateChanged;
			this.state = targetState;
		}

		public boolean isStateChanged() {
			return stateChanged;
		}

		public S getState() {
			return state;
		}
		
		public Object getActionResult() {
			return actionResult;
		}
	}
	
	public static interface Action<S extends ManagedState<S>, EE> {
		public Object execute(EE extendedState, S fromState, S toState, Permit<S> permit);
	}
	
	public static interface VoidAction<S extends ManagedState<S>, EE> {
		public void execute(EE extendedState, S fromState, S toState, Permit<S> permit);
	}
	
	@SuppressWarnings("unchecked")
	private static <T, U> T safeCast(U u) {
		return (T) u;
	}
	
	private static class TransitionRule<S extends ManagedState<S>, E extends ExtendedState<S>> {
		S fromState;
		S toState;
		Predicate<E> predicate;
		Action<S, E> action;
		
		TransitionRule(
				S fromState, 
				S toState, 
				Predicate<E> predicate, 
				Action<S, E> action) {
			if(fromState == null) {
				throw new IllegalArgumentException("From-state cannot be null");
			}
			if(toState == null) {
				throw new IllegalArgumentException("To-state cannot be null");
			}
			this.fromState = fromState;
			this.toState = toState;
			this.predicate = predicate;
			this.action = action;
		}
		
		boolean matches(StateComparator<S> stateComparator, E context, S fromState, @SuppressWarnings("unchecked") S...targetStates) {
			
			boolean result = fromState == null || stateComparator.compare(this.fromState, fromState);
			result = result && (targetStates.length == 0 
						|| Arrays.stream(targetStates).anyMatch(
								targetState -> stateComparator.compare(toState, targetState) 
									|| stateComparator.compare(toState, targetState.permitRequested())));
			
			result = result && predicate.test(context);
			return result;
		}
	}
	
	private StateComparator<S> stateComparator;
	private Map<Aspect<S, ? extends E>, LinkedHashSet<TransitionRule<S, E>>> contextRules = new HashMap<>();
	
	public StateManager(StateComparator<S> stateComparator) {
		this.stateComparator = stateComparator;
	}
	
	public <EE extends E> RuleBuilder<EE> in(Aspect<S, EE> aspect) {
		return new RuleBuilder<EE>(aspect);
	}
	
	public Result changeState(Aspect<S, ? extends E> aspect, E extendedState, @SuppressWarnings("unchecked") S...targetStates) {
		S currentState = extendedState.getState();
		//System.out.println("Current state: " + currentState);
		return changeStateFromTo(aspect, extendedState, currentState, targetStates);
	}
	
	@SuppressWarnings("unchecked")
	public Result changeStateFromAnyOf(Aspect<S, ? extends E> aspect, E extendedState, S targetState, S...expectedFromStates) {
		S currentState = extendedState.getState();
		if(!Arrays.stream(expectedFromStates).anyMatch(expectedFromState -> stateComparator.compare(expectedFromState, currentState))) {
			return new Result(false, currentState);
		}
		return changeStateFromTo(aspect, extendedState, currentState, targetState);
	}
	
	protected Result changeStateFromTo(Aspect<S, ? extends E> aspect, E extendedState, S currentState, @SuppressWarnings("unchecked") S...targetStates) {
		
		if(extendedState == null) {
			return null;
		}
		
		//System.out.println("Current state: " + fromState);
		// If current state matches any of the target states, return immediately
		if(Arrays.stream(targetStates).anyMatch(target -> stateComparator.compare(currentState, target))) {
			return new Result(false, currentState);
		}
		TransitionRule<S, E> newStateRule = findNextStateRule(aspect, extendedState, currentState, targetStates);
		Result result = null;
		if(newStateRule != null) {
			extendedState.setState(newStateRule.toState);
			System.out.println("State changed from " + currentState + " to "+ newStateRule.toState);
			result = new Result(true, newStateRule.toState);
			if(newStateRule.action != null) {
				result.actionResult = newStateRule.action.execute(extendedState, currentState, newStateRule.toState, null);
			}
		} 
		
		if(result == null) {
			result = new Result(false, currentState);
		}
		
		return result;
	}


	private TransitionRule<S, E> findNextStateRule(Aspect<S, ? extends E> aspect, E extendedState, S currentState, @SuppressWarnings("unchecked") S... targetStates) {
				
		return contextRules.entrySet().stream()
				.filter(e -> e.getKey() == aspect) 
				.map(e -> e.getValue()) // convert entry to a list of rules
				.flatMap(LinkedHashSet::stream) // merge the rule list
				.filter(rule -> rule.matches(stateComparator, extendedState, currentState, targetStates)) // find matching rule
				.findFirst() // stop on the first found rule
				.orElse(null); // default to null if rule not found
	}
}
