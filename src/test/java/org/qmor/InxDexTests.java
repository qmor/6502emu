package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InxDexTests extends BaseCpuTest{
    @Test
    void InxTest1()
    {
        var op = OpCodes.INX;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.setX((short) 0);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x1, cpu.getX());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void Inx255Test()
    {
        var op = OpCodes.INX;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.setX((short) 0xff);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x0, cpu.getX());
        assertTrue(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void DexTest1()
    {
        var op = OpCodes.DEX;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.setX((short) 1);
        cpu.exec(cycles);
        assertEquals(0x0, cpu.getX());
        assertEquals(0,cycles.get());
        assertTrue(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void DeyFfTest()
    {
        var op = OpCodes.DEY;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.setY((short) 0xff);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0xfe,cpu.getY());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertTrue(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void Inx127Test()
    {
        var op = OpCodes.INX;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.setX((short) 0x7f);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x80, cpu.getX());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertTrue(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void Dey0Test()
    {
        var op = OpCodes.DEY;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.setY((short) 0x0);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0xff,cpu.getY());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertTrue(cpu.getF().getAsBoolean(Flag.N));
    }

}
