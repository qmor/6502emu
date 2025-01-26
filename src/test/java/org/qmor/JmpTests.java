package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class JmpTests extends BaseCpuTest{

    @Test
    void jmpAbsoluteTest()
    {
        final var op = OpCodes.JMP_ABSOLUTE;
        var fLatch = cpu.getF();
        cpu.setPC(0);
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x88;
        memory.data[2] = (byte) 0x14;
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(fLatch, cpu.getF());
        assertEquals(0,cycles.get());
        assertEquals(0x1488,cpu.getPC());
    }


    @Test
    void jmpIndirectTest()
    {
        /**
         * JMP is the only 6502 instruction to support indirection. The instruction contains a 16 bit address which
         * identifies the location of the least significant byte of another 16 bit memory address which is the real
         * target of the instruction.

         * For example if location $0120 contains $FC and location $0121 contains $BA then the instruction JMP ($0120)
         * will cause the next instruction execution to occur at $BAFC (e.g. the contents of $0120 and $0121).
         */
        final var op = OpCodes.JMP_INDIRECT;
        var fLatch = cpu.getF();
        cpu.setPC(0);
        memory.data[0] = (byte) op.getOpcode();
        memory.data[1] = (byte) 0x11;
        memory.data[2] = (byte) 0x10;
        memory.data[0x1011] = (byte) 0x88;
        memory.data[0x1012] = (byte) 0x14;

        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(fLatch, cpu.getF());
        assertEquals(0,cycles.get());
        assertEquals(0x1488,cpu.getPC());
    }
}
