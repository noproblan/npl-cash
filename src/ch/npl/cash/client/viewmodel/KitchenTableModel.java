package ch.npl.cash.client.viewmodel;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.table.AbstractTableModel;

import ch.npl.cash.domain.Article;

public class KitchenTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private EntityManager em;
	private List<Article> articles;
	private String[] columnNames = { "Id", "Name", "Preis", "Verfügbar" };
	private boolean[] columnEditable = { false, true, true, true };
	private Class<?>[] columnClasses = {Integer.class, String.class, Double.class, Boolean.class};
	
	public KitchenTableModel(EntityManager entityManager) {
		this.em = entityManager;
		this.reload();
	}

	public void reload() {
		TypedQuery<Article> q = em.createQuery("select u from Article u", Article.class);
		articles = q.getResultList();
		for (Article a : articles) {
			em.refresh(a);
		}
		fireTableDataChanged();
	}
	
	public void addArticle(Article a) {
		em.getTransaction().begin();
		em.persist(a);
		em.getTransaction().commit();
		articles.add(a);
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
		return articles.size();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
    }
	
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		Article a = articles.get(rowIndex);
		switch (colIndex) {
		case 0:
			return a.getId();
		case 1:
			return a.getName();
		case 2:
			return a.getPrice();
		case 3:
			return a.isAvailable();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return columnEditable[col];
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		Article a = articles.get(row);
		em.getTransaction().begin();
		switch (col) {
		case 1: a.setName((String) value); break;
		case 2: a.setPrice((double) value); break;
		case 3: a.setAvailable((boolean) value); break;
		}
		em.persist(a);
		em.getTransaction().commit();
		fireTableCellUpdated(row, col);
	}

	/**
	 * Search article by id
	 * @param id
	 * @return
	 */
	public Article getArticle(int id) {
		for (Article a : articles) {
			if (a.getId() == id) {
				return a;
			}
		}
		return null;
	}
	
	// TODO immutable
	public List<Article> getArticles() {
		return this.articles;
	}
}
