package ch.npl.cash.client.viewmodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.table.AbstractTableModel;

import ch.npl.cash.domain.Ordering;
import ch.npl.cash.domain.Transaction;

public class OrderingTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private EntityManager em;
	private List<Ordering> orderings;
	private String[] columnNames = { "Id", "Transaktions ID", "Anz. Artikel", "Datum" };
	private Class<?>[] columnClasses = {Integer.class, Integer.class, Integer.class, String.class};
	
	public OrderingTableModel(EntityManager entityManager) {
		this.em = entityManager;
		this.reload();
	}

	public void reload() {
		TypedQuery<Ordering> q = em.createQuery("select u from Ordering u", Ordering.class);
		orderings = q.getResultList();
		fireTableDataChanged();
	}
	
	public void addOrdering(Ordering b) {
		em.getTransaction().begin();
		em.persist(b);
		em.getTransaction().commit();
		orderings.add(b);
		fireTableDataChanged();
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
		return orderings.size();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
    }
	
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		Ordering b = orderings.get(rowIndex);
		switch (colIndex) {
		case 0:
			return b.getId();
		case 1:
			Transaction t = b.getTransaction();
			if (t != null) {
				return b.getTransaction().getId();
			} else {
				return 0;
			}
		case 2:
			return b.getPositions().size();
		case 3:
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return dateFormat.format(b.getDate());
		}
		return null;
	}
}
