package org.qmor;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import static org.qmor.OpFunctions.NO_AFFECTS;
import static org.qmor.OpFunctions.ZFIFZERO_NFIFNEG;
import static org.qmor.OpFunctions.ZFIFZERO_NFIFNEG_CIFCARRY;
import static org.qmor.OpGroups.REG_A_LOAD_CODES;
import static org.qmor.OpGroups.REG_X_LOAD_CODES;
import static org.qmor.OpGroups.REG_Y_LOAD_CODES;


@Getter

public enum OpCodes {
    JSR(0x20,NO_AFFECTS,6,AddressMode.ABSOLUTE),
    RTS(0x60,NO_AFFECTS,6,AddressMode.NONE),

    JMP_ABSOLUTE(0x4C,NO_AFFECTS,3,AddressMode.ABSOLUTE),
    JMP_INDIRECT(0x6C,NO_AFFECTS,5,AddressMode.INDIRECT),

    LDA_IM(0xa9, ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES, AddressMode.IMMEDIATE),
    LDA_ZP(0xa5, ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES, AddressMode.ZERO_PAGE),
    LDA_ZP_X(0xB5, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES, AddressMode.ZERO_PAGE_X),
    LDA_ABSOLUTE(0xAD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES, AddressMode.ABSOLUTE),
    LDA_ABSOLUTE_X(0xBD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_X),
    LDA_ABSOLUTE_Y(0xB9, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_Y),
    LDA_INDIRECT_X(0xA1, ZFIFZERO_NFIFNEG,6,REG_A_LOAD_CODES,AddressMode.INDIRECT_X),
    LDA_INDIRECT_Y(0xB1, ZFIFZERO_NFIFNEG,5,REG_A_LOAD_CODES,AddressMode.INDIRECT_Y),

    LDX_IM(0xA2,ZFIFZERO_NFIFNEG,2,REG_X_LOAD_CODES, AddressMode.IMMEDIATE),
    LDX_ZP(0xA6,ZFIFZERO_NFIFNEG,3,REG_X_LOAD_CODES, AddressMode.ZERO_PAGE),
    LDX_ZP_Y(0xB6,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES,AddressMode.ZERO_PAGE_Y),
    LDX_ABSOLUTE(0xAE,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES,AddressMode.ABSOLUTE),
    LDX_ABSOLUTE_Y(0xBE,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES,AddressMode.ABSOLUTE_Y),

    LDY_IM(0xA0,ZFIFZERO_NFIFNEG,2,REG_Y_LOAD_CODES, AddressMode.IMMEDIATE),
    LDY_ZP(0xA4,ZFIFZERO_NFIFNEG,3,REG_Y_LOAD_CODES, AddressMode.ZERO_PAGE),
    LDY_ZP_X(0xB4,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES,AddressMode.ZERO_PAGE_X),
    LDY_ABSOLUTE(0xAC,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES,AddressMode.ABSOLUTE),
    LDY_ABSOLUTE_X(0xBC,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES,AddressMode.ABSOLUTE_X),

    TAX(0xAA,ZFIFZERO_NFIFNEG, 2,REG_X_LOAD_CODES,AddressMode.NONE),
    TXA(0x8A,ZFIFZERO_NFIFNEG, 2,REG_A_LOAD_CODES,AddressMode.NONE),
    TAY(0xA8,ZFIFZERO_NFIFNEG, 2,REG_Y_LOAD_CODES,AddressMode.NONE),
    TYA(0x98,ZFIFZERO_NFIFNEG, 2,REG_A_LOAD_CODES,AddressMode.NONE),


    STA_ZP(0x85,NO_AFFECTS,3,AddressMode.ZERO_PAGE),
    STA_ZP_X(0x95,NO_AFFECTS,4,AddressMode.ZERO_PAGE_X),
    STA_ABSOLUTE(0x8D,NO_AFFECTS,4,AddressMode.ABSOLUTE),
    STA_ABSOLUTE_X(0x9D,NO_AFFECTS,5,AddressMode.ABSOLUTE_X),
    STA_ABSOLUTE_Y(0x99,NO_AFFECTS,5,AddressMode.ABSOLUTE_Y),
    STA_INDIRECT_X(0x81,NO_AFFECTS,6,AddressMode.INDIRECT_X),
    STA_INDIRECT_Y(0x91,NO_AFFECTS,6,AddressMode.INDIRECT_Y),

    STX_ZP(0x86, NO_AFFECTS,3,AddressMode.ZERO_PAGE),
    STX_ZP_Y(0x96, NO_AFFECTS,4,AddressMode.ZERO_PAGE_Y),
    STX_ABSOLUTE(0x8e, NO_AFFECTS,4,AddressMode.ABSOLUTE),

    STY_ZP(0x84, NO_AFFECTS,3,AddressMode.ZERO_PAGE),
    STY_ZP_X(0x94, NO_AFFECTS,4,AddressMode.ZERO_PAGE_X),
    STY_ABSOLUTE(0x8c, NO_AFFECTS,4,AddressMode.ABSOLUTE),

    TSX(0xBA,ZFIFZERO_NFIFNEG,2, REG_X_LOAD_CODES,AddressMode.NONE),
    TXS(0x9A,NO_AFFECTS,2,AddressMode.NONE),
    PHA(0x48,NO_AFFECTS,3,AddressMode.NONE),
    PHP(0x08,NO_AFFECTS,3,AddressMode.NONE),
    PLA(0x68,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.NONE),
    PLP(0x28,NO_AFFECTS, 4,AddressMode.NONE),


