package org.qmor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LdxTests extends BaseCpuTest{

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
    void testLdxZeroPageY() {
        cpu.setY((short) 0x10);
        final var op = OpCodes.LDX_ZP_Y;
        memory.data[0x0020] = 0x14;//value in zero page to be read to X
        memory.data[0xfffc] = (byte) op.getOpcode();
        memory.data[0xfffd] = 0x10; //zero page address
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(0x14,cpu.getX());
    }



    @Test
    void testLdxAbsolute()
    {
        final var op = OpCodes.LDX_ABSOLUTE;
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1488] = 56;
        cpu.setPC(0);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getX());
    }


    @Test
    void testLdxAbsoluteY()
    {
        var op = OpCodes.LDX_ABSOLUTE_Y;
        System.arraycopy(HexFormat.of().parseHex("%02X8814".formatted(op.getOpcode())), 0,memory.data,0,3);
        memory.data[0x1489] = 56;
        cpu.setPC(0);
        cpu.setY((short) 1);
        var cycles = new AtomicInteger(op.getCycles());
        cpu.exec(cycles);
        assertEquals(0,cycles.get());
        assertFalse(cpu.getF().getAsBoolean(Flag.Z));
        assertFalse(cpu.getF().getAsBoolean(Flag.N));
        assertEquals(56,cpu.getX());


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
        assertEquals(56,cpu.getX());

    }


}
