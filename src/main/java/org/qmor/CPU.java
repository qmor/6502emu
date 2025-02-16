package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.qmor.OpCodes.*;
import org.qmor.AddressMode.AddressModeFuncGetAddr.Direction;

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
    void writeByte(AtomicInteger cycles, int address, byte value)
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


    private final static List<OpCodes> OPERATION_OR = List.of(OR_IM,OR_ZP,OR_ZP_X,OR_ABSOLUTE,OR_ABSOLUTE_X,OR_ABSOLUTE_Y,OR_INDIRECT_X,OR_INDIRECT_Y);
    private final static List<OpCodes> OPERATION_XOR = List.of(EOR_IM,EOR_ZP,EOR_ZP_X,EOR_ABSOLUTE,EOR_ABSOLUTE_X,EOR_ABSOLUTE_Y,EOR_INDIRECT_X,EOR_INDIRECT_Y);
    private final static List<OpCodes> OPERATION_AND = List.of(AND_IM,AND_ZP,AND_ZP_X,AND_ABSOLUTE,AND_ABSOLUTE_X,AND_ABSOLUTE_Y,AND_INDIRECT_X,AND_INDIRECT_Y);
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
                case JMP_INDIRECT -> PC = readWord(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles, Direction.R));
                case LDA_IM, LDA_ZP, LDA_ZP_X, LDA_ABSOLUTE, LDA_ABSOLUTE_X, LDA_ABSOLUTE_Y,LDA_INDIRECT_X,LDA_INDIRECT_Y -> A = readByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.R));
                case LDX_IM, LDX_ZP, LDX_ZP_Y, LDX_ABSOLUTE, LDX_ABSOLUTE_Y -> X = readByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.R));
                case LDY_IM, LDY_ZP, LDY_ZP_X, LDY_ABSOLUTE, LDY_ABSOLUTE_X -> Y = readByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.R));
                case STA_ZP,STA_ZP_X,STA_ABSOLUTE,STA_ABSOLUTE_X,STA_ABSOLUTE_Y,STA_INDIRECT_X,STA_INDIRECT_Y ->writeByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.W), (byte) A);
                case STX_ZP,STX_ZP_Y,STX_ABSOLUTE -> writeByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.W), (byte) X);
                case STY_ZP,STY_ZP_X,STY_ABSOLUTE -> writeByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.W), (byte) Y);

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

                case AND_IM,AND_ZP,AND_ZP_X,AND_ABSOLUTE,AND_ABSOLUTE_X,AND_ABSOLUTE_Y,AND_INDIRECT_X,AND_INDIRECT_Y,
                 OR_IM,OR_ZP,OR_ZP_X,OR_ABSOLUTE,OR_ABSOLUTE_X,OR_ABSOLUTE_Y,OR_INDIRECT_X,OR_INDIRECT_Y,
                 EOR_IM,EOR_ZP,EOR_ZP_X,EOR_ABSOLUTE,EOR_ABSOLUTE_X,EOR_ABSOLUTE_Y,EOR_INDIRECT_X,EOR_INDIRECT_Y -> {
                    BinaryOperator<Integer> f = (a, b)->a&b;
                    var val =readByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.R));
                    if (OPERATION_OR.contains(op)) f  = (a,b)->a|b;
                    else if (OPERATION_XOR.contains(op)) f = (a,b)->a^b;
                    A = f.apply((int)A,(int)val).shortValue();
                }

                case DEX, DEY,INX, INY -> {
                    cycles.decrementAndGet();
                    final boolean isY = List.of(DEY, INY ).contains(op);
                    final int sign = List.of(DEY,DEX).contains(op)?-1:1;
                    final Supplier<Short> getter = isY?this::getY:this::getX;
                    final Consumer<Short> setter = isY?this::setY:this::setX;
                    setter.accept((short) ((short) (getter.get() + sign)&0xff));
                }

                case INC_ZP,INC_ZP_X, INC_ABSOLUTE,INC_ABSOLUTE_X, DEC_ZP,DEC_ZP_X,DEC_ABSOLUTE,DEC_ABSOLUTE_X ->
                {
                    var sign = List.of(INC_ZP,INC_ZP_X,INC_ABSOLUTE,INC_ABSOLUTE_X).contains(op)?1:-1;
                    var addr = op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.W);
                    var val = (short)((readByte(cycles,addr) +sign)&0xff);
                    OpFunctions.ZeroFlagIfZeroValue.apply(this.F,val);
                    OpFunctions.NegFlagIf7BitRaised.apply(this.F,val);
                    writeByte(cycles,addr, (byte) val);
                    cycles.decrementAndGet();
                }



                case BIT_ABSOLUTE,BIT_ZP -> {
                    var v = readByte(cycles,op.getAddressMode().getAddressModeImpl().getAddr(this,cycles,Direction.R));
                    this.getF().setFlag(Flag.Z, (A & v) == 0);
                    this.getF().setFlag(Flag.V, ((v>>6)&1)==1);
                    this.getF().setFlag(Flag.N, ((v>>7)&1)==1);
                }

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
