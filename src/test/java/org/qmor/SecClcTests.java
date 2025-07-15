package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecClcTests extends BaseCpuTest{
	@Test
	void testSec() {
		var op = OpCodes.SEC;
		memory.data[0xfffc] = (byte) op.getOpcode();
		cpu.getF().setFlag(Flag.C, false); // Очистка флага переноса
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testClc() {
		var op = OpCodes.CLC;
		memory.data[0xfffc] = (byte) op.getOpcode();
		cpu.getF().setFlag(Flag.C, true); // Установка флага переноса
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}
}
