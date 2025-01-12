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
    public String printRegs()
    {
        return "PC:%04X SP:%04X A:%02X X:%02X Y:%02X F:[%s]".formatted(PC,SP,A,X,Y,F.printFlags());
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
     * read byte from memory at PC and increments PC
     * @param cycles - cycles holder
     * @return - fetched byte
     */
    private short fetchByte(AtomicInteger cycles)
    {
        final var res = memory.data[PC]&0xff;
        PC++;
        cycles.decrementAndGet();
        return (short) res;
    }

    /**
     * read byte from memory at address
     * @param cycles - cycles holder
     * @param address - address to read byte from
     * @return - byte
     */
    private short readByte(AtomicInteger cycles, int address)
    {
        cycles.decrementAndGet();
        return (short) (memory.data[address]&0xff);
    }

    /**
     * Read little endian word from memory at address
     * @param cycles - cycles holder
     * @param address - address to read word from
     * @return word
     */
    private int readWord(AtomicInteger cycles, int address)
    {
        return readByte(cycles,address)| readByte(cycles,address+1)<<8;
    }

    /**
     * Write byte value to memory at address
     * @param cycles - cycles holder
     * @param address - address to write byte into
     * @param value - byte to write
     */
    private void writeByte(AtomicInteger cycles, int address, byte value)
    {
        memory.data[address] = value;
        cycles.decrementAndGet();
    }

    /**
     * Write little endian word to memory at address
     * @param cycles - cycles holder
     * @param address - address to write word into
     * @param word - word to write
     */
    private void writeWord(AtomicInteger cycles, int address, short word)
    {
        writeByte(cycles,address, (byte) (word&0xff));
        writeByte(cycles,address+1, (byte) ((word>>8)&0xff));
    }

    /**
     * Write Little endian word to stack at SP and decrement SP by 2
     * @param cycles - cycles holder
     * @param word - word to place to stack
     */
    private void writeWordToStack(AtomicInteger cycles, short word)
    {
        writeWord(cycles,SP-2, word);
        setSP(SP-2);
    }

    /**
     * read little endian word from stack at SP and increment SP by 2
     * @param cycles - cycles holder
     * @return word fetched from stack
     */
    private int readWordFromStack(AtomicInteger cycles)
    {
        var word = readWord(cycles,SP);
        setSP(SP+2);
        return word;
    }

    private void applyOpFunctions(OpCodes op, short data)
    {
        op.getFunctions().forEach(e->e.apply(F,data));
    }

    /**
     * check if two adresses are within the same 256 bytes page
     * @param a1 first address
     * @param a2 second address
     * @return true if addresses are within same page
     */
    private boolean addressInSamePage(int a1, int a2)
    {
        return (a1 & 0x100) == (a2&0x100);
    }

    /**
     * Load register immediate
     * @param cycles - cycles holder
     * @param destRegWriteAccessor accessor to write destination reg
     */
    private void ldIm(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor)
    {
        destRegWriteAccessor.accept(fetchByte(cycles));
    }

    /**
     * load register zero page
     * @param cycles - cycles holder
     * @param destRegWriteAccessor accessor to write destination reg
     */
    private void ldZp(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor,Supplier<Short> addRegReadAccessor)
    {
        var addr = fetchByte(cycles);
        if (addRegReadAccessor!=null)
        {
            addr+=addRegReadAccessor.get();
            cycles.decrementAndGet();
        }
        destRegWriteAccessor.accept(readByte(cycles,addr));
    }

    private void stZp(AtomicInteger cycles, Supplier<Short> regToStore, Supplier<Short> addAddrReg)
    {
        var addr = fetchByte(cycles)&0xff;
        if (addAddrReg!=null)
        {
            addr+=addAddrReg.get();
            cycles.decrementAndGet();
        }
        writeByte(cycles,addr,regToStore.get().byteValue());
    }

    private void stAbsolute(AtomicInteger cycles, Supplier<Short> regToStore,  Supplier<Short> addAddrReg)
    {
        var addr = fetchWord(cycles)&0xffff;
        if (addAddrReg!=null)
        {
            addr+=addAddrReg.get();
            cycles.decrementAndGet();
        }
        writeByte(cycles,addr,regToStore.get().byteValue());
    }




    /**
     * load register absolute mode
     * @param cycles - cycles holder
     * @param destRegWriteAccessor accessor to write destination reg
     */
    private void ldAbsolute(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor)
    {
        var address = fetchWord(cycles);
        destRegWriteAccessor.accept((short) memory.data[address]);
        cycles.decrementAndGet();
    }

    /**
     * load regiser absolute mode with additional register
     * @param cycles - cycles holder
     * @param destRegWriteAccessor accessor to write destination reg
     * @param addRegReadAccessor
     */
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

    /**
     * transfer (copy) value of one register to another
     * @param cycles - cycles holder
     * @param destRegWriteAccessor accessor to write destination reg
     * @param srcRegReadAccessor accessor to read source reg
     */
    private void transferRegister(AtomicInteger cycles, Consumer<Short> destRegWriteAccessor, Supplier<Short> srcRegReadAccessor )
    {
        cycles.decrementAndGet();
        destRegWriteAccessor.accept(srcRegReadAccessor.get());
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
                case LDA_ZP -> ldZp(cycles,this::setA,null);
                case LDA_ZP_X -> ldZp(cycles,this::setA,this::getX);
                case LDA_ABSOLUTE -> ldAbsolute(cycles, this::setA);
                case LDA_ABSOLUTE_X -> ldAbsolutePlusAddrReg(cycles, this::setA, this::getX);
                case LDA_ABSOLUTE_Y -> ldAbsolutePlusAddrReg(cycles, this::setA, this::getY);
                case LDA_INDIRECT_X -> {
                    final var instrAddr = fetchByte(cycles)&0xff;
                    final var baseAddr = (instrAddr+X)&0xff;     cycles.decrementAndGet();
                    final var addr = readWord(cycles,baseAddr);
                    A = readByte(cycles,addr);
                }
                case LDA_INDIRECT_Y -> {
                    final var instrAddr = fetchByte(cycles)&0xff;
                    final var addr = readWord(cycles,instrAddr);
                    final var finalAddr = addr+Y;
                    if (!addressInSamePage(addr,finalAddr))
                        cycles.decrementAndGet();
                    A = readByte(cycles,finalAddr);
                }

                //LDX
                case LDX_IM -> ldIm(cycles,this::setX);
                case LDX_ZP -> ldZp(cycles, this::setX,null);
                case LDX_ZP_Y -> ldZp(cycles, this::setX, this::getY);
                case LDX_ABSOLUTE -> ldAbsolute(cycles,this::setX);
                case LDX_ABSOLUTE_Y -> ldAbsolutePlusAddrReg(cycles,this::setX,this::getY);

                //LDY
                case LDY_IM -> ldIm(cycles,this::setY);
                case LDY_ZP -> ldZp(cycles, this::setY,null);
                case LDY_ZP_X -> ldZp(cycles, this::setY, this::getX);
                case LDY_ABSOLUTE -> ldAbsolute(cycles,this::setY);
                case LDY_ABSOLUTE_X -> ldAbsolutePlusAddrReg(cycles,this::setY,this::getX);


                case STA_ZP -> stZp(cycles,this::getA,null);
                case STA_ZP_X -> stZp(cycles,this::getA,this::getX);
                case STA_ABSOLUTE -> stAbsolute(cycles,this::getA,null);
                case STA_ABSOLUTE_X -> stAbsolute(cycles,this::getA,this::getX);
                case STA_ABSOLUTE_Y -> stAbsolute(cycles,this::getA,this::getY);


                case STA_INDIRECT_X -> {
                    final var instrAddr = fetchByte(cycles)&0xff;
                    final var baseAddr = (instrAddr+X)&0xff;     cycles.decrementAndGet();
                    final var addr = readWord(cycles,baseAddr);
                    writeByte(cycles,addr, (byte) A);
                }
                case STA_INDIRECT_Y -> {
                    final var instrAddr = fetchByte(cycles)&0xff;
                    final var addr = readWord(cycles,instrAddr);
                    final var finalAddr = addr+Y; cycles.decrementAndGet();
                    writeByte(cycles,finalAddr, (byte) A);
                }

                case STX_ZP -> stZp(cycles,this::getX,null);
                case STX_ZP_Y -> stZp(cycles,this::getX,this::getY);
                case STX_ABSOLUTE -> stAbsolute(cycles,this::getX,null);

                case STY_ZP -> stZp(cycles,this::getY,null);
                case STY_ZP_X -> stZp(cycles,this::getY,this::getX);
                case STY_ABSOLUTE -> stAbsolute(cycles,this::getY,null);

                case TXA ->transferRegister(cycles,this::setA, this::getX);
                case TYA ->transferRegister(cycles,this::setA, this::getY);
                case TAX -> transferRegister(cycles,this::setX, this::getA);
                case TAY -> transferRegister(cycles,this::setY, this::getA);

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
            log.info("{}",printRegs());
        }
    }
}
