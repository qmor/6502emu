package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@RequiredArgsConstructor
public enum AddressMode {
    NONE(null,null),
    /*
    Immediate addressing allows the programmer to directly specify an 8 bit constant within the instruction.
    It is indicated by a '#' symbol followed by an numeric expression.
     */
    IMMEDIATE(CPU::fetchByte,null),
    /*
    An instruction using zero page addressing mode has only an 8 bit address operand. This limits it to addressing only
    the first 256 bytes of memory (e.g. $0000 to $00FF) where the most significant byte of the address is always zero.
    In zero page mode only the least significant byte of the address is held in the instruction making it shorter by
    one byte (important for space saving) and one less memory fetch during execution (important for speed).

    An assembler will automatically select zero page addressing mode if the operand evaluates to a zero page address and the
    instruction supports the mode (not all do).
     */
    ZERO_PAGE((cpu,cycles)->
    {
        var addr = cpu.fetchByte(cycles);
        return cpu.readByte(cycles,addr);
    }, (cpu,cycles,value)->{
        var addr = cpu.fetchByte(cycles)&0xff;
        cpu.writeByte(cycles,addr, (byte) value);
    }),
    /*
    The address to be accessed by an instruction using indexed zero page addressing is calculated by taking the 8 bit
    zero page address from the instruction and adding the current value of the X register to it. For example if the X
    register contains $0F and the instruction LDA $80,X is executed then the accumulator will be loaded from $008F
    (e.g. $80 + $0F => $8F).

    NB:a
    The address calculation wraps around if the sum of the base address and the register exceed $FF. If we repeat the last
    example but with $FF in the X register then the accumulator will be loaded from $007F (e.g. $80 + $FF => $7F) and not $017F.
     */
    ZERO_PAGE_X((cpu,cycles)->
    {
        var addr = cpu.fetchByte(cycles);
        addr+=cpu.getX();
        cycles.decrementAndGet();
        return cpu.readByte(cycles,addr);
    },(cpu,cycles,value)->{
        var addr = cpu.fetchByte(cycles)&0xff;
        addr+=cpu.getX();
        cycles.decrementAndGet();
        cpu.writeByte(cycles,addr, (byte) value);
    }),
    /*
    The address to be accessed by an instruction using indexed zero page addressing is calculated by taking the 8 bit
    zero page address from the instruction and adding the current value of the Y register to it. This mode can only be
    used with the LDX and STX instructions.
     */
    ZERO_PAGE_Y((cpu,cycles)->
    {
        var addr = cpu.fetchByte(cycles);
        addr+=cpu.getY();
        cycles.decrementAndGet();
        return cpu.readByte(cycles,addr);
    },(cpu,cycles,value)->{
        var addr = cpu.fetchByte(cycles)&0xff;
        addr+=cpu.getY();
        cycles.decrementAndGet();
        cpu.writeByte(cycles,addr, (byte) value);
    }),
    /*
    Instructions using absolute addressing contain a full 16 bit address to identify the target location.
     */
    ABSOLUTE((cpu,cycles)->
    {
        var address = cpu.fetchWord(cycles);
        cycles.decrementAndGet();
        return cpu.getMemory().data[address];
    },((cpu, cycles, value) -> {
        var addr = cpu.fetchWord(cycles)&0xffff;
        cpu.writeByte(cycles,addr, (byte) value);
    })),
    /*
    The address to be accessed by an instruction using X register indexed absolute addressing is computed by taking the
     16 bit address from the instruction and added the contents of the X register. For example if X contains $92 then
     an STA $2000,X instruction will store the accumulator at $2092 (e.g. $2000 + $92).
     */
    ABSOLUTE_X((cpu,cycles)->
    {
        final var address = cpu.fetchWord(cycles);
        final int addressWithAdd = address+cpu.getX();
        if (!cpu.addressInSamePage(address,addressWithAdd))
        {
            cycles.decrementAndGet();
        }
        cycles.decrementAndGet();
        return cpu.getMemory().data[addressWithAdd];
    },(cpu,cycles, value)->{
        cycles.decrementAndGet();
        cpu.writeByte(cycles,(cpu.fetchWord(cycles)&0xffff)+cpu.getX(), (byte) value);
    }),
    /*
    The Y register indexed absolute addressing mode is the same as the previous mode only with the contents of the
    Y register added to the 16 bit address from the instruction.
     */
    ABSOLUTE_Y((cpu,cycles)->
    {
        final var address = cpu.fetchWord(cycles);
        final int addressWithAdd = address+cpu.getY();
        if (!cpu.addressInSamePage(address,addressWithAdd))
        {
            cycles.decrementAndGet();
        }
        cycles.decrementAndGet();
        return cpu.getMemory().data[addressWithAdd];
    },(cpu,cycles, value)->{
        cycles.decrementAndGet();
        cpu.writeByte(cycles,(cpu.fetchWord(cycles)&0xffff)+cpu.getY(), (byte) value);
    }),
    /*
    Indexed indirect addressing is normally used in conjunction with a table of address held on zero page.
     The address of the table is taken from the instruction and the X register added to it (with zero page wrap around)
      to give the location of the least significant byte of the target address.
     */
    INDIRECT_X((cpu,cycles)->{
        final var instrAddr = cpu.fetchByte(cycles)&0xff;
        final var baseAddr = (instrAddr+cpu.getX())&0xff;     cycles.decrementAndGet();
        final var addr = cpu.readWord(cycles,baseAddr);
        return cpu.readByte(cycles,addr);
    },(cpu,cycles, value)->{
        final var instrAddr = cpu.fetchByte(cycles)&0xff;
        final var baseAddr = (instrAddr+cpu.getX())&0xff;     cycles.decrementAndGet();
        final var addr = cpu.readWord(cycles,baseAddr);
        cpu.writeByte(cycles,addr, (byte) value);
    }),
    /*
    Indirect indirect addressing is the most common indirection mode used on the 6502. In instruction contains the
    zero page location of the least significant byte of 16 bit address. The Y register is dynamically added to this
    value to generated the actual target address for operation.
     */
    INDIRECT_Y((cpu,cycles)->{
        final var instrAddr = cpu.fetchByte(cycles)&0xff;
        final var addr = cpu.readWord(cycles,instrAddr);
        final var finalAddr = addr+cpu.getY();
        if (!cpu.addressInSamePage(addr,finalAddr))
            cycles.decrementAndGet();
        return cpu.readByte(cycles,finalAddr);
    },(cpu,cycles, value)->{
        final var instrAddr = cpu.fetchByte(cycles)&0xff;
        final var addr = cpu.readWord(cycles,instrAddr);
        final var finalAddr = addr+cpu.getY(); cycles.decrementAndGet();
        cpu.writeByte(cycles,finalAddr, (byte) value);
    }),
    /*
    JMP is the only 6502 instruction to support indirection. The instruction contains a 16 bit address which identifies
     the location of the least significant byte of another 16 bit memory address which is the real target of the instruction.

For example if location $0120 contains $FC and location $0121 contains $BA then the instruction JMP ($0120) will cause
the next instruction execution to occur at $BAFC (e.g. the contents of $0120 and $0121).
     */
    INDIRECT((cpu,cycles)->{
        var instAddr = cpu.fetchWord(cycles);
        return (short) cpu.readWord(cycles,instAddr);
    },null);


    public interface AddressModeFuncRead
    {
        short readValue(CPU cpu, AtomicInteger cycles);
    }

    public interface AddressModeFuncWrite
    {
        void writeValue(CPU cpu, AtomicInteger cycles, int value);
    }

    private final AddressModeFuncRead readImpl;
    private final AddressModeFuncWrite writeImpl;

}
