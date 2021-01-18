package com.epb.epbdevice.utl;

public class QRCodeException extends Exception{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4706281335531351575L;

	public QRCodeException() {
        super();
    }

    public QRCodeException(String message) {
        super(message);
    }

    public QRCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}