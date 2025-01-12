package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TyaTests extends BaseCpuTest{

    @Test
    void tyaTest()
    {
        final var op = OpCodes.TYA;
        cpu.setPC(0);
        memory.data[0] = (byte) op.getOpcode();
        cpu.setY((short) 12);
        cpu.setA((short) 0);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(12,cpu.getY());
        assertEquals(12,cpu.getA());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));

    }
}
