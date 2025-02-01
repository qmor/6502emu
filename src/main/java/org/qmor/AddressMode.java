package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@RequiredArgsConstructor
public enum AddressMode {
    NONE((cpu,cycles)-> (short) 0),
    IMMEDIATE(CPU::fetchByte),
    ZERO_PAGE((cpu,cycles)->
    {
        var addr = cpu.fetchByte(cycles);
        return cpu.readByte(cycles,addr);
    }),
    ZERO_PAGE_X((cpu,cycles)->
    {
        var addr = cpu.fetchByte(cycles);
        addr+=cpu.getX();
        cycles.decrementAndGet();
        return cpu.readByte(cycles,addr);
    }),
    ZERO_PAGE_Y((cpu,cycles)->
    {
        var addr = cpu.fetchByte(cycles);
        addr+=cpu.getY();
        cycles.decrementAndGet();
        return cpu.readByte(cycles,addr);
    }),
    ABSOLUTE((cpu,cycles)->
    {
        var address = cpu.fetchWord(cycles);
        cycles.decrementAndGet();
        return cpu.getMemory().data[address];
    }),
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
    }),
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
    }),
    INDIRECT_X((cpu,cycles)->{
        final var instrAddr = cpu.fetchByte(cycles)&0xff;
        final var baseAddr = (instrAddr+cpu.getX())&0xff;     cycles.decrementAndGet();
        final var addr = cpu.readWord(cycles,baseAddr);
        return cpu.readByte(cycles,addr);
    }),
    INDIRECT_Y((cpu,cycles)->{
        final var instrAddr = cpu.fetchByte(cycles)&0xff;
        final var addr = cpu.readWord(cycles,instrAddr);
        final var finalAddr = addr+cpu.getY();
        if (!cpu.addressInSamePage(addr,finalAddr))
            cycles.decrementAndGet();
        return cpu.readByte(cycles,finalAddr);
    }),
    INDIRECT((cpu,cycles)->{
        var instAddr = cpu.fetchWord(cycles);
        return (short) cpu.readWord(cycles,instAddr);
    });

    public interface AddressModeFunc
    {
       short getValue(CPU cpu, AtomicInteger cycles);
    }

    @Getter
    private final AddressModeFunc addressModeImpl;

}
