package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyTests extends BaseCpuTest{
    @Test
    void StyZpTest()
    {
        var op = OpCodes.STY_ZP;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x10;
        cpu.setPC(0);
        cpu.setY((short) 14);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x10]);
    }

    @Test
    void StxZpYTest()
    {
        var op = OpCodes.STY_ZP_X;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0xf;
        cpu.setX((short) 1);
        cpu.setPC(0);
        cpu.setY((short) 14);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x10]);
    }

    @Test
    void StyAbsoluteTest()
    {
        var op = OpCodes.STY_ABSOLUTE;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x34;
        memory.data[2] = (byte) 0x12;
        cpu.setPC(0);
        cpu.setY((short) 14);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x1234]);
    }
}
