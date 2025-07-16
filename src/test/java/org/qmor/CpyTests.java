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


	@Test
	void testCpyZpEqual() {
		var op = OpCodes.CPY_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = 0x30; // Value at zero-page address
		cpu.setY((short) 0x30);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyZpLess() {
		var op = OpCodes.CPY_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = 0x30; // Value at zero-page address
		cpu.setY((short) 0x20);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpyZpGreater() {
		var op = OpCodes.CPY_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = 0x30; // Value at zero-page address
		cpu.setY((short) 0x40);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyZpEqualNegative() {
		var op = OpCodes.CPY_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = (byte) 0x80; // -128 in 2's complement
		cpu.setY((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyZpLessNegative() {
		var op = OpCodes.CPY_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = (byte) 0x80; // -128 in 2's complement
		cpu.setY((short) 0x7F); // 127 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpyZpGreaterNegative() {
		var op = OpCodes.CPY_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = (byte) 0x7F; // 127 in 2's complement
		cpu.setY((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyAbsoluteEqual() {
		var op = OpCodes.CPY_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = 0x30; // Value at absolute address
		cpu.setY((short) 0x30);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyAbsoluteLess() {
		var op = OpCodes.CPY_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = 0x30; // Value at absolute address
		cpu.setY((short) 0x20);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpyAbsoluteGreater() {
		var op = OpCodes.CPY_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = 0x30; // Value at absolute address
		cpu.setY((short) 0x40);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyAbsoluteEqualNegative() {
		var op = OpCodes.CPY_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = (byte) 0x80; // -128 in 2's complement
		cpu.setY((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpyAbsoluteLessNegative() {
		var op = OpCodes.CPY_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = (byte) 0x80; // -128 in 2's complement
		cpu.setY((short) 0x7F); // 127 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpyAbsoluteGreaterNegative() {
		var op = OpCodes.CPY_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = (byte) 0x7F; // 127 in 2's complement
		cpu.setY((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

}
