package org.qmor;

import org.junit.jupiter.api.BeforeEach;

public class BaseCpuTest {
    protected final Memory memory = new Memory();
    protected final CPU cpu = new CPU(memory);

    @BeforeEach
    protected void setup() {
        cpu.reset();
    }
}
