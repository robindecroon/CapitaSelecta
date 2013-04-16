package swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import util.Logger;

public class SearchTool extends Composite {

	public SearchTool(Composite arg0, int arg1) {
		super(arg0, arg1);

		setLayout(new GridLayout(1, true));

		final Text text = new Text(this, SWT.SEARCH | SWT.ICON_SEARCH
				| SWT.ICON_CANCEL);
		// text.setText("search...");
		text.setMessage("search...");
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.CENTER;
		data.verticalAlignment = GridData.CENTER;
		data.widthHint = 160;
		text.setLayoutData(data);

		text.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				Logger.Info("Entered \"" + text.getText() + "\" as search text");

				if (SWTGui.applet.getMap() != null)
					SWTGui.applet.getMap().getWordCloudManager()
							.setHighlightedWord(text.getText());

			}
		});
	}
}
