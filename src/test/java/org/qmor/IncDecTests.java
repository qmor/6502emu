package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class IncDecTests extends BaseCpuTest{
    @Test
    void incZpTest()
    {
        var op = OpCodes.INC_ZP;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.getMemory().data[0] = 0;
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x0, cpu.getX());
        assertEquals(0x1, cpu.getMemory().data[0]);
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void incZpXTest()
    {
        var op = OpCodes.INC_ZP_X;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.getMemory().data[1] = 1;
        cpu.setX((short) 1);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x1, cpu.getX());
        assertEquals(0x2, cpu.getMemory().data[1]);
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void decAbsoluteTest()
    {
        var op = OpCodes.DEC_ABSOLUTE;
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = (byte) 0x34;
        memory.data[0xfffe] = (byte) 0x12;
        memory.data[0x1234] = 0;
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x0, cpu.getX());
        assertEquals(0xff, cpu.getMemory().data[0x1234]&0xff);
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertTrue(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void decAbsoluteXTest()
    {
        var op = OpCodes.DEC_ABSOLUTE_X;
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = (byte) 0x34;
        memory.data[0xfffe] = (byte) 0x12;
        cpu.setX((short) 1);
        memory.data[0x1235] = 1;
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x1, cpu.getX());
        assertEquals(0x0, cpu.getMemory().data[0x1235]&0xff);
        assertTrue(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }
}
