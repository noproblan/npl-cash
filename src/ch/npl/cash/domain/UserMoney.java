package ch.npl.cash.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class UserMoney {
	@Id
	private int id; // von NPL gesynct
	
	@Column(nullable = false)
	private String username; // von NPL gesynct
	
	private String rfid; // von Hand eingelesen
	
	@Column(nullable = false)
	private double balance;
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "userMoney")
	private List<Transaction> transactions;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	public double getBalance() {
		return balance;
	}

	/**
	 * Money Transaction
	 * @param balance
	 */
	public void setBalance(double balance) {
		if (balance != this.balance) {
			// execute money transaction
			Transaction t = new Transaction();
			t.setUserMoney(this);
			t.setOrdering(null);
			t.setAmount(balance - this.balance);
			this.transactions.add(t);
			// then set new balance
			this.balance = balance;
		}
	}
	
	public void executeOrdering(Ordering b) throws TooLessMoneyException {
		double total = b.getTotal();
		if (this.balance < total) {
			throw new TooLessMoneyException("too less money!");
		}
		Transaction t = new Transaction();
		t.setUserMoney(this);
		t.setOrdering(b);
		t.setAmount(-b.getTotal());
		
		b.setTransaction(t);
		
		this.transactions.add(t);
		this.balance -= total;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
}
