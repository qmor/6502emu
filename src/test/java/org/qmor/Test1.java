package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class Test1 {
    @Test
    void testLdaImmediate() {
        Memory memory = new Memory();
        CPU cpu = new CPU(memory);
        cpu.reset();
        memory.data[0xfffc] = (byte) OpCodes.LDA_IM.getOpcode();
        var cycles = new AtomicInteger(2);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertTrue(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }
    @Test
    void testLdaZeroPage() {
        Memory memory = new Memory();
        CPU cpu = new CPU(memory);
        cpu.reset();
        memory.data[0xfffc] = (byte) OpCodes.LDA_ZP.getOpcode();
        memory.data[0xfffd] = 0x42;
        memory.data[0x0042] = 0x14;
        var cycles = new AtomicInteger(3);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getA());
    }


    @Test
    void testLdaZeroPageX() {
        Memory memory = new Memory();
        CPU cpu = new CPU(memory);
        cpu.reset();
        memory.data[0xfffc] = (byte) OpCodes.LDX_IM.getOpcode();
        memory.data[0xfffd] = 0x10;

        memory.data[0x0020] = 0x14;//value in zero page to be read to A

        memory.data[0xfffe] = (byte) OpCodes.LDA_ZP_X.getOpcode();
        memory.data[0xffff] = 0x10; //zero page address
        var cycles = new AtomicInteger(2);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x10,cpu.getX());

        cycles.set(4);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getA());

    }


}
