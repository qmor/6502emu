package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StackOpTests extends BaseCpuTest {

    @Test
    void TSXtest()
    {
        var op = OpCodes.TSX;
        memory.data[0] = (byte) op.getOpcode();
        cpu.setPC(0);
        final var cycles = new AtomicInteger(op.getCycles());
        assertNotEquals(cpu.getX(), cpu.getSP());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(cpu.getX(), cpu.getSP());
        assertEquals(0x80,cpu.getF().getByteValue());
    }

    @Test
    void TXStest()
    {
        var op = OpCodes.TXS;
        memory.data[0] = (byte) op.getOpcode();
        cpu.setPC(0);
        cpu.setX((short) 10);
        final var latchF = cpu.getF().getCopy();
        final var cycles = new AtomicInteger(op.getCycles());
        assertNotEquals(cpu.getX(), cpu.getSP());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(cpu.getX(), cpu.getSP());
        assertEquals(latchF,cpu.getF());
    }

    @Test
    void PHA_PLA_Test()
    {
        var op = OpCodes.PHA;
        memory.data[0] = (byte) op.getOpcode();
        cpu.setPC(0);
        cpu.setA((short) 0x80);
        final var latchF = cpu.getF().getCopy();
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(latchF,cpu.getF());
        assertEquals(0,cycles.get());
        assertEquals(0xfe, cpu.getSP());
        assertEquals(0x80,cpu.getMemory().data[CPU.STACK_LOW+cpu.getSP()]&0xff);
        op = OpCodes.PLA;
        memory.data[1] = (byte) op.getOpcode();
        cpu.setA((short) 12);
        cycles.set(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0xff, cpu.getSP());
        assertEquals(0x80,cpu.getA());
        assertEquals(0x80, cpu.getF().getByteValue());
    }


    @Test
    void PHP_PLP_Test()
    {
        var op = OpCodes.PHP;
        memory.data[0] = (byte) op.getOpcode();
        cpu.setPC(0);
        cpu.getF().setByteValue((short) 0x12);
        final var latchF = cpu.getF().getCopy();
        final var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0xfe, cpu.getSP());
        assertEquals(0x12,cpu.getMemory().data[CPU.STACK_LOW+cpu.getSP()]&0xff);
        assertEquals(latchF,cpu.getF());

        cpu.getF().setByteValue((short) 0);
        op = OpCodes.PLP;
        memory.data[1] = (byte) op.getOpcode();
        cycles.set(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0xff, cpu.getSP());
        assertEquals(0x12,cpu.getF().getByteValue());
    }
}