    AND_IM(0x29,ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES,AddressMode.IMMEDIATE),
    AND_ZP(0x25,ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE),
    AND_ZP_X(0x35,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE_X),
    AND_ABSOLUTE(0x2d,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE),
    AND_ABSOLUTE_X(0x3d,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_X),
    AND_ABSOLUTE_Y(0x39,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_Y),
    AND_INDIRECT_X(0x21,ZFIFZERO_NFIFNEG,6,REG_A_LOAD_CODES,AddressMode.INDIRECT_X),
    AND_INDIRECT_Y(0x31,ZFIFZERO_NFIFNEG,5,REG_A_LOAD_CODES,AddressMode.INDIRECT_Y),

    OR_IM(0x09,ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES,AddressMode.IMMEDIATE),
    OR_ZP(0x05,ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE),
    OR_ZP_X(0x15,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE_X),
    OR_ABSOLUTE(0x0d,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE),
    OR_ABSOLUTE_X(0x1d,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_X),
    OR_ABSOLUTE_Y(0x19,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_Y),
    OR_INDIRECT_X(0x01,ZFIFZERO_NFIFNEG,6,REG_A_LOAD_CODES,AddressMode.INDIRECT_X),
    OR_INDIRECT_Y(0x11,ZFIFZERO_NFIFNEG,5,REG_A_LOAD_CODES,AddressMode.INDIRECT_Y),

    //XOR
    EOR_IM(0x49,ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES,AddressMode.IMMEDIATE),
    EOR_ZP(0x45,ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE),
    EOR_ZP_X(0x55,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE_X),
    EOR_ABSOLUTE(0x4d,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE),
    EOR_ABSOLUTE_X(0x5d,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_X),
    EOR_ABSOLUTE_Y(0x59,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_Y),
    EOR_INDIRECT_X(0x41,ZFIFZERO_NFIFNEG,6,REG_A_LOAD_CODES,AddressMode.INDIRECT_X),
    EOR_INDIRECT_Y(0x51,ZFIFZERO_NFIFNEG,5,REG_A_LOAD_CODES,AddressMode.INDIRECT_Y),

    DEX(0xCA,ZFIFZERO_NFIFNEG,2,REG_X_LOAD_CODES,AddressMode.NONE),
    DEY(0x88,ZFIFZERO_NFIFNEG,2,REG_Y_LOAD_CODES,AddressMode.NONE),
    INX(0xE8,ZFIFZERO_NFIFNEG,2,REG_X_LOAD_CODES,AddressMode.NONE),
    INY(0xC8,ZFIFZERO_NFIFNEG,2,REG_Y_LOAD_CODES,AddressMode.NONE),

    INC_ZP(0xE6,ZFIFZERO_NFIFNEG,5, AddressMode.ZERO_PAGE),
    INC_ZP_X(0xF6,ZFIFZERO_NFIFNEG,6, AddressMode.ZERO_PAGE_X),
    INC_ABSOLUTE(0xEE,ZFIFZERO_NFIFNEG,6, AddressMode.ABSOLUTE),
    INC_ABSOLUTE_X(0xFE,ZFIFZERO_NFIFNEG,7, AddressMode.ABSOLUTE_X),


    DEC_ZP(0xC6,ZFIFZERO_NFIFNEG,5, AddressMode.ZERO_PAGE),
    DEC_ZP_X(0xD6,ZFIFZERO_NFIFNEG,6, AddressMode.ZERO_PAGE_X),
    DEC_ABSOLUTE(0xCE,ZFIFZERO_NFIFNEG,6, AddressMode.ABSOLUTE),
    DEC_ABSOLUTE_X(0xDE,ZFIFZERO_NFIFNEG,7, AddressMode.ABSOLUTE_X),

    BIT_ZP(0x24,NO_AFFECTS,3,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE),
    BIT_ABSOLUTE(0x2C,NO_AFFECTS,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE),

    ADC_IM(0x69,ZFIFZERO_NFIFNEG_CIFCARRY,2, REG_A_LOAD_CODES,AddressMode.IMMEDIATE),
    SBC_IM(0xE9,ZFIFZERO_NFIFNEG_CIFCARRY,2, REG_A_LOAD_CODES,AddressMode.IMMEDIATE),
    CMP_IM(0xC9,NO_AFFECTS,2,AddressMode.IMMEDIATE),
    CPX_IM(0xE0,NO_AFFECTS,2,AddressMode.IMMEDIATE),
    CPY_IM(0xC0,NO_AFFECTS,2,AddressMode.IMMEDIATE),
    SEC(0x38, NO_AFFECTS, 2, AddressMode.NONE),
    CLC(0x18, NO_AFFECTS, 2, AddressMode.NONE),
    NOP(0xEA,NO_AFFECTS,2,AddressMode.NONE);

    private final int opcode;
    private final List<BiFunction<FlagRegister, Short, FlagRegister>> functions;
    private final int cycles;
    private final AddressMode addressMode;

    OpCodes(int opcode, List<BiFunction<FlagRegister, Short, FlagRegister>> functions, int cycles, AddressMode addressMode) {
        this.opcode = opcode;
        this.functions = functions;
        this.cycles = cycles;
        this.addressMode = addressMode;
    }

    OpCodes(int opcode, List<BiFunction<FlagRegister, Short, FlagRegister>> functions, int cycles, Set<OpCodes> group, AddressMode addressMode) {
        this(opcode, functions, cycles,addressMode);
        Optional.ofNullable(group).ifPresent(e->e.add(this));
    }


    public AtomicInteger getCyclesAi()
    {
        return new AtomicInteger(cycles);
    }


    public static OpCodes fromByte(int opcode) {
        for (var b: OpCodes.values()) {
            if (opcode == b.opcode) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown opcode %02x".formatted(opcode));
    }


}
