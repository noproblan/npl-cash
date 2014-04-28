package ch.npl.cash.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;

import ch.npl.cash.client.users.*;
import ch.npl.cash.client.viewmodel.*;
import ch.npl.cash.domain.*; // TODO: Das Fenster sollte eigentlich nicht direkt hiervon abhängig sein

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static BarMode barMode = BarMode.ADD;
	private static UserDownSynchronizer userDownSynchronizer;
	private static UserProvider userProvider = new CsvUserProvider();
	
	/**
	 * JPA
	 */
	private static final String PERSISTENCE_UNIT_NAME = "cash";
	private static EntityManagerFactory factory;
	private UserTableModel userTableModel;
	private TransactionTableModel transactionTableModel;
	private OrderingTableModel orderingTableModel;
	private KitchenTableModel kitchenTableModel;
	
	/**
	 * ANDERE MODELS
	 */
	private CurrentOrderingTableModel currentOrderingTableModel;
	private AvailableArticlesTableModel availableArticlesTableModel;
	
	/**
	 * GUI Elements
	 */
	private JPanel contentPane, panelBillBar;
	private JTextField userSearchTextField;
	private JTable userTable;
	private JTextField transactionSearchTextField;
	private JTable transactionTable;
	private JTextField kitchenSearchTextField;
	private JTable kitchenTable;
	private JTextField orderingSearchTextField;
	private JTable orderingTable;
	private JCheckBox checkboxInclOrderings;
	private JTextField commandTextField;
	private JTable currentOrderingTable;
	private JLabel labelBarMessage, labelBillName, labelBillPaid, labelBillSaldo, labelBillTotal;
	private JTable availableArticlesTable;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow(userProvider);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow(UserProvider userProvider) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 575);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (userTable != null) {
					((UserTableModel)userTable.getModel()).reload();
				}
				if (transactionTable != null) {
					((TransactionTableModel)transactionTable.getModel()).reload();
				}
				if (orderingTable != null) {
					((OrderingTableModel)orderingTable.getModel()).reload();
				}
				if (kitchenTable != null) {
					((KitchenTableModel)kitchenTable.getModel()).reload();
				}
				if (availableArticlesTable != null) {
					((AvailableArticlesTableModel)availableArticlesTable.getModel()).reload();
				}
				if (tabbedPane.getSelectedIndex() == 4) {
					commandTextField.requestFocus();
				}
			}
		});
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel panelUser = new JPanel();
		tabbedPane.addTab("Users", null, panelUser, null);
		panelUser.setLayout(new BorderLayout(0, 0));

		JPanel userOptions = new JPanel();
		FlowLayout flowLayout = (FlowLayout) userOptions.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelUser.add(userOptions, BorderLayout.NORTH);

		userSearchTextField = new JTextField();
		userSearchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filterUserTable();
			}
		});
		userOptions.add(userSearchTextField);
		userSearchTextField.setColumns(15);
		
		JButton btnDownsync = new JButton("downsync");
		btnDownsync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				userDownSynchronizer.syncronize();
				userTableModel.reload();
			}
		});
		userOptions.add(btnDownsync);
				
		userTable = new JTable() {
			private static final long serialVersionUID = 1L;
			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int colIndex) {
		          Component c = super.prepareRenderer(renderer, rowIndex, colIndex);
		          if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, colIndex)) {
		            c.setBackground(new Color(248,243,244));
		          } else {
		        	if (isRowSelected(rowIndex)) {
		        		c.setBackground(new Color(176,196,222));
		        	} else {
		        		c.setBackground(getBackground());
		        	}
		          }
		          return c;
		     }
		};
		userTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				userTableModel.reload();
			}
		});
		JScrollPane scrollPaneUser = new JScrollPane();
		panelUser.add(scrollPaneUser, BorderLayout.CENTER);
		scrollPaneUser.setViewportView(userTable);

		JPanel panelTransaction = new JPanel();
		tabbedPane.addTab("Transactions", null, panelTransaction, null);
		panelTransaction.setLayout(new BorderLayout(0, 0));

		JPanel transactionOptions = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) transactionOptions.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panelTransaction.add(transactionOptions, BorderLayout.NORTH);

		transactionSearchTextField = new JTextField();
		transactionSearchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filterTransactionTable();
			}
		});
		transactionSearchTextField.setColumns(15);
		userSearchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filterTransactionTable();
			}
		});
		transactionOptions.add(transactionSearchTextField);
		
		checkboxInclOrderings = new JCheckBox("inkl. Bestellungen");
		checkboxInclOrderings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				filterTransactionTable();
			}
		});
		transactionOptions.add(checkboxInclOrderings);

		transactionTable = new JTable() {
			private static final long serialVersionUID = 1L;
			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int colIndex) {
		          Component c = super.prepareRenderer(renderer, rowIndex, colIndex);
		          if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, colIndex)) {
		            c.setBackground(new Color(248,243,244));
		          } else {
		        	if (isRowSelected(rowIndex)) {
		        		c.setBackground(new Color(176,196,222));
		        	} else {
		        		c.setBackground(getBackground());
		        	}
		          }
		          return c;
		     }
		};
		JScrollPane scrollPaneTransactions = new JScrollPane();
		panelTransaction.add(scrollPaneTransactions, BorderLayout.CENTER);
		scrollPaneTransactions.setViewportView(transactionTable);
		
		JPanel panelOrderings = new JPanel();
		tabbedPane.addTab("Orderings", null, panelOrderings, null);
		panelOrderings.setLayout(new BorderLayout(0, 0));
		
		JPanel orderingOptions = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) orderingOptions.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		panelOrderings.add(orderingOptions, BorderLayout.NORTH);
		
		orderingSearchTextField = new JTextField();
		orderingSearchTextField.setColumns(15);
		orderingSearchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filterOrderingTable();
			}
		});
		orderingOptions.add(orderingSearchTextField);
		
		JScrollPane scrollPaneOrderings = new JScrollPane();
		panelOrderings.add(scrollPaneOrderings, BorderLayout.CENTER);
		
		orderingTable = new JTable();
		scrollPaneOrderings.setViewportView(orderingTable);

		JPanel panelKitchen = new JPanel();
		tabbedPane.addTab("Kitchen", null, panelKitchen, null);
		panelKitchen.setLayout(new BorderLayout(0, 0));

		JPanel kitchenOptions = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) kitchenOptions.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panelKitchen.add(kitchenOptions, BorderLayout.NORTH);

		kitchenSearchTextField = new JTextField();
		kitchenSearchTextField.setColumns(15);
		kitchenSearchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filterKitchenTable();
			}
		});
		kitchenOptions.add(kitchenSearchTextField);
		
		JButton buttonAddArticle = new JButton("+");
		buttonAddArticle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddArticleWindow articleWindow = new AddArticleWindow(kitchenTableModel);
				articleWindow.setVisible(true);
			}
		});
		kitchenOptions.add(buttonAddArticle);
		
		JButton btnGenerateBarcodes = new JButton("Generate Barcodes");
		btnGenerateBarcodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path = "npl-kitchen-barcodes.pdf";
				ArticleBarcodePdfGenerator g = new ArticleBarcodePdfGenerator(path);
				for(Article a : kitchenTableModel.getArticles()) {
					g.addData(a.getName(), String.valueOf(a.getPrice()), String.valueOf(a.getId()));
				}
				g.generate();
				try {
					Desktop.getDesktop().open(new File(path));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		kitchenOptions.add(btnGenerateBarcodes);

		kitchenTable = new JTable() {
			private static final long serialVersionUID = 1L;
			public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int colIndex) {
		          Component c = super.prepareRenderer(renderer, rowIndex, colIndex);
		          if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, colIndex)) {
		            c.setBackground(new Color(248,243,244));
		          } else {
		        	if (isRowSelected(rowIndex)) {
		        		c.setBackground(new Color(176,196,222));
		        	} else {
		        		c.setBackground(getBackground());
		        	}
		          }
		          return c;
		     }
		};
		JScrollPane scrollPaneKitchen = new JScrollPane();
		panelKitchen.add(scrollPaneKitchen, BorderLayout.CENTER);
		scrollPaneKitchen.setViewportView(kitchenTable);

		JPanel panelBar = new JPanel();
		tabbedPane.addTab("Bar", null, panelBar, null);
		panelBar.setLayout(new BorderLayout(0, 0));
		
		JPanel panelCenterBar = new JPanel();
		panelBar.add(panelCenterBar, BorderLayout.CENTER);
		panelCenterBar.setLayout(new GridLayout(0, 3, 0, 0));
		
		JPanel panelAvailableArticles = new JPanel();
		panelCenterBar.add(panelAvailableArticles);
		panelAvailableArticles.setLayout(null);
		
		JLabel labelAvailableArticles = new JLabel("Verf\u00FCgbar:");
		labelAvailableArticles.setBounds(32, 49, 94, 25);
		panelAvailableArticles.add(labelAvailableArticles);
		labelAvailableArticles.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JScrollPane scrollPaneAvailableArticles = new JScrollPane();
		scrollPaneAvailableArticles.setBounds(32, 87, 223, 299);
		panelAvailableArticles.add(scrollPaneAvailableArticles);
		
		availableArticlesTable = new JTable();
		scrollPaneAvailableArticles.setViewportView(availableArticlesTable);
		
		JPanel panelOrderingsBar = new JPanel();
		panelCenterBar.add(panelOrderingsBar);
		panelOrderingsBar.setLayout(null);
		
		JLabel labelCurrentOrdering = new JLabel("Bestellung:");
		labelCurrentOrdering.setFont(new Font("Tahoma", Font.PLAIN, 20));
		labelCurrentOrdering.setBounds(31, 48, 200, 25);
		panelOrderingsBar.add(labelCurrentOrdering);
		
		JScrollPane currentOrderingScrollPane = new JScrollPane();
		currentOrderingScrollPane.setBounds(31, 86, 228, 300);
		panelOrderingsBar.add(currentOrderingScrollPane);
		
		currentOrderingTable = new JTable();
		currentOrderingScrollPane.setViewportView(currentOrderingTable);
		
		JLabel lblGesamt = new JLabel("Gesamt:");
		lblGesamt.setBounds(169, 395, 56, 16);
		panelOrderingsBar.add(lblGesamt);
		
		labelBillTotal = new JLabel("0");
		labelBillTotal.setBounds(237, 395, 56, 16);
		panelOrderingsBar.add(labelBillTotal);
		
		panelBillBar = new JPanel();
		panelCenterBar.add(panelBillBar);
		panelBillBar.setLayout(null);
		panelBillBar.setVisible(false);
		
		JLabel labelBill = new JLabel("Quittung:");
		labelBill.setFont(new Font("Tahoma", Font.PLAIN, 20));
		labelBill.setBounds(66, 50, 164, 25);
		panelBillBar.add(labelBill);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(66, 88, 76, 16);
		panelBillBar.add(lblUsername);
		
		JLabel lblBezahlt = new JLabel("Bezahlt:");
		lblBezahlt.setBounds(66, 117, 76, 16);
		panelBillBar.add(lblBezahlt);
		
		JLabel lblSaldo = new JLabel("Saldo:");
		lblSaldo.setBounds(66, 146, 76, 16);
		panelBillBar.add(lblSaldo);
		
		labelBillSaldo = new JLabel("0");
		labelBillSaldo.setBounds(154, 146, 76, 16);
		panelBillBar.add(labelBillSaldo);
		
		labelBillPaid = new JLabel("0");
		labelBillPaid.setBounds(154, 117, 76, 16);
		panelBillBar.add(labelBillPaid);
		
		labelBillName = new JLabel("test0r");
		labelBillName.setBounds(154, 88, 76, 16);
		panelBillBar.add(labelBillName);
		
		JPanel panelBottomBar = new JPanel();
		panelBar.add(panelBottomBar, BorderLayout.SOUTH);
		panelBottomBar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblKommando = new JLabel("Kommando:");
		panelBottomBar.add(lblKommando);
		
		commandTextField = new JTextField();
		commandTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!commandTextField.getText().equals("")) {
						performCommand(commandTextField.getText());
						commandTextField.setText("");
					}
				}
			}
		});
		panelBottomBar.add(commandTextField);
		commandTextField.setColumns(15);
		
		Component horizontalStrut = Box.createHorizontalStrut(40);
		panelBottomBar.add(horizontalStrut);
		
		labelBarMessage = new JLabel("Messages");
		labelBarMessage.setVisible(false);
		
		Component verticalStrut = Box.createVerticalStrut(50);
		panelBottomBar.add(verticalStrut);
		labelBarMessage.setForeground(Color.BLACK);
		labelBarMessage.setFont(new Font("Tahoma", Font.PLAIN, 36));
		panelBottomBar.add(labelBarMessage);
		
		if (!Beans.isDesignTime()) {
			// MODELS
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			EntityManager em = factory.createEntityManager();

			// erstelle Syncher und mache gleich die erste Synchronisation
			userDownSynchronizer = new UserDownSynchronizer(userProvider, em);
			userDownSynchronizer.syncronize();
			
			userTableModel = new UserTableModel(em);
			userTable.setModel(userTableModel);
			userTable.setRowSorter(new TableRowSorter<UserTableModel>(userTableModel));
			userTable.getRowSorter().toggleSortOrder(0);
			
			transactionTableModel = new TransactionTableModel(em);
			transactionTable.setModel(transactionTableModel);
			transactionTable.setRowSorter(new TableRowSorter<TransactionTableModel>(transactionTableModel));
			transactionTable.getRowSorter().toggleSortOrder(0);
	
			orderingTableModel = new OrderingTableModel(em);
			orderingTable.setModel(orderingTableModel);
			orderingTable.setRowSorter(new TableRowSorter<OrderingTableModel>(orderingTableModel));
			orderingTable.getRowSorter().toggleSortOrder(0);			
			
			kitchenTableModel = new KitchenTableModel(em);
			kitchenTable.setModel(kitchenTableModel);
			kitchenTable.setRowSorter(new TableRowSorter<KitchenTableModel>(kitchenTableModel));
			kitchenTable.getRowSorter().toggleSortOrder(0);
			
			currentOrderingTableModel = new CurrentOrderingTableModel();
			currentOrderingTable.setModel(currentOrderingTableModel);
			
			availableArticlesTableModel = new AvailableArticlesTableModel(em);
			availableArticlesTable.setModel(availableArticlesTableModel);
		}
	}
	

	protected void filterUserTable() {
		@SuppressWarnings("unchecked")
		TableRowSorter<UserTableModel> sorterUsers = (TableRowSorter<UserTableModel>) userTable.getRowSorter();
		RowFilter<UserTableModel, Object> finalFilterUser = null;
		try {
			finalFilterUser = RowFilter.regexFilter(".*");
			ArrayList<RowFilter<UserTableModel, Object>> filtersUsers = new ArrayList<RowFilter<UserTableModel, Object>>(0);

			// Nehme Suchfeld als Filter dazu
			RowFilter<UserTableModel, Object> searchFilter = RowFilter.regexFilter(getWildcardRegex(userSearchTextField.getText()));
			filtersUsers.add(searchFilter);

			finalFilterUser = RowFilter.andFilter(filtersUsers);
		} catch (java.util.regex.PatternSyntaxException ex) {
			return;
		}
		sorterUsers.setRowFilter(finalFilterUser);
	}

	
	protected void filterTransactionTable() {
		@SuppressWarnings("unchecked")
		TableRowSorter<TransactionTableModel> sorterTransactions = (TableRowSorter<TransactionTableModel>) transactionTable.getRowSorter();
		RowFilter<TransactionTableModel, Object> finalFilterTransaction = null;
		try {
			finalFilterTransaction = RowFilter.regexFilter(".*");
			ArrayList<RowFilter<TransactionTableModel, Object>> filtersTransactions = new ArrayList<RowFilter<TransactionTableModel, Object>>(0);

			// Nehme Suchfeld als Filter dazu
			RowFilter<TransactionTableModel, Object> searchFilter = RowFilter.regexFilter(getWildcardRegex(transactionSearchTextField.getText()));
			filtersTransactions.add(searchFilter);
			
			// nehme Checkbox "inkl. Bestellungen" als Filer dazu
			if (checkboxInclOrderings.isSelected()) {
				RowFilter<TransactionTableModel, Object> availableFilterLoan = RowFilter.regexFilter("^0$", 3);
				filtersTransactions.add(availableFilterLoan);
			}
			
			finalFilterTransaction = RowFilter.andFilter(filtersTransactions);
		} catch (java.util.regex.PatternSyntaxException ex) {
			return;
		}
		sorterTransactions.setRowFilter(finalFilterTransaction);
	}

	
	protected void filterOrderingTable() {
		@SuppressWarnings("unchecked")
		TableRowSorter<OrderingTableModel> sorterOrdering = (TableRowSorter<OrderingTableModel>) orderingTable.getRowSorter();
		RowFilter<OrderingTableModel, Object> finalFilterOrdering = null;
		try {
			finalFilterOrdering = RowFilter.regexFilter(".*");
			ArrayList<RowFilter<OrderingTableModel, Object>> filtersOrdering = new ArrayList<RowFilter<OrderingTableModel, Object>>(0);

			// Nehme Suchfeld als Filter dazu
			RowFilter<OrderingTableModel, Object> searchFilter = RowFilter.regexFilter(getWildcardRegex(orderingSearchTextField.getText()));
			filtersOrdering.add(searchFilter);

			finalFilterOrdering = RowFilter.andFilter(filtersOrdering);
		} catch (java.util.regex.PatternSyntaxException ex) {
			return;
		}
		sorterOrdering.setRowFilter(finalFilterOrdering);
	}
	
	protected void filterKitchenTable() {
		@SuppressWarnings("unchecked")
		TableRowSorter<KitchenTableModel> sorterKitchen = (TableRowSorter<KitchenTableModel>) kitchenTable.getRowSorter();
		RowFilter<KitchenTableModel, Object> finalFilterKitchen = null;
		try {
			finalFilterKitchen = RowFilter.regexFilter(".*");
			ArrayList<RowFilter<KitchenTableModel, Object>> filtersKitchen = new ArrayList<RowFilter<KitchenTableModel, Object>>(0);

			// Nehme Suchfeld als Filter dazu
			RowFilter<KitchenTableModel, Object> searchFilter = RowFilter.regexFilter(getWildcardRegex(kitchenSearchTextField.getText()));
			filtersKitchen.add(searchFilter);

			finalFilterKitchen = RowFilter.andFilter(filtersKitchen);
		} catch (java.util.regex.PatternSyntaxException ex) {
			return;
		}
		sorterKitchen.setRowFilter(finalFilterKitchen);
	}	
	
	// source: http://www.rgagnon.com/javadetails/java-0515.html
	public String getWildcardRegex(String wildcard) {
		StringBuffer s = new StringBuffer(wildcard.length());
		Boolean justHadAStar = false;
		s.append("(?i)^.*");
		for (int i = 0, is = wildcard.length(); i < is; i++) {
			char c = wildcard.charAt(i);
			switch (c) {
			case '*':
				if (!justHadAStar) {
					s.append(".*");
					justHadAStar = true;
				}
				break;
			case '?':
				s.append(".");
				justHadAStar = false;
				break;
			// escape special regexp-characters
			case '(':
			case ')':
			case '[':
			case ']':
			case '$':
			case '^':
			case '.':
			case '{':
			case '}':
			case '|':
			case '\\':
				s.append("\\");
				s.append(c);
				justHadAStar = false;
				break;
			default:
				s.append(c);
				justHadAStar = false;
				break;
			}
		}
		s.append(".*$");
		return (s.toString());
	}

	protected void performCommand(String s) {
		switch (s.charAt(0)) {
		case '0': // internal command
			switch (s) {
			case "0-01":
				barMode = BarMode.ADD;
				showSuccess("Hinzufügen-Modus");
				break;
			case "0-02":
				barMode = BarMode.REMOVE;
				showSuccess("Entfernen-Modus");
				break;
			
			case "0-99":
				barMode = BarMode.ADD;
				currentOrderingTableModel.getPositions().clear();
				currentOrderingTableModel.fireTableDataChanged();
				labelBillTotal.setText(String.valueOf(currentOrderingTableModel.getTotal()));
				showSuccess("Alle entfernt");
				break;
			}
			break;
		case 'G': // RFID was applied as signature
			executeOrdering(s);
			barMode = BarMode.ADD;
			break;
		default: // product shall be added/removed
			int id;
			try {
				id = Integer.valueOf(s);
			} catch(NumberFormatException e) {
				showError("Artikelnummer ungültig");
				return;
			}
			
			Article a = kitchenTableModel.getArticle(id);
			if (a != null) {
				if (barMode == BarMode.ADD) {
					if (a.isAvailable()) {
						OrderingPosition op = new OrderingPosition();
						op.setArticle(a);
						currentOrderingTableModel.addPosition(op);
						labelBillTotal.setText(String.valueOf(currentOrderingTableModel.getTotal()));
						showSuccess("+");
					} else {
						showError("Artikel nicht verfügbar");
					}
				} else if (barMode == BarMode.REMOVE) {
					for (OrderingPosition op : currentOrderingTableModel.getPositions()) {
						if (op.getArticle() == a) {
							currentOrderingTableModel.removePosition(op);
							labelBillTotal.setText(String.valueOf(currentOrderingTableModel.getTotal()));
							showSuccess("-");
							break;
						}
					}
				}
			} else {
				showError("Artikel nicht vorhanden");
			}
			break;
		}
	}
	
	public void showSuccess(String errorText) {
		labelBarMessage.setForeground(Color.GREEN);
		showMessage(errorText);
	}
	
	public void showError(String errorText) {
		labelBarMessage.setForeground(Color.RED);
		showMessage(errorText);
	}
	
	public void showMessage(String messageText) {
		labelBarMessage.setText(messageText);
		labelBarMessage.setVisible(true);
		Thread t = new Thread(new Runnable() {
		    public void run() {
		    	try { Thread.sleep(3000); } catch (InterruptedException e) {}
		        SwingUtilities.invokeLater(new Runnable()
		        {
		            public void run()
		            {
		            	labelBarMessage.setVisible(false);
		            	labelBarMessage.setText("");
		            }
		        });
		    }
		});
		t.start();
	}
	
	public void showBill(String username, double paid, double saldo) {
		labelBillName.setText(username);
		labelBillPaid.setText(String.valueOf(paid));
		labelBillSaldo.setText(String.valueOf(saldo));
		panelBillBar.setVisible(true);
		Thread t = new Thread(new Runnable() {
		    public void run() {
		    	try { Thread.sleep(10000); } catch (InterruptedException e) {}
		        SwingUtilities.invokeLater(new Runnable()
		        {
		            public void run()
		            {
		            	panelBillBar.setVisible(false);
		            }
		        });
		    }
		});
		t.start();
	}
	
	public void executeOrdering(String rfid) {
		userTableModel.reload();
		availableArticlesTableModel.reload();
		List<OrderingPosition> positions = currentOrderingTableModel.getPositions();
		if (positions == null || positions.size() == 0) {
			showError("Keine Artikel ausgewählt");
			return;
		}
		
		UserMoney m = userTableModel.getUserMoneyByRfid(rfid);
		if (m == null) {
			showError("Benutzer nicht gefunden");
			return;
		}
		
		Ordering b = new Ordering();
		for (OrderingPosition p : positions) {
			p.setOrdering(b);
			b.addPosition(p);
		}
		
		try {
			m.executeOrdering(b);
			userTableModel.saveUserMoney(m);
			showBill(m.getUsername(), b.getTotal(), m.getBalance());
			currentOrderingTableModel.getPositions().clear();
			currentOrderingTableModel.fireTableDataChanged();
			labelBillTotal.setText("0");
		} catch (TooLessMoneyException e) {
			showError("Zu wenig Geld");
		}
	}
}
