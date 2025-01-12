package org.qmor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LdaTests {
    Memory memory = new Memory();
    CPU cpu = new CPU(memory);

    @BeforeEach
    public void setup() {
        cpu.reset();
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
}
