package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Flag {
    C(0),
    Z(1),
    I(2),
    D(3),
    B(4),
    U(5),
    V(6),
    N(7);

    private final int offset;
}