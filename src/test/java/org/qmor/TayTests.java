package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TayTests extends BaseCpuTest{

    @Test
    void tayTest()
    {
        final var op = OpCodes.TAY;
        cpu.setPC(0);
        memory.data[0] = (byte) op.getOpcode();
        cpu.setA((short) 0x81);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x0,cpu.getX());
        assertEquals(0x81,cpu.getY());
        assertEquals(0x81,cpu.getA());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertTrue(cpu.getF().getAsBoolean(Flag.N));

    }
}
