package org.qmor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class LdyTests {

    Memory memory = new Memory();
    CPU cpu = new CPU(memory);

    @BeforeEach
    public void setup() {
        cpu.reset();
    }
    @Test
    void testLdyImmediate() {
        var op = OpCodes.LDY_IM;
        memory.data[0xfffc] = (byte) op.getOpcode();
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertTrue(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
    }




    @Test
    void testLdyZeroPage() {
        var op = OpCodes.LDY_ZP;
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = 0x42;
        memory.data[0x0042] = 0x14;
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getY());
    }


    @Test
    void testLdyZeroPageX() {
        cpu.setX((short) 0x10);
        var op = OpCodes.LDY_ZP_X;
        memory.data[0x0020] = 0x14;//value in zero page to be read to X
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = 0x10; //zero page address
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getY());
    }



    @Test
    void testLdyAbsolute()
    {
        final var op = OpCodes.LDY_ABSOLUTE;
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1488] = 56;
        cpu.setPC(0);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getY());
    }


    @Test
    void testLdyAbsoluteX()
    {
        var op = OpCodes.LDY_ABSOLUTE_X;
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1489] = 56;
        cpu.setPC(0);
        cpu.setX((short) 1);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getY());


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
        assertEquals(56,cpu.getY());

    }
}
