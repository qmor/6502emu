package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdcTests extends BaseCpuTest {
	@Test
	void testAdcImmediate() {
		var op = OpCodes.ADC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x20;
		cpu.setA((short) 0x10);
		cpu.getF().setFlag(Flag.C, false); // Очистка флага переноса
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса не установлен
		assertEquals(0x30, cpu.getA());
	}

	@Test
	void testAdcImmediateWithCarry() {
		var op = OpCodes.ADC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setA((short) 0x10);
		cpu.getF().setFlag(Flag.C, true); // Установка флага переноса
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
		assertEquals(0x53, cpu.getA()); // 0x10 + 0x42 + 0x01 = 0x53
	}

	@Test
	void testAdcImmediateWithOverflow() {
		var op = OpCodes.ADC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x01;
		cpu.setA((short) 0xFF);
		cpu.getF().setFlag(Flag.C, false); // Очистка флага переноса
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
		assertEquals(0x00, cpu.getA()); // 0xFF + 0x01 = 0x00
	}

	@Test
	void testAdcImmediateWithOverflowAndCarry() {
		var op = OpCodes.ADC_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x01;
		cpu.setA((short) 0xFF);
		cpu.getF().setFlag(Flag.C, true); // Установка флага переноса
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
		assertEquals(0x01, cpu.getA()); // 0xFF + 0x01 + 0x01 = 0x01
	}
}
