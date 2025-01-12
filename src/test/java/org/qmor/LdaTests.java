package org.qmor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LdaTests extends BaseCpuTest{

    @Test
    void testLdaImmediate() {
        memory.data[0xfffc] = (byte) OpCodes.LDA_IM.getOpcode();
        memory.data[0xfffd] = (byte) 0x84;
        var cycles = new AtomicInteger(OpCodes.LDA_IM.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertTrue(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void testLdaZeroPage() {
        memory.data[0xfffc] = (byte) OpCodes.LDA_ZP.getOpcode();
        memory.data[0xfffd] = 0x42;
        memory.data[0x0042] = 0x14;
        var cycles = new AtomicInteger(OpCodes.LDA_ZP.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getA());
    }

    @Test
    void testLdaZeroPageX() {
        cpu.setX((short) 0x10);

        memory.data[0x0020] = 0x14;//value in zero page to be read to A
        memory.data[0xfffc] = (byte) OpCodes.LDA_ZP_X.getOpcode();
        memory.data[0xfffd] = 0x10; //zero page address
        var cycles = new AtomicInteger(OpCodes.LDA_ZP_X.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getA());
    }

    @Test
    void testLdaAbsolute()
    {
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(OpCodes.LDA_ABSOLUTE.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1488] = 56;
        cpu.setPC(0);
        var cycles = new AtomicInteger(OpCodes.LDA_ABSOLUTE.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getA());
    }


    @Test
    void testLdaAbsoluteX()
    {
        var op = OpCodes.LDA_ABSOLUTE_X;
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1489] = 56;
        cpu.setPC(0);
        cpu.setX((short) 1);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getA());


        //cross page

        System.arraycopy(HexFormat.of().parseHex("%02Xff14".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1500] = 56;
        cpu.setPC(0);
        cpu.setX((short) 1);
        cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(-1,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getA());

    }


    @Test
    void testLdaAbsoluteY()
    {
        var op = OpCodes.LDA_ABSOLUTE_Y;
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1489] = 56;
        cpu.setPC(0);
        cpu.setY((short) 1);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getA());


        //cross page

        System.arraycopy(HexFormat.of().parseHex("%02Xff14".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1500] = 56;
        cpu.setPC(0);
        cpu.setY((short) 1);
        cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(-1,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getA());

    }


    @Test
    void testLdaIndirectX() {
        var op = OpCodes.LDA_INDIRECT_X;
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = (byte) 0x20;
        memory.data[0x0030] = (byte) 0x56;
        memory.data[0x0031] = (byte) 0x78;
        memory.data[0x7856] = (byte) 0xCD;
        cpu.setX((short) 0x10);
        cpu.setPC(0xfffc);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0xcd,cpu.getA());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertTrue(cpu.getF().getAsBoolean(Flag.N));
    }

    @Test
    void testLdaIndirectY() {
        var op = OpCodes.LDA_INDIRECT_Y;
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = (byte) 0x20;
        memory.data[0x20] = (byte) 0xFE;
        memory.data[0x21] = (byte) 0x12;
        memory.data[0x12FF] = (byte) 0x78;

        cpu.setY((short) 0x1);
        cpu.setPC(0xfffc);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertEquals(0x78,cpu.getA());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));

        //page crossed variant

        memory.data[0x20] = (byte) 0xFF;
        memory.data[0x21] = (byte) 0x12;
        memory.data[0x1300] = (byte) 0x79;

        cpu.setA((short) 0);
        cpu.setY((short) 0x1);
        cpu.setPC(0xfffc);
        cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(-1,cycles.get());
        assertEquals(0x79,cpu.getA());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));

    }
}
