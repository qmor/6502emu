package org.qmor;


import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClvTests extends BaseCpuTest {
	@Test
	void testClv() {
		var op = OpCodes.CLV;
		memory.data[0xfffc] = (byte) op.getOpcode();
		cpu.getF().setFlag(Flag.V, true); // Set the overflow flag
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.V)); // Overflow flag should be cleared
	}
}