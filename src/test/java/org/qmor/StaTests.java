package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StaTests extends BaseCpuTest{
    @Test
    void StaZpTest()
    {
        var op = OpCodes.STA_ZP;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x10;
        cpu.setPC(0);
        cpu.setA((short) 14);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x10]);
    }

    @Test
    void StaZpXTest()
    {
        var op = OpCodes.STA_ZP_X;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0xf;
        cpu.setX((short) 1);
        cpu.setPC(0);
        cpu.setA((short) 14);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x10]);
    }



    @Test
    void StaAbsoluteTest()
    {
        var op = OpCodes.STA_ABSOLUTE;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x34;
        memory.data[2] = (byte) 0x12;
        cpu.setPC(0);
        cpu.setA((short) 14);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x1234]);
    }

    @Test
    void StaAbsoluteXTest()
    {
        var op = OpCodes.STA_ABSOLUTE_X;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x33;
        memory.data[2] = (byte) 0x12;
        cpu.setPC(0);
        cpu.setA((short) 14);
        cpu.setX((short) 1);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x1234]);
    }

    @Test
    void StaAbsoluteYTest()
    {
        var op = OpCodes.STA_ABSOLUTE_Y;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x33;
        memory.data[2] = (byte) 0x12;
        cpu.setPC(0);
        cpu.setA((short) 14);
        cpu.setY((short) 1);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x1234]);
    }


    @Test
    void StaIndirectXTest()
    {
        var op = OpCodes.STA_INDIRECT_X;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x55;
        memory.data[0x56] = 0x34;
        memory.data[0x57] = 0x12;
        cpu.setPC(0);
        cpu.setA((short) 14);
        cpu.setX((short) 1);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x1234]);
    }


    @Test
    void StaIndirectYTest()
    {
        var op = OpCodes.STA_INDIRECT_Y;
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x55;
        memory.data[0x55] = 0x33;
        memory.data[0x56] = 0x12;
        cpu.setPC(0);
        cpu.setA((short) 14);
        cpu.setY((short) 1);
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(14,memory.data[0x1234]);
    }
}
