package swt;

import java.awt.Dimension;
import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import util.Logger;
import core.MainApplet;
import filter.Filter;
import filter.GeneralFilter;

public class SWTGui {
	public static final Display display = new Display();
	public static final Shell shell = new Shell(display);
	public static MainApplet applet;
	public static SWTGui instance;

	private Timeline timeline;
	private ConferenceTool conference;

	private SWTGui() {
	}

	public void start() {
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		initControls(shell);
		initApplet(shell);

		shell.pack();

		// Monitor dimension
		Monitor monitor = display.getPrimaryMonitor();
		Rectangle mSize = monitor.getBounds();
		Rectangle sSize = shell.getBounds();

		// Set the location
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

		// Open the shell and execute it's events.
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
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

	public void initApplet(Composite parent) {
		Composite component = new Composite(parent, SWT.EMBEDDED
				| SWT.NO_BACKGROUND);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = MainApplet.APPLET_WIDTH;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessVerticalSpace = true;
		data.heightHint = MainApplet.APPLET_HEIGHT;
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
		System.out.println("Filter changed!");
		Filter filter = new GeneralFilter(timeline.getYears(),
				conference.getConferenceList());
		applet.getMap().getWordCloudManager().setFilter(filter);

	}

	public static void main(String[] args) {
		instance = new SWTGui();
		instance.start();
	}
}
