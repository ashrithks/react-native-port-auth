package com.portauthority.network;

import com.portauthority.async.DnsLookupAsyncTask;
import com.portauthority.response.DnsAsyncResponse;

import java.lang.reflect.Type;


public final class Dns {

    /**
     * Kicks off a DNS lookup
     *
     * @param domain     Domain name
     * @param recordType The type of DNS record to look up
     * @param delegate   Delegate to be called when the lookup has finished
     */
    public static void lookup(String domain, String recordType, DnsAsyncResponse delegate) throws NoSuchFieldException, IllegalAccessException {
        String type = Integer.toString(Type.class.getField(recordType).getInt(null));
        new DnsLookupAsyncTask(delegate).execute(domain, type);
    }

}
