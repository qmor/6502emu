package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpyTests extends BaseCpuTest{
	@Test
	void testCpyImmediateEqual() {
		var op = OpCodes.CPY_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setY((short) 0x42);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyImmediateLess() {
		var op = OpCodes.CPY_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setY((short) 0x30);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpyImmediateGreater() {
		var op = OpCodes.CPY_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setY((short) 0x50);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}
}
