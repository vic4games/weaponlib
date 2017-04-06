package com.vicmatskiv.weaponlib.state;

import org.junit.Test;

import com.vicmatskiv.weaponlib.network.TypeRegistry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PermitTest {

	@Test
	public void test() {
		TestPermit p = new TestPermit(5);
		TypeRegistry typeRegistry = TypeRegistry.getInstance();
		
		typeRegistry.register(TestPermit.class);
		ByteBuf buf = Unpooled.buffer();
		typeRegistry.toBytes(p, buf);
		buf.resetReaderIndex();
		
		
		TestPermit restored = typeRegistry.fromBytes(buf);
		System.out.println(restored.getAmount());
	}
}
