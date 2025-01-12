package org.qmor;

import org.junit.jupiter.api.BeforeEach;

public class BaseCpuTest {
    protected Memory memory = new Memory();
    protected CPU cpu = new CPU(memory);

    @BeforeEach
    protected void setup() {
        cpu.reset();
    }
}
