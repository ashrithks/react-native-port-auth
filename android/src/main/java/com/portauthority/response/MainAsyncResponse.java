package com.portauthority.response;

import com.portauthority.network.Host;

import java.util.concurrent.atomic.AtomicInteger;

public interface MainAsyncResponse extends ErrorAsyncResponse {

    /**
     * Delegate to handle Host + AtomicInteger outputs
     *
     * @param h
     * @param i
     */
   default void processFinish(Host h, AtomicInteger i){};

    /**
     * Delegate to handle integer outputs
     *
     * @param output
     */
   default void processFinish(int output){};

    /**
     * Delegate to handle string outputs
     *
     * @param output
     */
    default void processFinish(String output){};

    /**
     * Delegate to handle boolean outputs
     *
     * @param output
     */
   default void processFinish(boolean output){};

    void setupMac();

}
