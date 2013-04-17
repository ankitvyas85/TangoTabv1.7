package com.tangotab.core.ex;
/**
 * Exception class will be used through out the application for handling Exceptions.
 * 
 * @author dillip.lenka
 *
 */
public class TangoTabException extends Exception
{

	private static final long serialVersionUID = 6206774749275338388L;
	protected String className;
	protected int errorCode;
	protected String methodName;
	protected Exception exceptionRef;
	/**
	 * Overloaded constructor
	 * 
	 * @param inErrorCode
	 * @param inClassName
	 * @param inMethodName
	 * @param inExceptionRef
	 */
	public TangoTabException(int inErrorCode, String inClassName,	String inMethodName, Exception inExceptionRef) 
	{
		super(inClassName + " : " + inMethodName, inExceptionRef);
		this.errorCode = inErrorCode;
		this.className = inClassName;
		this.methodName = inMethodName;
		this.exceptionRef = inExceptionRef;
	}
	/**
	 * Overloaded constructor
	 * 
	 * @param inClassName
	 * @param inMethodName
	 * @param inExceptionRef
	 */
	public TangoTabException(String inClassName, String inMethodName, Exception inExceptionRef) 
	{
		super(inClassName + " : " + inMethodName, inExceptionRef);
		this.className = inClassName;
		this.methodName = inMethodName;
		this.exceptionRef = inExceptionRef;
	}
	/**
	 * Overloaded constructor
	 * 
	 * @param inClassName
	 * @param inMethodName
	 * @param errorMessage
	 */
	public TangoTabException(String inClassName, String inMethodName, String errorMessage) 
	{
		super(errorMessage);
		this.className = inClassName;
		this.methodName = inMethodName;
	}
}
