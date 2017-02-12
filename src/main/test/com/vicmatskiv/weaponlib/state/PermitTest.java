package com.vicmatskiv.weaponlib.state;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PermitTest {

	@Test
	public void test() {
		TestPermit p = new TestPermit(5);
		
		ByteBuf buf = Unpooled.buffer();
		p.serialize(buf);
		buf.resetReaderIndex();
		
		TestPermit restored = UniversalObject.fromBytes(buf);
		System.out.println(restored.getAmount());
	}
}
