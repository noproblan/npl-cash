package ch.npl.cash.domain;

public class TooLessMoneyException extends Exception {
	private static final long serialVersionUID = 1L;
	public TooLessMoneyException(String string) {
		super(string);
	}
}
