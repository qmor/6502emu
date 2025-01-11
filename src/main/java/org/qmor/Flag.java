package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Flag {
    C(0),//CARRY
    Z(1),//ZERO
    I(2),//Interrupt Disable
    D(3),//Decimal Mode Flag
    B(4),//Break Command
    U(5),//UNUSED
    V(6),//Overflow Flag
    N(7);//Negative

    private final int offset;
}