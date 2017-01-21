package textSearch;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Scanner;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

@SuppressWarnings("serial")
public class textSearch extends JPanel implements ActionListener {
	private int width, height;
	private JButton buttonOne, buttonTwo;
	private JRadioButton alpha;
	private TextArea firstTextBox, resultBoxTwo;
	private JLabel firstFileLabel, secondFileLabel, nLabel, scoreLabel;
	private SuperString[] array0, array1;
	private JTextField nField;
	private String n;
	private JButton match;

	public textSearch() {
		JPanel upperPanel = new JPanel();
		JPanel subpanel = new JPanel();
		width = 1000;
		height = 600;
		nField = new JTextField("2", 3);
		alpha = new JRadioButton("Alphabetical");
		buttonOne = new JButton("Open File");
		firstFileLabel = new JLabel("No file selected");
		buttonTwo = new JButton("Open File");
		secondFileLabel = new JLabel("No file selected");
		nLabel = new JLabel("Size of N ");
		scoreLabel = new JLabel("Match Score ");
		match = new JButton("Compute match");
		firstTextBox = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		resultBoxTwo = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);

		setLayout(new BorderLayout());
		subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.X_AXIS));
		firstTextBox.setPreferredSize(new Dimension(width / 2, height * 3 / 5));
		resultBoxTwo.setPreferredSize(new Dimension(width / 2, height * 3 / 5));
		setPreferredSize(new Dimension(width, height));

		buttonOne.addActionListener(this);
		buttonTwo.addActionListener(this);
		match.addActionListener(this);
		upperPanel.add(alpha);
		upperPanel.add(buttonOne);
		upperPanel.add(firstFileLabel);
		upperPanel.add(buttonTwo);
		upperPanel.add(secondFileLabel);
		upperPanel.add(nField);
		upperPanel.add(nLabel);
		upperPanel.add(match);
		upperPanel.add(scoreLabel);
		subpanel.add(firstTextBox);
		subpanel.add(resultBoxTwo);
		add(subpanel, BorderLayout.CENTER);
		add(upperPanel, BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == match) {
			NumberFormat formatter = new DecimalFormat("#.###");
			double result = (MatchTools.match(array0, array1));
			String formattedResult = formatter.format(result);
			scoreLabel.setText(formattedResult + " ");
			return;
		}

		JFileChooser chooser = new JFileChooser("../Text");
		int returnValue = chooser.showOpenDialog(null);
		if (returnValue != JFileChooser.APPROVE_OPTION)
			return;
		File file = chooser.getSelectedFile();
		String filename = file.getName();

		SuperString[] array = loadfile(file);

		if (!alpha.isSelected())
			Arrays.sort(array, new SuperStringComparator());
		if (event.getSource() == buttonOne) {
			array0 = array;
			firstFileLabel.setText(filename);
			firstTextBox.setText("");
			StringBuilder sb = new StringBuilder();
			for (SuperString token : array0)
				sb.append(token + "\n");
			firstTextBox.setText(sb.toString());
		}

		if (event.getSource() == buttonTwo) {
			array1 = array;
			secondFileLabel.setText(filename);
			resultBoxTwo.setText("");
			StringBuilder sb = new StringBuilder();
			for (SuperString token : array1)
				sb.append(token + "\n");
			resultBoxTwo.setText(sb.toString());
		}

	}

	private SuperString[] loadfile(File file) {
		BinaryCountTree<SuperString> tree = new BinaryCountTree<SuperString>();
		try {
			StringBuilder sb = new StringBuilder();
			Scanner scan = new Scanner(file);
			n = nField.getText();
			Integer limit = Integer.parseInt(n);

			while (scan.hasNext()) {
				String scanned = scan.next() + " ";
				scanned = scanned.replaceAll("\\p{P}", "").replaceAll("^[ \r\t\n]*$", "").toLowerCase();
				sb.append(scanned);
			}

			String[] superArray = sb.toString().split(" ");
			String input[] = new String[limit];

			for (int i = 0; i < superArray.length; i++) {
				if ((i + limit) <= superArray.length) {
					for (int j = 0; j < limit; j++) {
						input[j] = superArray[i + j];
					}
					tree.add(new SuperString(input));
				}
			}

			scan.close();
		} catch (FileNotFoundException err) {
			System.err.println("File read error");
			return null;
		}

		SuperString[] array = new SuperString[tree.size()];
		int i = 0;
		for (SuperString token : tree) {
			array[i] = token;
			i++;
		}
		return array;
	}

	public static void main(String[] arg) {
		JFrame frame = new JFrame("textSearch");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new textSearch());
		frame.pack();
		frame.setVisible(true);
	}
}