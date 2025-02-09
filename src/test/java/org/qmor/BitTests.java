package org.qmor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.qmor.OpCodes.BIT_ZP;

class BitTests extends BaseCpuTest{
    @Test
    void testZp_81and0()
    {
        final var op = BIT_ZP;
        cpu.setA((short) 0x81);
        cpu.setPC(0x1000);
        memory.data[0] = 0;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = 0;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0x81,cpu.getA());
        assertEquals(0,cpu.getF().getAsInt(Flag.N));
        assertEquals(1,cpu.getF().getAsInt(Flag.Z));
        assertEquals(0,cpu.getF().getAsInt(Flag.V));
    }


    @Test
    void testZp_81and1()
    {
        final var op = BIT_ZP;
        cpu.setA((short) 0x81);
        cpu.setPC(0x1000);
        memory.data[0] = 1;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = 0;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0x81,cpu.getA());
        assertEquals(0,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
        assertEquals(0,cpu.getF().getAsInt(Flag.V));
    }


    @Test
    void testAbsolute_81and0()
    {
        final var op = BIT_ZP;
        cpu.setA((short) 0x81);
        cpu.setPC(0x1000);
        memory.data[0] = 1<<6;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = 0;
        memory.data[0x1002] = 0;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0x81,cpu.getA());
        assertEquals(0,cpu.getF().getAsInt(Flag.N));
        assertEquals(1,cpu.getF().getAsInt(Flag.Z));
        assertEquals(1,cpu.getF().getAsInt(Flag.V));
    }


    @Test
    void testAbsolute_81and1()
    {
        final var op = BIT_ZP;
        cpu.setA((short) 0x81);
        cpu.setPC(0x1000);
        memory.data[0] = (byte) 0xC0;
        memory.data[0x1000] = (byte) op.getOpcode();
        memory.data[0x1001] = 0;
        memory.data[0x1002] = 0;
        var c = op.getCyclesAi();
        cpu.exec(c);
        assertEquals(0,c.get());
        assertEquals(0x81,cpu.getA());
        assertEquals(1,cpu.getF().getAsInt(Flag.N));
        assertEquals(0,cpu.getF().getAsInt(Flag.Z));
        assertEquals(1,cpu.getF().getAsInt(Flag.V));
    }
}
