package org.qmor;

import java.util.Arrays;

public class Memory {

    public static final int MEM_SIZE = 1024*64;
    byte[] data = new byte[MEM_SIZE];
    public void reset()
    {
        Arrays.fill(data, (byte)0);
    }
}

