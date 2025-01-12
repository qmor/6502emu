package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

//https://www.youtube.com/watch?v=qJgsuQoy9bc&t=29s
//https://www.nesdev.org/wiki/Instruction_reference#LDA
//http://www.6502.org/users/obelisk/6502/reference.html#JSR
//https://www.masswerk.at/6502/
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
    public void printRegs()
    {
        System.out.println("PC:%04X SP:%04X A:%02X X:%02X Y:%02X F:[%s]".formatted(PC,SP,A,X,Y,F.printFlags()));
    }
    public void reset()
    {
        PC = 0xFFFC;
        SP = 0x00FF;
        F.reset();
        A = X = Y = 0;
        memory.reset();
    }


    private int fetchWord(AtomicInteger cycles)
    {
        return fetchByte(cycles) | (fetchByte(cycles)  << 8);
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

    private void writeByte(AtomicInteger cycles, int address, byte value)
    {
        memory.data[address] = value;
        cycles.decrementAndGet();
    }
    private void writeWord(AtomicInteger cycles, int address, short word)
    {
        writeByte(cycles,address, (byte) (word&0xff));
        writeByte(cycles,address+1, (byte) ((word>>8)&0xff));
    }
    private int readWord(AtomicInteger cycles, int address)
    {
        return readByte(cycles,address)| readByte(cycles,address+1)<<8;
    }
    private void writeWordToStack(AtomicInteger cycles, short word)
    {
        writeWord(cycles,SP-2, word);
        setSP(SP-2);
    }
    private int readWordFromStack(AtomicInteger atomicInteger)
    {
        var word = readWord(atomicInteger,SP);
        setSP(SP+2);
        return word;
    }

    private void applyOpFunctions(OpCodes op, short data)
    {
        op.getFunctions().forEach(e->e.apply(F,data));
    }
    private boolean addressInSamePage(int a1, int a2)
    {
        return (a1 & 0x100) == (a2&0x100);
    }

    private void ldIm(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor)
    {
        destRegWriteAccessor.accept(fetchByte(cycles));
    }
    private void ldZp(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor)
    {
        final var zeroPageAddress = fetchByte(cycles);
        destRegWriteAccessor.accept(readByte(cycles,zeroPageAddress));
    }
    private void ldZpReg(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor,Supplier<Short> addRegReadAccessor )
    {
        final var zeroPageAddress = fetchByte(cycles);
        destRegWriteAccessor.accept(readByte(cycles,zeroPageAddress+addRegReadAccessor.get()));
        cycles.decrementAndGet();//because of zeroPageAddress+REG
    }
    private void ldAbsolute(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor)
    {
        var address = fetchWord(cycles);
        destRegWriteAccessor.accept((short) memory.data[address]);
        cycles.decrementAndGet();
    }
    private void ldAbsolutePlusAddrReg(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor, Supplier<Short> addRegReadAccessor )
    {
        final var address = fetchWord(cycles);
        final int addressWithAdd = address+addRegReadAccessor.get();
        if (!addressInSamePage(address,addressWithAdd))
        {
            cycles.decrementAndGet();
        }
        destRegWriteAccessor.accept((short) memory.data[addressWithAdd]);
        cycles.decrementAndGet();
    }

    public void exec(AtomicInteger cycles)
    {
        log.info("enter exec {}",cycles.get());
        while (cycles.get() > 0)
        {
            final var op =OpCodes.fromByte(fetchByte(cycles));
            switch (op)
            {
                case JSR ->
                {
                    var subAddress = fetchWord(cycles);
                    writeWordToStack(cycles,(short)(PC-1));
                    setPC(subAddress);
                    cycles.decrementAndGet();
                }
                case RTS ->
                {
                    var returnAddress = readWordFromStack(cycles);
                    setPC(returnAddress+1);
                    cycles.addAndGet(-3);
                }
                //LDA
                case LDA_IM -> ldIm(cycles,this::setA);
                case LDA_ZP -> ldZp(cycles,this::setA);
                case LDA_ZP_X -> ldZpReg(cycles,this::setA,this::getX);
                case LDA_ABSOLUTE -> ldAbsolute(cycles, this::setA);
                case LDA_ABSOLUTE_X -> ldAbsolutePlusAddrReg(cycles, this::setA, this::getX);
                case LDA_ABSOLUTE_Y -> ldAbsolutePlusAddrReg(cycles, this::setA, this::getY);

                //LDX
                case LDX_IM -> ldIm(cycles,this::setX);
                case LDX_ZP -> ldZp(cycles, this::setX);
                case LDX_ZP_Y -> ldZpReg(cycles, this::setX, this::getY);
                case LDX_ABSOLUTE -> ldAbsolute(cycles,this::setX);
                case LDX_ABSOLUTE_Y -> ldAbsolutePlusAddrReg(cycles,this::setX,this::getY);

                //LDY
                case LDY_IM -> ldIm(cycles,this::setY);
                case LDY_ZP -> ldZp(cycles, this::setY);
                case LDY_ZP_X -> ldZpReg(cycles, this::setY, this::getX);
                case LDY_ABSOLUTE -> ldAbsolute(cycles,this::setY);
                case LDY_ABSOLUTE_X -> ldAbsolutePlusAddrReg(cycles,this::setY,this::getX);

                case NOP -> cycles.decrementAndGet();
                default -> throw new UnsupportedOperationException("Unsupported opcode: " + op);
            }
            if (OpGroups.REG_A_LOAD_CODES.contains(op))
            {
                applyOpFunctions(op, A);
            }
            else if (OpGroups.REG_X_LOAD_CODES.contains(op))
            {
                applyOpFunctions(op, X);
            }
            else if (OpGroups.REG_Y_LOAD_CODES.contains(op))
            {
                applyOpFunctions(op, Y);
            }
            log.info("exec after {}, {}",op,cycles.get());
            printRegs();
        }
    }
}
