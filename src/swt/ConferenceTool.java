package swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import data.Conference;

public class ConferenceTool extends Composite {
	private Button lak;
	private Button edm;

	public ConferenceTool(Composite arg0, int arg1) {
		super(arg0, arg1);

		setLayout(new GridLayout(2, true));

		lak = new Button(this, SWT.CHECK);
		lak.setSelection(true);
		lak.setText("LAK");
		lak.setLayoutData(SWTUtils.getCheckBoxData());
		lak.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				SWTGui.instance.filterChanged();
			}
		});

		edm = new Button(this, SWT.CHECK);
		edm.setText("EDM");
		edm.setSelection(true);
		edm.setLayoutData(SWTUtils.getCheckBoxData());
		edm.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				SWTGui.instance.filterChanged();
			}
		});
	}

	public List<Conference> getConferenceList() {
		List<Conference> result = new ArrayList<Conference>();
		if (lak != null && lak.getSelection())
			result.add(Conference.LAK);
		if (edm != null && edm.getSelection())
			result.add(Conference.EDM);
		return result;
	}

}
