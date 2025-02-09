package org.qmor;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplePgmTest extends BaseCpuTest{
    @Test
    void simpleTest()
    {
        var pgm = HexFormat.of().parseHex("a9ff85908d00804c0010");
        System.arraycopy(pgm,0,cpu.getMemory().data,0x1000,pgm.length);
        cpu.setPC(0x1000);
        cpu.exec(new AtomicInteger(10000));
        assertEquals(255, cpu.getSP());
        assertEquals(0, cpu.getX());
        assertEquals(0, cpu.getY());
    }


}
