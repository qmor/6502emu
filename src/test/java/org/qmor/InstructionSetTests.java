package org.qmor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InstructionSetTests  extends BaseCpuTest{

    @Test
    void JstAndRtsTest()
    {
        cpu.setPC(0x0600);
        var program = HexFormat.of().parseHex("200007EAEAEA");//JSR $0700 NOP NOP NOP
        System.arraycopy(program,0,memory.data,0x0600,program.length);
        memory.data[0x0700] = (byte) OpCodes.RTS.getOpcode();
        var cycles = new AtomicInteger(OpCodes.JSR.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x0700, cpu.getPC());
        assertEquals(0xFD, cpu.getSP());
        assertEquals(0x02,cpu.getMemory().data[cpu.getSP()]&0xff);
        assertEquals(0x06,cpu.getMemory().data[cpu.getSP()+1]&0xff);
        cycles.set(OpCodes.RTS.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0xFF, cpu.getSP());
        assertEquals(0x0603, cpu.getPC());

        cycles.set(6);
        cpu.exec(cycles);
        assertEquals(0,cycles.get());

    }

    @Test
    void testJsr()
    {
        cpu.setPC(0xff00);
        var op = OpCodes.JSR;
        memory.data[0xff00] = (byte) op.getOpcode();
        memory.data[0xff01] = (byte) 0x12;
        memory.data[0xff02] = (byte) 0x13;
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x1312, cpu.getPC());
        assertEquals(0xfd,cpu.getSP());
        assertEquals(0x02,cpu.getMemory().data[cpu.getSP()]&0xff);
        assertEquals(0xff,cpu.getMemory().data[cpu.getSP()+1]&0xff);
    }








}
