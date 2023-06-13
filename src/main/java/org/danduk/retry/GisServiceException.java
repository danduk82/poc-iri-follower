package org.danduk.retry;

public class GisServiceException extends Exception {

    public GisServiceException(final Throwable t) {
        super(t);
    }

    public GisServiceException(final String s) {
        super(s);
    }

    public GisServiceException(final String s, final Throwable t) {
        super(s, t);
    }
}
