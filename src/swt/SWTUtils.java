package swt;

import org.eclipse.swt.layout.GridData;

public class SWTUtils {
	public static GridData getCheckBoxData(int alignment) {
		GridData result = new GridData();
		result.horizontalAlignment = alignment;
		result.verticalAlignment = GridData.CENTER;
		result.grabExcessHorizontalSpace = true;
		result.grabExcessVerticalSpace = true;
		return result;
	}
	
	public static GridData getCheckBoxData() {
		return getCheckBoxData(GridData.FILL);
	}
}
