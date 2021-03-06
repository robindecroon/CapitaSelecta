package swt;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import util.FileLogger;
import util.Logger;
import core.Constants;
import core.MainApplet;
import data.Author;
import data.Paper;
import filter.Filter;
import filter.GeneralFilter;

public class SWTGui {
	public static final Display display = new Display();
	public static final Shell shell = new Shell(display);
	public static MainApplet applet;
	public static SWTGui instance;

	public Paper currentPaper;
	public Text title;
	public Text authors;
	public Text conferenceText;
	public Text year;
	public Text fullText;

	public Timeline timeline;
	public ConferenceTool conference;
	public boolean canChange = true;

	private SWTGui() {
	}

	public void start() {
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setText(Constants.APP_NAME);

		ImageData[] data = new ImageLoader().load("data/image/author.png");
		Image image = new Image(display, data[0]);
		shell.setImage(image);

		initControls(shell);
		initAppletWithPaperData(shell);

		shell.pack();

		// Monitor dimension
		Monitor monitor = display.getPrimaryMonitor();
		Rectangle mSize = monitor.getBounds();
		Rectangle sSize = shell.getBounds();

		// Set the location
		shell.setMinimumSize(MainApplet.APPLET_WIDTH + 256,
				MainApplet.APPLET_HEIGHT + 128);
		shell.setLocation(mSize.x + (mSize.width - sSize.width) / 2, mSize.y
				+ (mSize.height - sSize.height) / 2);

		// Listen for close button
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				Logger.Info("close button is clicked");
				System.exit(0);
			}
		});

		FileLogger logger = new FileLogger() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see util.LogAdapter#Severe(java.lang.String)
			 */
			@Override
			public void Severe(final String message) {
				super.Severe(message);
				display.asyncExec(new Runnable() {
					public void run() {
						MessageBox box = new MessageBox(shell, SWT.ERROR
								| SWT.OK);
						box.setText("Error");
						box.setMessage("An error occured with the following debug message...\n\n"
								+ message);
						box.open();
					}
				});
			}
		};
		Logger.addListener(logger);

		// Open the shell and execute it's events.
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
		
		logger.close();
	}

	public void initControls(Composite parent) {
		Composite controlPanel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(5, false);
		controlPanel.setLayout(layout);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		controlPanel.setLayoutData(data);

		initTimeLine(controlPanel);
		initSeperator(controlPanel);
		initConferenceControl(controlPanel);
		initSeperator(controlPanel);
		initSearchTool(controlPanel);
	}

	private void initSeperator(Composite parent) {
		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = false;
		data.verticalAlignment = GridData.FILL;
		label.setLayoutData(data);
	}

	private void initTimeLine(Composite parent) {
		timeline = new Timeline(parent, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		timeline.setLayoutData(data);

	}

	private void initConferenceControl(Composite parent) {
		conference = new ConferenceTool(parent, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.CENTER;
		data.verticalAlignment = GridData.BEGINNING;
		conference.setLayoutData(data);

	}

	private void initSearchTool(Composite parent) {
		SearchTool search = new SearchTool(parent, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.CENTER;
		data.verticalAlignment = GridData.BEGINNING;
		search.setLayoutData(data);
	}

	public void initAppletWithPaperData(Composite parent) {
		Composite component = new Composite(parent, SWT.BORDER);
		component.setLayout(new GridLayout(2, false));

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		component.setLayoutData(data);

		initApplet(component);
		initPaperData(component);
	}

	public void initPaperData(Composite parent) {
		Composite component = new Composite(parent, SWT.NONE);
		component.setLayout(new GridLayout(1, false));

		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		component.setLayoutData(data);

		Label label = new Label(component, SWT.NONE);
		label.setText("Title");
		label.setLayoutData(getCenterLabelData());

		title = new Text(component, SWT.SINGLE | SWT.BORDER);
		title.setText("");
		title.setEditable(false);
		title.setLayoutData(getFillTextData());

		label = new Label(component, SWT.NONE);
		label.setText("Authors");
		label.setLayoutData(getCenterLabelData());

		authors = new Text(component, SWT.SINGLE | SWT.BORDER);
		authors.setText("");
		authors.setEditable(false);
		authors.setLayoutData(getFillTextData());

		label = new Label(component, SWT.NONE);
		label.setText("Conference");
		label.setLayoutData(getCenterLabelData());
		
		conferenceText = new Text(component, SWT.SINGLE | SWT.BORDER);
		conferenceText.setText("");
		conferenceText.setEditable(false);
		conferenceText.setLayoutData(getFillTextData());

		label = new Label(component, SWT.NONE);
		label.setText("Year");
		label.setLayoutData(getCenterLabelData());

		year = new Text(component, SWT.SINGLE | SWT.BORDER);
		year.setText("");
		year.setEditable(false);
		year.setLayoutData(getFillTextData());

		label = new Label(component, SWT.NONE);
		label.setText("Full text");
		label.setLayoutData(getCenterLabelData());

		fullText = new Text(component, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		fullText.setText("");
		fullText.setEditable(false);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		fullText.setLayoutData(data);

	}

	public void initApplet(Composite parent) {
		Composite component = new Composite(parent, SWT.EMBEDDED
				| SWT.NO_BACKGROUND);
		/**
		 * Applet
		 */
		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = false;
		data.widthHint = MainApplet.APPLET_WIDTH;
		data.minimumHeight = MainApplet.APPLET_WIDTH;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessVerticalSpace = true;
		data.heightHint = MainApplet.APPLET_HEIGHT;
		data.minimumHeight = MainApplet.APPLET_HEIGHT;
		component.setLayoutData(data);

		Frame frame = SWT_AWT.new_Frame(component);
		applet = new MainApplet();
		applet.init();

		Dimension size = new Dimension(MainApplet.APPLET_WIDTH,
				MainApplet.APPLET_HEIGHT);
		frame.setSize(size);
		frame.setPreferredSize(size);
		frame.add(applet);
		frame.pack();
	}

	public void filterChanged() {
		if (applet.getMap() == null)
			return;
		if (applet.getMap().getWordCloudManager() == null)
			return;
		Filter filter = new GeneralFilter(timeline.getYears(),
				conference.getConferenceList());
		applet.getMap().getWordCloudManager().setFilter(filter);

	}

	public static void main(String[] args) {
		instance = new SWTGui();
		instance.start();
	}

	private GridData getCenterLabelData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		return data;
	}

	private GridData getFillTextData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		return data;
	}

	private void changeTextField(final Text text, final String value) {
		if (text == null)
			return;
		display.asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				text.setText(value);
			}
		});
	}

	public void setPaper(Paper paper) {
		if (!canChange || currentPaper != null && paper.equals(currentPaper))
			return;
		canChange = false;
		currentPaper = paper;
		changeTextField(title, paper.getName());
		changeTextField(conferenceText, paper.getConference().toString().toUpperCase());

		String authorString = "";
		for (Author author : paper.getAuthors())
			authorString += author.getFullName() + "; ";
		changeTextField(authors, authorString);
		changeTextField(year, "" + paper.getYear());

		String[] split = paper.getFullText().split("\n");
		List<String> parts = new ArrayList<String>();

		for (String str : split) {
			String[] newSplit = str.split(" ");

			for (int i = 0; i < newSplit.length; i += 16) {
				String newStr = "";
				for (int k = i; k < Math.min(newSplit.length, i + 16); k++)
					newStr += newSplit[k] + " ";
				parts.add(newStr);
			}
		}

		String full = "";
		for (String str : parts)
			full += str + "\n";

		changeTextField(fullText, full);

		System.out.println(paper.getAuthors().get(0).getUniversity());
	}
}
