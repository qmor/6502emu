package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SbcTests extends BaseCpuTest{

	@Test
	void testSbcImmediate() {
		var op = OpCodes.SBC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setA((short) 0x52);
		cpu.getF().setFlag(Flag.C, true); // Установка флага переноса (нет занимания)
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен (нет занимания)
		assertEquals(0x10, cpu.getA());
	}

	@Test
	void testSbcImmediateWithBorrow() {
		var op = OpCodes.SBC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setA((short) 0x52);
		cpu.getF().setFlag(Flag.C, false); // Очистка флага переноса (занимание)
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен (занимание)
		assertEquals(0x0F, cpu.getA()); // 0x52 - (0x42 + 1) = 0x0F
	}

	@Test
	void testSbcImmediateWithOverflow() {
		var op = OpCodes.SBC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x01;
		cpu.setA((short) 0x00);
		cpu.getF().setFlag(Flag.C, false); // Очистка флага переноса (занимание)
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен (занимание)
		assertEquals(0xFE, cpu.getA()); // 0x00 - (0x01 + 1) = 0xFE
	}

	@Test
	void testSbcImmediateWithOverflowAndNoBorrow() {
		var op = OpCodes.SBC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x01;
		cpu.setA((short) 0x00);
		cpu.getF().setFlag(Flag.C, true); // Установка флага переноса (нет занимания)
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен (нет занимания)
		assertEquals(0xFF, cpu.getA()); // 0x00 - 0x01 = 0xFF
	}

}
