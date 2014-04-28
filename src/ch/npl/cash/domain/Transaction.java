package ch.npl.cash.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "userMoneyRef")
	private UserMoney userMoney;
	
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, optional = true)
	@JoinColumn(name = "orderingRef")
	private Ordering ordering;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date created;
	private double amount;

	public int getId() {
		return id;
	}
	public void setUserMoney(UserMoney m) {
		userMoney = m;
	}
	public UserMoney getUserMoney()
	{
		return userMoney;
	}
	public void setOrdering(Ordering b) {
		ordering = b;
	}
	public Ordering getOrdering() {
		return ordering;
	}
	
	public Date getDate() {
		return created;
	}
	public void setAmount(double a) {
		amount = a;
	}
	public double getAmount() {
		return amount;
	}
	
	@PrePersist
    protected void onCreate() {
		this.created = new Date();
    }
}
