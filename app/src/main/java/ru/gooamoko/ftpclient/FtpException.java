package ru.gooamoko.ftpclient;

public class FtpException extends RuntimeException {

    public FtpException(String message) {
        super(message);
    }

    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }
}
