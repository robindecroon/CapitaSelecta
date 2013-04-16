/**
 * 
 */
package core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import layout.VerticalFlowLayout;
import processing.core.PApplet;

/**
 * @author Robin
 * 
 */
public class Main {

	private static Label authors = new Label();
	private static Label affiliations = new Label();
	private static Label year = new Label();
	private static Label conference = new Label();
	private static Label fullText = new Label();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		initLabels();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension size = toolkit.getScreenSize();

		final Frame frame = initFrame();

		JPanel toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel timeline = initTimeLine();
		
		JPanel conference = initConferenceCheckBoxes();

		JSeparator sep = new JSeparator(JSeparator.VERTICAL);
		
		JPanel searchWrapper = initSearch();
		
		toolbar.add(timeline);
		toolbar.add(conference);
		toolbar.add(sep);
		toolbar.add(searchWrapper);

		Panel sidebar = initSidebar();
		
		final PApplet mainApplet = new MainApplet();
		mainApplet.init();
		frame.add(toolbar, BorderLayout.NORTH);
		frame.add(mainApplet, BorderLayout.CENTER);
		frame.add(sidebar, BorderLayout.EAST);
		frame.pack();

		Dimension frameSize = frame.getSize();
		frame.setLocation((size.width - frameSize.width) / 2,
				(size.height - frameSize.height) / 2);
		frame.setVisible(true);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent we) {
						System.out.println("exit pressed");
						System.exit(0);
					}
				});
			}
		});
	}

	private static JPanel initSearch() throws IOException {
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		final TextField tf1 = new TextField(Constants.TEXTBOX1, 40);
		tf1.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				tf1.setText("");
			}
		});


		JButton searchButton = new JButton("Search...");
		BufferedImage searchIcon = ImageIO.read(new File("data/search.png"));
		searchButton.setIcon(new ImageIcon(searchIcon));
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text1 = tf1.getText();
				if (!text1.equals(Constants.TEXTBOX1)) {
					System.out.println("To Search: " + text1);
					// TODO
				}
			}
		});
		searchPanel.add(searchButton);

		searchPanel.add(tf1);
		
		JPanel searchWrapper = new JPanel();
		searchWrapper.setLayout(new VerticalFlowLayout());
		JLabel searchTemp = new JLabel();
		searchTemp.setPreferredSize(new Dimension(200,20));
		searchWrapper.add(searchTemp);
		searchWrapper.add(searchPanel);
		searchWrapper.setBorder(BorderFactory.createEmptyBorder(0,0,0, 10));
		return searchWrapper;
	}

	private static JPanel initTimeLine() throws IOException {
		JPanel timeline = new JPanel();
		timeline.setLayout(new VerticalFlowLayout());
		JPanel temp = new JPanel();
		JLabel tempLabel = new JLabel();
		tempLabel.setPreferredSize(new Dimension(70, 20));
		temp.add(tempLabel);
		temp.add(initYearCheckBoxes());
		timeline.add(temp);
		BufferedImage myPicture = ImageIO.read(new File("data/timeline.png"));
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		timeline.add(picLabel);
		return timeline;
	}

	private static JPanel initConferenceCheckBoxes() {
		JPanel conference = new JPanel();
		conference.setLayout(new VerticalFlowLayout());
		JCheckBox edm = new JCheckBox("edm");
		JCheckBox lak = new JCheckBox("lak");

		conference.add(edm);
		conference.add(lak);
		return conference;
	}

	public void setAuthors(String authors) {
		Main.authors.setText(authors);
	}

	public void setAffiliations(String affiliations) {
		Main.affiliations.setText(affiliations);
	}

	public void setYear(String year) {
		Main.year.setText(year);
	}

	public void setConference(String conference) {
		Main.conference.setText(conference);
	}

	public void setFullText(String fullText) {
		Main.fullText.setText(fullText);
	}

	private static void initLabels() {
		authors.setBackground(Color.WHITE);
		authors.setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH,
				Constants.SIDEBAR_LABEL_HEIGHT));
		affiliations.setBackground(Color.WHITE);
		affiliations.setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH,
				Constants.SIDEBAR_LABEL_HEIGHT));
		year.setBackground(Color.WHITE);
		year.setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH,
				Constants.SIDEBAR_LABEL_HEIGHT));
		conference.setBackground(Color.WHITE);
		conference.setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH,
				Constants.SIDEBAR_LABEL_HEIGHT));
		fullText.setBackground(Color.WHITE);
		fullText.setPreferredSize(new Dimension(Constants.SIDEBAR_WIDTH,
				Constants.FULLTEXT_HEIGHT));
	}

	private static Panel initSidebar() {
		Panel sidebar = new Panel();
		sidebar.setPreferredSize(new Dimension(300, 0));
		sidebar.setLayout(new VerticalFlowLayout());
		sidebar.setBackground(new Color(240, 240, 240));

		Label authorLabel = new Label(Constants.AUTHOR_LABEL);
		Label affiliationLabel = new Label(Constants.AFFILATION_LABEL);
		Label yearLabel = new Label(Constants.YEAR_LABEL);
		Label conferenceLabel = new Label(Constants.CONFERENCE_LABEL);
		Label textLabel = new Label(Constants.TEXT_LABEL);

		sidebar.add(authorLabel);
		sidebar.add(authors);
		sidebar.add(affiliationLabel);
		sidebar.add(affiliations);
		sidebar.add(yearLabel);
		sidebar.add(year);
		sidebar.add(conferenceLabel);
		sidebar.add(conference);
		sidebar.add(textLabel);
		sidebar.add(fullText);

		return sidebar;
	}

	private static Frame initFrame() {
		Frame frame = new Frame(Constants.APP_NAME);
		frame.setLayout(new BorderLayout());

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		return frame;
	}

	private static JPanel initYearCheckBoxes() throws IOException {
		JPanel toolbar = new JPanel();
		toolbar.setLayout(new GridLayout());
		toolbar.setPreferredSize(new Dimension(800, 50));

		final JCheckBox box2008 = new JCheckBox();
		box2008.setText("2008");
		box2008.setFocusPainted(true);
		box2008.setSelectedIcon(new ImageIcon("data/arrow.PNG"));
		box2008.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (box2008.isSelected()) {
					box2008.setForeground(Color.RED);
					System.out.println("2008 selected!");
				} else {
					box2008.setForeground(Color.BLACK);
					System.out.println("2008 deselected!");
				}
			}
		});
		final JCheckBox box2009 = new JCheckBox();
		box2009.setText("2009");
		box2009.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (box2009.isSelected()) {
					box2009.setForeground(Color.BLUE);
					System.out.println("2009 selected!");
				} else {
					box2009.setForeground(Color.BLACK);
					System.out.println("2009 deselected!");
				}
			}
		});
		final JCheckBox box2010 = new JCheckBox();
		box2010.setText("2010");
		box2010.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (box2010.isSelected()) {
					box2010.setForeground(Color.GREEN);
					System.out.println("2010 selected!");
				} else {
					box2010.setForeground(Color.BLACK);
					System.out.println("2010 deselected!");
				}
			}
		});
		final JCheckBox box2011 = new JCheckBox();
		box2011.setText("2011");
		box2011.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (box2011.isSelected()) {
					box2011.setForeground(Color.MAGENTA);
					System.out.println("2011 selected!");
				} else {
					box2011.setForeground(Color.BLACK);
					System.out.println("2011 deselected!");
				}
			}
		});
		final JCheckBox box2012 = new JCheckBox();
		box2012.setText("2012");
		box2012.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (box2012.isSelected()) {
					box2012.setForeground(Color.ORANGE);
					System.out.println("2012 selected!");
				} else {
					box2012.setForeground(Color.BLACK);
					System.out.println("2012 deselected!");
				}
			}
		});

		toolbar.add(box2008);
		toolbar.add(box2009);
		toolbar.add(box2010);
		toolbar.add(box2011);
		toolbar.add(box2012);

		return toolbar;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	public ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	// private static void clearPaperColors() {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// for (Paper paper : Database.getInstance().getPapers()) {
	// paper.setColor(KeywordColor.BLUE);
	// }
	// }
	// }).start();
	// }

}
