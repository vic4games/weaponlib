package com.vicmatskiv.weaponlib.state;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Test;

public class StateManagerTest {
	
	private static enum MyState implements ManagedState {
		STATE1, STATE2_REQUESTED, STATE2(STATE2_REQUESTED), STATE3;

		private MyState permitRequested;
		
		private MyState() {
			this(null);
		}
		
		private MyState(MyState permitRequested) {
			this.permitRequested = permitRequested;
		}
		
		@Override
		public ManagedState permitRequested() {
			return permitRequested;
		}
	};
	
	private static class MyStateContainer implements ManagedStateContainer<ManagedState> {
		
		private AtomicReference<ManagedState> ref;
		
		MyStateContainer(ManagedState state) {
			this.ref = new AtomicReference<>(state);
		}

		@Override
		public boolean compareAndSetManagedState(ManagedState expectedState, ManagedState updateToState) {
			return ref.compareAndSet(expectedState, updateToState);
		}

		@Override
		public ManagedState getManagedState() {
			return ref.get();
		}

		@Override
		public long getLastManagedStateUpdateTimestamp() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	private static class MyStateContext implements StateContext {
		boolean flag;
		private ManagedStateContainer<ManagedState> stateContainer;
		
		//private StateContainer<? extends State> stateContainer;

		public MyStateContext(ManagedStateContainer<ManagedState> stateContainer) {
			this.stateContainer = stateContainer;
		}

		@Override
		public ManagedStateContainer<ManagedState> getStateContainer() {
			return stateContainer;
		}
	}
	

	@Test
	public void testAlways() {
		
		MyStateContext context = new MyStateContext(new MyStateContainer(MyState.STATE1));
		StateManager stateManager = new StateManager((s1, s2) -> s1 == s2)
				.in(MyStateContext.class)
				.change(MyState.STATE1).to(MyState.STATE2).allowed();
		
		StateManager.Result result;
		result = stateManager.changeState(context, MyState.STATE2);
		Assert.assertTrue(result.isStateChanged());
		Assert.assertEquals(MyState.STATE2, result.getState());
	}
	
	static class MyNetworkPermit extends Permit {

		public MyNetworkPermit(ManagedState state) {
			super(state);
		}
		
		@Override
		public Status getStatus() {
			return Status.GRANTED;
		}

	}
	
	@Test
	public void testAlwaysWithPermit() {
		
		class NetworkContext implements StateContext {
			Permit permit;
			
			private ManagedStateContainer<ManagedState> stateContainer;
			
			public NetworkContext(ManagedStateContainer<ManagedState> stateContainer) {
				this.stateContainer = stateContainer;
			}
			
			public Permit getPermit(ManagedState state) {
				if(permit == null) {
					permit = new MyNetworkPermit(state);
				}
				return permit;
			}
			
			@Override
			public ManagedStateContainer<ManagedState> getStateContainer() {
				return stateContainer;
			}
		};
		
		NetworkContext context = new NetworkContext(new MyStateContainer(MyState.STATE1));
		
		PermitManager<NetworkContext> permitManager = new PermitManager<NetworkContext>() {


			@Override
			public <T extends Permit> void registerEvaluator(Class<T> permitClass,
					BiConsumer<T, NetworkContext> evaluator) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <T extends Permit> void request(T permit, NetworkContext context,
					BiConsumer<Permit, NetworkContext> callback) {
				// TODO Auto-generated method stub
				
			}
		};
		
		StateManager stateManager = new StateManager((s1, s2) -> s1 == s2);
		stateManager
				.in(NetworkContext.class)
				.change(MyState.STATE1).to(MyState.STATE2)
				.withPermit((s, c) -> c.getPermit(s), permitManager)
				.allowed();
		
		StateManager.Result result;
		result = stateManager.changeState(context, MyState.STATE2);
		Assert.assertTrue(result.isStateChanged());
		Assert.assertEquals(MyState.STATE2.permitRequested(), result.getState());
		
		result = stateManager.changeState(context);
		Assert.assertTrue(result.isStateChanged());
		Assert.assertEquals(MyState.STATE2, result.getState());
	}
	
	@Test
	public void testAlwaysWithAction() {
		
		
		final String actionResult = "result";
		StateManager stateManager = new StateManager(
				(s1, s2) -> s1 == s2)
				.in(MyStateContext.class)
				.change(MyState.STATE1).to(MyState.STATE2).withAction((context, from, to) -> actionResult).allowed();
		
		MyStateContext context = new MyStateContext(new MyStateContainer(MyState.STATE1));		
		StateManager.Result result;
		result = stateManager.changeState(context, MyState.STATE2);
		Assert.assertTrue(result.isStateChanged());
		Assert.assertEquals(MyState.STATE2, result.getState());
		Assert.assertEquals(actionResult, result.getActionResult());
	}
	
	@Test
	public void testNoTransitionDefined() {
		MyStateContext context = new MyStateContext(new MyStateContainer(MyState.STATE1));		StateManager stateManager = new StateManager(
				(s1, s2) -> s1 == s2)
				.in(MyStateContext.class)
				.change(MyState.STATE1).to(MyState.STATE2).allowed();
		
		StateManager.Result result;
		result = stateManager.changeState(context, MyState.STATE3);
		Assert.assertFalse(result.isStateChanged());
		Assert.assertEquals(MyState.STATE1, result.getState());
	}
	
	@Test
	public void testConditionalTransition() {
		
		StateManager stateManager = new StateManager(
				(s1, s2) -> s1 == s2)
				.in(MyStateContext.class)
				.change(MyState.STATE1).to(MyState.STATE3).when((context) -> context.flag).allowed();
		
		StateManager.Result result;
		MyStateContext context = new MyStateContext(new MyStateContainer(MyState.STATE1));		
		// Try change state without condition evaluated to true
		result = stateManager.changeState(context, MyState.STATE3);
		Assert.assertFalse(result.isStateChanged());
		Assert.assertEquals(MyState.STATE1, result.getState());
		
		// Now change state with condition evaluated to true
		context.flag = true;
		result = stateManager.changeState(context, MyState.STATE3);
		Assert.assertTrue(result.isStateChanged());
		Assert.assertEquals(MyState.STATE3, result.getState());
	}
	
	@Test
	public void testMultipleTransitions() {
		
		String actionResult = "result";
		StateManager stateManager = new StateManager((s1, s2) -> s1 == s2)
				.in(MyStateContext.class)
				
				.change(MyState.STATE1).to(MyState.STATE2).allowed()
				
				.in(MyStateContext.class)
				.change(MyState.STATE2).to(MyState.STATE3)
					.withAction((context, from, to) -> actionResult)
					.when((context) -> context.flag)
				.allowed();
		
		StateManager.Result result;
		MyStateContext context = new MyStateContext(new MyStateContainer(MyState.STATE1));		
		result = stateManager.changeState(context, MyState.STATE2);
		Assert.assertEquals(MyState.STATE2, result.getState());
		
		context.flag = true;
		result = stateManager.changeState(context, MyState.STATE3);
		Assert.assertTrue(result.isStateChanged());
		Assert.assertEquals(MyState.STATE3, result.getState());
		
		Assert.assertEquals(actionResult, result.getActionResult());
	}
}
