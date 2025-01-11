package org.qmor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InstructionSetTests {

    Memory memory = new Memory();
    CPU cpu = new CPU(memory);

    @BeforeEach
    public void setup() {
        cpu.reset();
    }
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
    void testLdxImmediate() {
        var op = OpCodes.LDX_IM;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertTrue(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
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
    void testLdxZeroPage() {
        var op = OpCodes.LDX_ZP;
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = 0x42;
        memory.data[0x0042] = 0x14;
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getX());
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
    void testLdxZeroPageY() {
        cpu.setY((short) 0x10);

        memory.data[0x0020] = 0x14;//value in zero page to be read to X
        memory.data[0xfffc] = (byte) OpCodes.LDX_ZP_Y.getOpcode();
        memory.data[0xfffd] = 0x10; //zero page address
        var cycles = new AtomicInteger(OpCodes.LDX_ZP_Y.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getX());
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
    void testLdxAbsolute()
    {
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(OpCodes.LDX_ABSOLUTE.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1488] = 56;
        cpu.setPC(0);
        var cycles = new AtomicInteger(OpCodes.LDX_ABSOLUTE.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getX());
    }


}
