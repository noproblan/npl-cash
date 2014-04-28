package ch.npl.cash.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Ordering {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date created;
	
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "ordering", optional = false)
	private Transaction transaction;
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, mappedBy = "ordering")
	@JoinColumn(name = "orderingRef")
	private List<OrderingPosition> positions = new ArrayList<OrderingPosition>();
	
	public int getId() {
		return id;
	}

	public void setTransaction(Transaction t) {
		transaction = t;
		transaction.setOrdering(this);
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public Date getDate() {
		return created;
	}

	public List<OrderingPosition> getPositions() {
		return positions;
	}

	@PrePersist
    protected void onCreate() {
		this.created = new Date();
    }
	
	public double getTotal() {
		double sum = 0;
		for(OrderingPosition op : positions) {
			sum += op.getArticlePrice();
		}
		return sum;
	}

	public void addPosition(OrderingPosition p) {
		positions.add(p);
	}
}
