package swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import filter.PaperColor;

public class Timeline extends Composite {
	private Button box2008;
	private Button box2009;
	private Button box2010;
	private Button box2011;
	private Button box2012;

	/**
	 * 
	 * @param parent
	 * @param style
	 */
	public Timeline(Composite parent, int style) {
		super(parent, style);

		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);

		initCheckboxes(this);
		initTimeline(this);
	}

	private void initTimeline(Composite parent) {
		Canvas canvas = new Canvas(parent, SWT.NO_BACKGROUND);
		canvas.addPaintListener(new PaintListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.PaintListener#paintControl(org.eclipse
			 * .swt.events.PaintEvent)
			 */
			@Override
			public void paintControl(PaintEvent e) {
				int xDevision = e.width / 5;
				int yOffsets = 8;

				/**
				 * Draw the first four colors.
				 */
				for (int i = 0; i < PaperColor.colors.length - 1; i++) {
					PaperColor c = PaperColor.colors[i];

					e.gc.setBackground(new Color(SWTGui.display, c.red,
							c.green, c.blue));
					e.gc.fillRectangle(e.x + i * xDevision, e.y + yOffsets,
							xDevision, e.height - 2 * yOffsets);
					e.gc.setForeground(SWTGui.display
							.getSystemColor(SWT.COLOR_BLACK));
					e.gc.drawRectangle(e.x + i * xDevision, e.y + yOffsets,
							xDevision, e.height - 2 * yOffsets);
				}

				/**
				 * Draw an arrow on the last one
				 */
				int i = PaperColor.colors.length - 1;
				PaperColor c = PaperColor.colors[i];
				int xLeft = e.x + (i + 1) * xDevision - 32 - 1;
				int xRight = e.x + (i + 1) * xDevision - 1;
				int yTop = e.y;
				int yBottom = e.y + e.height;
				int[] path = new int[] { xLeft, yTop, xLeft, yBottom, xRight,
						(yTop + yBottom) / 2 };
				e.gc.setBackground(new Color(SWTGui.display, c.red, c.blue,
						c.green));
				e.gc.fillRectangle(e.x + i * xDevision, e.y + yOffsets,
						xDevision - 32, e.height - 2 * yOffsets);
				e.gc.fillPolygon(path);

				int[] fullPath = new int[] { e.x + i * xDevision,
						e.y + yOffsets, xLeft, e.y + yOffsets, xLeft, yTop,
						xRight, (yTop + yBottom) / 2, xLeft, yBottom, xLeft,
						e.y + e.height - yOffsets, e.x + i * xDevision,
						e.y + e.height - yOffsets };
				e.gc.drawPolygon(fullPath);

			}
		});

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.heightHint = 48;
		canvas.setLayoutData(data);

	}

	private void initCheckboxes(Composite parent) {
		Composite checkBoxWrapper = new Composite(parent, SWT.NONE);
		checkBoxWrapper.setLayout(new GridLayout(5, true));

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		checkBoxWrapper.setLayoutData(data);

		box2008 = new Button(checkBoxWrapper, SWT.CHECK);
		box2008.setText("2008");
		box2008.setSelection(true);
		box2008.setLayoutData(SWTUtils.getCheckBoxData(GridData.CENTER));
		box2008.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				SWTGui.instance.filterChanged();
			}
		});

		box2009 = new Button(checkBoxWrapper, SWT.CHECK);
		box2009.setText("2009");
		box2009.setSelection(true);
		box2009.setLayoutData(SWTUtils.getCheckBoxData(GridData.CENTER));
		box2009.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				SWTGui.instance.filterChanged();
			}
		});

		box2010 = new Button(checkBoxWrapper, SWT.CHECK);
		box2010.setText("2010");
		box2010.setSelection(true);
		box2010.setLayoutData(SWTUtils.getCheckBoxData(GridData.CENTER));
		box2010.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				SWTGui.instance.filterChanged();
			}
		});

		box2011 = new Button(checkBoxWrapper, SWT.CHECK);
		box2011.setText("2011");
		box2011.setSelection(true);
		box2011.setLayoutData(SWTUtils.getCheckBoxData(GridData.CENTER));
		box2011.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				SWTGui.instance.filterChanged();
			}
		});

		box2012 = new Button(checkBoxWrapper, SWT.CHECK);
		box2012.setText("2012");
		box2012.setSelection(true);
		box2012.setLayoutData(SWTUtils.getCheckBoxData(GridData.CENTER));
		box2012.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				SWTGui.instance.filterChanged();
			}
		});
	}

	public List<Integer> getYears() {
		List<Integer> result = new ArrayList<Integer>();

		if (box2008 != null && box2008.getSelection())
			result.add(2008);
		if (box2009 != null && box2009.getSelection())
			result.add(2009);
		if (box2010 != null && box2010.getSelection())
			result.add(2010);
		if (box2011 != null && box2011.getSelection())
			result.add(2011);
		if (box2012 != null && box2012.getSelection())
			result.add(2012);
		return result;
	}
}
