package com.vicmatskiv.weaponlib.state;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Test;

import io.netty.buffer.ByteBuf;

public class StateManagerTest {

	private static class MyState { 
		private static final ManagedState STATE1 = new ManagedState("State1");
		private static final ManagedState STATE2 = new ManagedState("State2");
		private static final ManagedState STATE3 = new ManagedState("State3");
	};
	
	private static class MyStateContainer implements StateContainer<ManagedState> {
		
		private AtomicReference<ManagedState> ref;
		
		MyStateContainer(ManagedState state) {
			this.ref = new AtomicReference<>(state);
		}

		@Override
		public boolean compareAndSetState(ManagedState expectedState, ManagedState updateToState) {
			return ref.compareAndSet(expectedState, updateToState);
		}

		@Override
		public ManagedState get() {
			return ref.get();
		}
		
	}
	
	private static class MyStateContext implements StateContext {
		boolean flag;
		private StateContainer<ManagedState> stateContainer;
		
		//private StateContainer<? extends State> stateContainer;

		public MyStateContext(StateContainer<ManagedState> stateContainer) {
			this.stateContainer = stateContainer;
		}

		@Override
		public StateContainer<ManagedState> getStateContainer() {
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
		private static RegisteredUuid typeUuid = Permit.register(MyNetworkPermit.class, "cd06aa6c-b43a-4264-8526-a7d97a8db63a");

		@Override
		public Status getStatus() {
			return Status.GRANTED;
		}

		@Override
		protected RegisteredUuid getTypeUuid() {
			return typeUuid;
		}

		@Override
		protected void init(ByteBuf buf) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ManagedState getTargetState() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	@Test
	public void testAlwaysWithPermit() {
		
		class NetworkContext implements StateContext {
			Permit permit = new MyNetworkPermit();
			
			private StateContainer<ManagedState> stateContainer;
			
			public NetworkContext(StateContainer<ManagedState> stateContainer) {
				this.stateContainer = stateContainer;
			}
			
			public Permit getPermit(ManagedState state) {
				return permit;
			}
			
			@Override
			public StateContainer<ManagedState> getStateContainer() {
				return stateContainer;
			}
		};
		
		NetworkContext context = new NetworkContext(new MyStateContainer(MyState.STATE1));
		
		PermitManager<NetworkContext> permitManager = new PermitManager<NetworkContext>() {
			@Override
			public void request(Permit permit, NetworkContext context, BiConsumer<Permit, NetworkContext> callback) {
				// TODO Auto-generated method stub
			}
		};
		
		StateManager stateManager = new StateManager((s1, s2) -> s1 == s2);
		stateManager
				.in(NetworkContext.class)
				.change(MyState.STATE1).to(MyState.STATE2)
				.withPermit(NetworkContext::getPermit, permitManager)
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
