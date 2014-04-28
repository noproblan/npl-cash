package ch.npl.cash.client;

import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;

import ch.npl.cash.client.viewmodel.KitchenTableModel;
import ch.npl.cash.domain.Article;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AddArticleWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldName;
	private JTextField textFieldPrice;
	private JCheckBox checkboxAvailable;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddArticleWindow frame = new AddArticleWindow(null);
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
	public AddArticleWindow(final KitchenTableModel kitchenModel) {
		setModal(true);
		
		setTitle("Neuer Artikel");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 279, 196);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {50, 50};
		gbl_contentPane.rowHeights = new int[] {22, 22, 25, 25};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
				
		JLabel labelName = new JLabel("Name:");
		labelName.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_labelName = new GridBagConstraints();
		gbc_labelName.anchor = GridBagConstraints.EAST;
		gbc_labelName.insets = new Insets(0, 0, 5, 5);
		gbc_labelName.gridx = 0;
		gbc_labelName.gridy = 0;
		contentPane.add(labelName, gbc_labelName);
		
		textFieldName = new JTextField();
		GridBagConstraints gbc_textFieldName = new GridBagConstraints();
		gbc_textFieldName.anchor = GridBagConstraints.NORTHWEST;
		gbc_textFieldName.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldName.gridwidth = 2;
		gbc_textFieldName.gridx = 1;
		gbc_textFieldName.gridy = 0;
		contentPane.add(textFieldName, gbc_textFieldName);
		textFieldName.setColumns(10);
		
		JLabel lblPreis = new JLabel("Preis:");
		lblPreis.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblPreis = new GridBagConstraints();
		gbc_lblPreis.anchor = GridBagConstraints.EAST;
		gbc_lblPreis.insets = new Insets(0, 0, 5, 5);
		gbc_lblPreis.gridx = 0;
		gbc_lblPreis.gridy = 1;
		contentPane.add(lblPreis, gbc_lblPreis);
		
		textFieldPrice = new JTextField();
		textFieldPrice.setColumns(10);
		GridBagConstraints gbc_textFieldPrice = new GridBagConstraints();
		gbc_textFieldPrice.anchor = GridBagConstraints.NORTHWEST;
		gbc_textFieldPrice.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldPrice.gridwidth = 2;
		gbc_textFieldPrice.gridx = 1;
		gbc_textFieldPrice.gridy = 1;
		contentPane.add(textFieldPrice, gbc_textFieldPrice);
		
		checkboxAvailable = new JCheckBox("Verf\u00FCgbar");
		GridBagConstraints gbc_checkboxAvailable = new GridBagConstraints();
		gbc_checkboxAvailable.insets = new Insets(0, 0, 5, 0);
		gbc_checkboxAvailable.anchor = GridBagConstraints.NORTHWEST;
		gbc_checkboxAvailable.gridwidth = 2;
		gbc_checkboxAvailable.gridx = 1;
		gbc_checkboxAvailable.gridy = 2;
		contentPane.add(checkboxAvailable, gbc_checkboxAvailable);
		
		JButton buttonSave = new JButton("Speichern");
		buttonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Article a = new Article();
				a.setName(textFieldName.getText());
				a.setPrice(Double.valueOf(textFieldPrice.getText()));
				a.setAvailable(checkboxAvailable.isSelected());
				kitchenModel.addArticle(a);
			}
		});
		buttonSave.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_buttonSave = new GridBagConstraints();
		gbc_buttonSave.gridwidth = 2;
		gbc_buttonSave.anchor = GridBagConstraints.NORTHWEST;
		gbc_buttonSave.gridx = 1;
		gbc_buttonSave.gridy = 3;
		contentPane.add(buttonSave, gbc_buttonSave);
	}

}
