package org.qmor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.qmor.OpCodes.*;

class EorTests extends BaseCpuTest{

    @Test
    void eorImmediateZero()
    {
        final var op = EOR_IM;
        cpu.setA((short) 0);
        cpu.setPC(0x1000);
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = 0;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0,cpu.getA());
        assertEquals(0,cpu.getF().getAsInt(Flag.N));
        assertEquals(1,cpu.getF().getAsInt(Flag.Z));
    }

    @Test
    void eorImmediateFF()
    {
        final var op = EOR_IM;
        cpu.setA((short) 0xff);
        cpu.setPC(0x1000);
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 0xff;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0,cpu.getA());
        assertEquals(0,cpu.getF().getAsInt(Flag.N));
        assertEquals(1,cpu.getF().getAsInt(Flag.Z));
    }

    @Test
    void eorZeroPage()
    {
        final var op = EOR_ZP;
        cpu.setA((short) 0xCC);
        cpu.setPC(0x1000);
        memory.data[0x0] = (byte) 0xaa;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 0;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0xcc^0xaa,cpu.getA());
        assertEquals(0,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
    }

    @Test
    void eorZeroPageX()
    {
        final var op = EOR_ZP_X;
        cpu.setA((short) 0xCC);
        cpu.setX((short) 1);
        cpu.setPC(0x1000);
        memory.data[0x1] = (byte) 0xaa;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 0;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0xcc^0xaa,cpu.getA());
        assertEquals(0,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
    }

    @Test
    void eorAbsolute()
    {
        final var op = EOR_ABSOLUTE;
        cpu.setA((short) 0xAA);
        cpu.setPC(0x1000);
        memory.data[0x1234] = (byte) 0x55;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 0x34;
        memory.data[0x1002] = (byte) 0x12;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0xaa^0x55,cpu.getA());
        assertEquals(1,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
    }

    @Test
    void eorAbsoluteX()
    {
        final var op = EOR_ABSOLUTE_X;
        cpu.setA((short) 0xAA);
        cpu.setPC(0x1000);
        cpu.setX((short) 1);
        memory.data[0x1235] = (byte) 0x5f;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 0x34;
        memory.data[0x1002] = (byte) 0x12;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0xaa^0x5f,cpu.getA());
        assertEquals(1,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
    }

    @Test
    void eorAbsoluteY()
    {
        final var op = EOR_ABSOLUTE_Y;
        cpu.setA((short) 0xAA);
        cpu.setPC(0x1000);
        cpu.setY((short) 1);
        memory.data[0x1235] = (byte) 0x5f;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 0x34;
        memory.data[0x1002] = (byte) 0x12;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0xaa^0x5f,cpu.getA());
        assertEquals(1,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
    }

    @Test
    void eorIndirectX()
    {
        final var op = EOR_INDIRECT_X;
        cpu.setA((short) 0xAA);
        cpu.setPC(0x1000);
        cpu.setX((short) 1);
        memory.data[0x1234] = (byte) 0x5f;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 2;
        memory.data[0x3] = (byte) 0x34;
        memory.data[0x4] = (byte) 0x12;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0xaa^0x5f,cpu.getA());
        assertEquals(1,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
    }


    @Test
    void eorIndirectY()
    {
        final var op = EOR_INDIRECT_Y;
        cpu.setA((short) 0xAA);
        cpu.setPC(0x1000);
        cpu.setY((short) 1);
        memory.data[0x1234] = (byte) 0x5f;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = (byte) 2;
        memory.data[0x2] = (byte) 0x33;
        memory.data[0x3] = (byte) 0x12;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0xaa^0x5f,cpu.getA());
        assertEquals(1,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
    }
}
