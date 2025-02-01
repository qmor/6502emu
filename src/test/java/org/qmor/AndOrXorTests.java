package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.qmor.OpCodes.OR_IM;
import static org.qmor.OpCodes.OR_ZP;

class AndOrXorTests extends BaseCpuTest{
    @Test
    void andTests()
    {
        Set<OpCodes> codes = EnumSet.of(OpCodes.AND_IM,OpCodes.AND_ZP,OR_IM,OR_ZP);
        for (var op:codes) {
            cpu.reset();
            cpu.getMemory().reset();
            cpu.setPC(0);
            cpu.setA((short) 0x33);
            memory.data[0] = (byte) op.getOpcode();
            memory.data[1] = (byte) 0x10;
            memory.data[0x10] = 0x0;
            var cycles = new AtomicInteger(op.getCycles());
            cpu.exec(cycles);
            if (op == OpCodes.AND_IM)
            {
                assertEquals(0x10,cpu.getA());
                assertFalse(cpu.getF().getAsBoolean(Flag.N));
                assertFalse(cpu.getF().getAsBoolean(Flag.Z));
            }
            else if (op == OpCodes.AND_ZP)
            {
                assertEquals(0x0,cpu.getA());
                assertFalse(cpu.getF().getAsBoolean(Flag.N));
                assertTrue(cpu.getF().getAsBoolean(Flag.Z));
            }
            else if (op == OR_IM)
            {
                assertEquals(0x33,cpu.getA());
                assertFalse(cpu.getF().getAsBoolean(Flag.N));
                assertFalse(cpu.getF().getAsBoolean(Flag.Z));
            }
            else if (op == OR_ZP)
            {
                assertEquals(0x33,cpu.getA());
                assertFalse(cpu.getF().getAsBoolean(Flag.N));
                assertFalse(cpu.getF().getAsBoolean(Flag.Z));
            }
            else
            {
                throw new UnsupportedOperationException("not implemented");
            }

        }
    }

}
