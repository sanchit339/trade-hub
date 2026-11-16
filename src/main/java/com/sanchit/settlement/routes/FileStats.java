package com.sanchit.settlement.routes;

import java.util.concurrent.atomic.AtomicInteger;

public class FileStats {
    private final AtomicInteger accepted = new AtomicInteger();
    private final AtomicInteger rejected = new AtomicInteger();

    public void accepted(){ accepted.incrementAndGet(); }
    public void rejected(){ rejected.incrementAndGet(); }

    public int getAccepted() { return accepted.get(); }
    public int getRejected() { return rejected.get(); }

}
