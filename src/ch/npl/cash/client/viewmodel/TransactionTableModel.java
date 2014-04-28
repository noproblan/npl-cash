package ch.npl.cash.client.viewmodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.table.AbstractTableModel;

import ch.npl.cash.domain.Ordering;
import ch.npl.cash.domain.Transaction;

public class TransactionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private EntityManager em;
	private List<Transaction> transactions;
	private String[] columnNames = { "Id", "Eigentümer ID", "Eigent�mer", "Betrag", "Bestellung", "Datum" };
	private Class<?>[] columnClasses = {Integer.class, Integer.class, String.class, Double.class, Integer.class, String.class};
	
	public TransactionTableModel(EntityManager entityManager) {
		this.em = entityManager;
		this.reload();
	}

	public void reload() {
		TypedQuery<Transaction> q = em.createQuery("select t from Transaction t", Transaction.class);
		transactions = q.getResultList();
		fireTableDataChanged();
	}
	
	public void addTransaction(Transaction t) {
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
	}
	
	@Override
	public String getColumnName(int col) {
        return columnNames[col].toString();
    }
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return transactions.size();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
    }
	
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		Transaction t = transactions.get(rowIndex);
		switch (colIndex) {
		case 0:
			return t.getId();
		case 1:
			return t.getUserMoney().getId();
		case 2:
			return t.getUserMoney().getUsername();
		case 3:
			return t.getAmount();
		case 4:
			Ordering b = t.getOrdering();
			if (b != null) { 
				return b.getId();
			} else {
				return 0;
			}
		case 5:
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return dateFormat.format(t.getDate());
		}
		return null;
	}
}
