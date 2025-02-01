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
    public static final int STACK_LOW = 0x100;
    private static final int STACK_SIZE = 0xff;
    private static final int STACK_HIGH = STACK_LOW+STACK_SIZE;
    @Setter
    private int PC;
    /**
     * The 6502 microprocessor supports a 256 byte stack fixed between memory locations $0100 and $01FF.
     * A special 8-bit register, S, is used to keep track of the next free byte of stack space.
     * Pushing a byte on to the stack causes the value to be stored at the current free location (e.g. $0100,S)
     * and then the stack pointer is post decremented. Pull operations reverse this procedure
     */
    private int SP;

    public void setSP(int newValue)
    {
        SP = newValue&0xff;
    }
    public int getSP()
    {
        return SP&0xff;
    }

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
        return "PC:%04X SP:%04X A:%02X X:%02X Y:%02X F:[%s]".formatted(PC,getSP(),A,X,Y,F.printFlags());
    }
    public void reset()
    {
        PC = 0xFFFC;
        SP = STACK_SIZE;
        F.reset();
        A = X = Y = 0;
        memory.reset();
    }



    int fetchWord(AtomicInteger cycles)
    {
        return fetchByte(cycles) | (fetchByte(cycles)  << 8);
    }


    /**
     * read byte from memory at PC and increments PC
     * @param cycles - cycles holder
     * @return - fetched byte
     */
    short fetchByte(AtomicInteger cycles)
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
    short readByte(AtomicInteger cycles, int address)
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
    int readWord(AtomicInteger cycles, int address)
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
        writeWord(cycles,STACK_LOW+((SP-2)&0xff), word);
        setSP(SP-2);
    }

    /**
     * Write byte to stack at SP and decrement SP by 1
     * @param cycles - cycles holder
     * @param b - byte to place to stack
     */
    private void writeByteToStack(AtomicInteger cycles, byte b)
    {
        writeByte(cycles, STACK_LOW+((SP-1)&0xff),b);
        setSP(SP-1);
    }
    /**
     * read little endian word from stack at SP and increment SP by 2
     * @param cycles - cycles holder
     * @return word fetched from stack
     */
    private int readWordFromStack(AtomicInteger cycles)
    {
        var word = readWord(cycles,STACK_LOW+SP);
        setSP(SP+2);
        return word;
    }

    /**
     * read little endian word from stack at SP and increment SP by 2
     * @param cycles - cycles holder
     * @return word fetched from stack
     */
    private int readByteFromStack(AtomicInteger cycles)
    {
        var b = readByte(cycles,STACK_LOW+SP);
        setSP(SP+1);
        return b;
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
    boolean addressInSamePage(int a1, int a2)
    {
        return (a1 & 0x100) == (a2&0x100);
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

    enum AndOrXor
    {
        AND,
        OR,
        XOR
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
                case JMP_ABSOLUTE -> PC = fetchWord(cycles);
                case JMP_INDIRECT -> PC = op.getAddressMode().getAddressModeImpl().getValue(this,cycles)&0xffff;
                //LDA
                case LDA_IM, LDA_ZP, LDA_ZP_X, LDA_ABSOLUTE, LDA_ABSOLUTE_X, LDA_ABSOLUTE_Y,LDA_INDIRECT_X,LDA_INDIRECT_Y -> A =op.getAddressMode().getAddressModeImpl().getValue(this,cycles);

                //LDX
                case LDX_IM, LDX_ZP, LDX_ZP_Y, LDX_ABSOLUTE, LDX_ABSOLUTE_Y -> X = op.getAddressMode().getAddressModeImpl().getValue(this,cycles);

                //LDY
                case LDY_IM, LDY_ZP, LDY_ZP_X, LDY_ABSOLUTE, LDY_ABSOLUTE_X -> Y=op.getAddressMode().getAddressModeImpl().getValue(this,cycles);




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

                case TSX -> {X= (short) SP; cycles.decrementAndGet(); }
                case TXS -> {SP = X; cycles.decrementAndGet();}
                case PHA -> {writeByteToStack(cycles, (byte) A); cycles.decrementAndGet();}
                case PLA -> {A = (short) readByteFromStack(cycles);cycles.addAndGet(-2);}

                case PHP -> {writeByteToStack(cycles,(byte)F.getByteValue());cycles.decrementAndGet();}
                case PLP ->{F.setByteValue((short) (readByteFromStack(cycles)&0xff));cycles.addAndGet(-2);}

                case AND_IM,AND_ZP -> A = (short) (A & ((op.getAddressMode().getAddressModeImpl().getValue(this,cycles))&0xff));

                case OR_IM,OR_ZP -> A = (short) (A | ((op.getAddressMode().getAddressModeImpl().getValue(this,cycles))&0xff));

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
