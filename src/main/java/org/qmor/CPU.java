package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
//https://www.youtube.com/watch?v=qJgsuQoy9bc&t=29s
//https://www.nesdev.org/wiki/Instruction_reference#LDA
@RequiredArgsConstructor
@Getter
@Slf4j
public class CPU {
    @Setter
    private int PC;
    @Setter
    private int SP;
    @Setter
    private short A;
    @Setter
    private short X;
    @Setter
    private short Y;
    @Setter
    private FlagRegister F = new FlagRegister();

    private final Memory memory;
    public void reset()
    {
        PC = 0xFFFC;
        SP = 0x0100;
        F.reset();
        A = X = Y = 0;
        memory.reset();
    }

    /**
     * read byte from PC and increments pc
     * @param cycles
     * @return
     */
    private short fetchByte(AtomicInteger cycles)
    {
        final var res = memory.data[PC]&0xff;
        PC++;
        cycles.decrementAndGet();
        return (short) res;
    }

    private short readByte(AtomicInteger cycles, int address)
    {
        cycles.decrementAndGet();
        return (short) (memory.data[address]&0xff);
    }

    private void applyOpFunctions(OpCodes op, short data)
    {
        op.getFunctions().forEach(e->e.apply(F,data));
    }
    public void exec(AtomicInteger cycles)
    {
        log.info("enter exec {}",cycles.get());
        while (cycles.get() > 0)
        {
            final var op =OpCodes.fromByte(fetchByte(cycles));
            switch (op)
            {
                case LDA_IM -> {
                    A = fetchByte(cycles);
                    applyOpFunctions(op, A);
                }
                case LDA_ZP ->
                {
                    final var zeroPageAddress = fetchByte(cycles);
                    A = readByte(cycles,zeroPageAddress);
                    applyOpFunctions(op, A);
                }
                case LDA_ZP_X -> {
                    final var zeroPageAddress = fetchByte(cycles);
                    A = readByte(cycles,zeroPageAddress+X);
                    cycles.decrementAndGet();//because of zeroPageAddress+X
                    applyOpFunctions(op, A);
                }

                case LDX_IM -> {
                    X = fetchByte(cycles);
                    applyOpFunctions(op, X);
                }
                default -> {
                    throw new UnsupportedOperationException("Unsupported opcode: " + op);
                }
            }
            log.info("exec after {}, {}",op,cycles.get());
        }
    }
}
