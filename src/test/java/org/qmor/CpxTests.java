package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpxTests extends BaseCpuTest{
	@Test
	void testCpxImmediateEqual() {
		var op = OpCodes.CPX_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setX((short) 0x42);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxImmediateLess() {
		var op = OpCodes.CPX_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setX((short) 0x30);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpxImmediateGreater() {
		var op = OpCodes.CPX_IM;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42;
		cpu.setX((short) 0x50);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}



	@Test
	void testCpxZpEqual() {
		var op = OpCodes.CPX_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = 0x30; // Value at zero-page address
		cpu.setX((short) 0x30);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxZpLess() {
		var op = OpCodes.CPX_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = 0x30; // Value at zero-page address
		cpu.setX((short) 0x20);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpxZpGreater() {
		var op = OpCodes.CPX_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = 0x30; // Value at zero-page address
		cpu.setX((short) 0x40);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxZpEqualNegative() {
		var op = OpCodes.CPX_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = (byte) 0x80; // -128 in 2's complement
		cpu.setX((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxZpLessNegative() {
		var op = OpCodes.CPX_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = (byte) 0x80; // -128 in 2's complement
		cpu.setX((short) 0x7F); // 127 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpxZpGreaterNegative() {
		var op = OpCodes.CPX_ZP;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x42; // Zero-page address
		memory.data[0x0042] = (byte) 0x7F; // 127 in 2's complement
		cpu.setX((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxAbsoluteEqual() {
		var op = OpCodes.CPX_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = 0x30; // Value at absolute address
		cpu.setX((short) 0x30);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxAbsoluteLess() {
		var op = OpCodes.CPX_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = 0x30; // Value at absolute address
		cpu.setX((short) 0x20);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpxAbsoluteGreater() {
		var op = OpCodes.CPX_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = 0x30; // Value at absolute address
		cpu.setX((short) 0x40);
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxAbsoluteEqualNegative() {
		var op = OpCodes.CPX_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = (byte) 0x80; // -128 in 2's complement
		cpu.setX((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertTrue(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}

	@Test
	void testCpxAbsoluteLessNegative() {
		var op = OpCodes.CPX_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = (byte) 0x80; // -128 in 2's complement
		cpu.setX((short) 0x7F); // 127 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertTrue(cpu.getF().getAsBoolean(Flag.N));
		assertFalse(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса сброшен
	}

	@Test
	void testCpxAbsoluteGreaterNegative() {
		var op = OpCodes.CPX_ABSOLUTE;
		memory.data[0xfffc] = (byte) op.getOpcode();
		memory.data[0xfffd] = 0x34; // Low byte of absolute address
		memory.data[0xfffe] = 0x12; // High byte of absolute address
		memory.data[0x1234] = (byte) 0x7F; // 127 in 2's complement
		cpu.setX((short) 0x80); // -128 in 2's complement
		var cycles = new AtomicInteger(op.getCycles());
		cpu.exec(cycles);
		assertEquals(0, cycles.get());
		assertFalse(cpu.getF().getAsBoolean(Flag.Z));
		assertFalse(cpu.getF().getAsBoolean(Flag.N));
		assertTrue(cpu.getF().getAsBoolean(Flag.C)); // Флаг переноса установлен
	}
}
