package ch.npl.cash.client.viewmodel;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.table.AbstractTableModel;

import ch.npl.cash.domain.Article;

public class AvailableArticlesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private EntityManager em;
	private List<Article> articles;
	private String[] columnNames = { "Id", "Name", "Preis"};
	private Class<?>[] columnClasses = {Integer.class, String.class, Double.class};
	
	public AvailableArticlesTableModel(EntityManager entityManager) {
		this.em = entityManager;
		this.reload();
	}

	public void reload() {
		TypedQuery<Article> q = em.createQuery("select a from Article a WHERE a.available = 1", Article.class);
		articles = q.getResultList();
		for (Article a : articles) {
			em.refresh(a);
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
		}
		return null;
	}
}
