package ch.npl.cash.client.viewmodel;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import ch.npl.cash.domain.OrderingPosition;

public class CurrentOrderingTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private List<OrderingPosition> positions = new ArrayList<OrderingPosition>();
	private String[] columnNames = { "Id", "Name", "Preis" };
	private Class<?>[] columnClasses = {Integer.class, String.class, Double.class};
	
	public void addPosition(OrderingPosition op) {
		positions.add(op);
		fireTableDataChanged();
	}

	public void removePosition(OrderingPosition op) {
		positions.remove(op);
		fireTableDataChanged();
	}
	
	public List<OrderingPosition> getPositions() {
		return positions;
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
		return positions.size();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
    }
	
	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		OrderingPosition op = positions.get(rowIndex);
		switch (colIndex) {
		case 0:
			return op.getArticleId();
		case 1:
			return op.getArticleName();
		case 2:
			return op.getArticlePrice();
		}
		return null;
	}

	public void executeOrder(int userMoneyId) {
		// TODO Auto-generated method stub
		
	}

	public double getTotal() {
		double sum = 0;
		for (OrderingPosition p : positions) {
			sum += p.getArticlePrice();
		}
		return sum;
	}
}
