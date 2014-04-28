package ch.npl.cash.client.viewmodel;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.table.AbstractTableModel;

import ch.npl.cash.domain.UserMoney;

public class UserTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private EntityManager em;
	private List<UserMoney> users;
	private String[] columnNames = { "Id", "RFID", "Name", "Geld", "Anzahl Transaktionen" };
	private boolean[] columnEditable = { false, true, false, true, false };
	private Class<?>[] columnClasses = {Integer.class, String.class, String.class, Double.class, Integer.class};

	public UserTableModel(EntityManager entityManager) {
		this.em = entityManager;
		this.reload();
	}
	
	public void reload() {
		TypedQuery<UserMoney> q = em.createQuery("select u from UserMoney u", UserMoney.class);
		users = q.getResultList();
		for (UserMoney u : users) {
			em.refresh(u);
		}
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
		return users.size();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
    }
	
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		UserMoney m = users.get(rowIndex);
		switch (colIndex) {
		case 0:
			return m.getId();
		case 1:
			return m.getRfid();
		case 2:
			return m.getUsername();
		case 3:
			return m.getBalance();
		case 4:
			return m.getTransactions().size();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return columnEditable[col];
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		UserMoney m = users.get(row);
		if (col == 1) {
			m.setRfid((String) value);
		}
		if (col == 3) {
			m.setBalance((double) value);
		}
		saveUserMoney(m);
		fireTableCellUpdated(row, col);
	}

	public UserMoney getUserMoney(int id) {
		for(UserMoney m : users) {
			if (m.getId() == id) {
				return m;
			}
		}
		return null;
	}
	
	public UserMoney getUserMoneyByRfid(String rfid) {
		for(UserMoney m : users) {
			if (m.getRfid() != null && m.getRfid().equals(rfid)) {
				return m;
			}
		}
		return null;
	}
	
	public void saveUserMoney(UserMoney m) {
		em.getTransaction().begin();
		em.persist(m);
		em.getTransaction().commit();
	}
}
