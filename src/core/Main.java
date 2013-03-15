/**
 * 
 */
package core;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import layout.VerticalFlowLayout;
import processing.core.PApplet;
import threads.KeywordRunnable;
import util.KeywordColor;
import data.Database;
import data.Paper;

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
	 */
	public static void main(String[] args) {
		initLabels();

		Frame frame = initFrame();
		Panel toolbar = initToolbar();
		Panel sidebar = initSidebar();
		final PApplet mainApplet = new MainApplet();
		mainApplet.init();
		frame.add(toolbar, BorderLayout.NORTH);
		frame.add(mainApplet, BorderLayout.CENTER);
		frame.add(sidebar, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);
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

	private static Panel initToolbar() {
		Panel toolbar = new Panel();
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

		Label label = new Label(Constants.TOOLBAR_TEXT);
		toolbar.add(label);

		final TextField tf1 = new TextField(Constants.TEXTBOX1, 30);
		tf1.setBackground(Color.RED);
		tf1.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				tf1.setText("");

			}
		});
		toolbar.add(tf1);

		final TextField tf2 = new TextField(Constants.TEXTBOX2, 30);
		tf2.setBackground(Color.GREEN);
		tf2.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				tf2.setText("");

			}
		});
		toolbar.add(tf2);
		toolbar.setBackground(new Color(240, 240, 240));

		Button searchButton = new Button(Constants.GO_BUTTON_TEXT);
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text1 = tf1.getText();
				String text2 = tf2.getText();
				if(text1.equals(text2)) {
					return;
				}
				if(!text1.equals(Constants.TEXTBOX1) || !text1.equals("")) {
					Thread searchThread = new Thread(new KeywordRunnable(text1, KeywordColor.RED));
					searchThread.start();					
				}
				if(!text2.equals(Constants.TEXTBOX2) || !text2.equals("")) {
					Thread searchThread = new Thread(new KeywordRunnable(text2, KeywordColor.GREEN));
					searchThread.start();					
				}
			}
		});
		toolbar.add(searchButton);
		
		Button clearButton = new Button(Constants.CLEAR_BUTTON_TEXT);
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						for(Paper paper : Database.getInstance().getPapers()) {
							paper.setColor(KeywordColor.BLUE);
						}
					}
				}).start();
			}
		});
		toolbar.add(clearButton);
		return toolbar;
	}

}
