package keywordmap;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;

public class BoundedUnfoldingMap extends UnfoldingMap {
	private long lastPan = 0;

	public BoundedUnfoldingMap(PApplet arg0, AbstractMapProvider arg1) {
		super(arg0, arg1);
	}

	public void updateMap() {
		super.updateMap();

		Location tl = new Location(85, -180);
		Location br = new Location(-85, 180);

		Location stl = getTopLeftBorder();
		Location sbr = getBottomRightBorder();
		Location c = getCenter();

		float leftXOffset = Math.abs(c.getLat() - stl.getLat());
		float rightXOffset = Math.abs(c.getLat() - sbr.getLat());
		float topYOffset = Math.abs(c.getLon() - stl.getLon());
		float bottomYOffset = Math.abs(c.getLon() - sbr.getLon());

		boolean xInside = false;
		boolean yInside = false;
		float newX = c.getLat();
		float newY = c.getLon();

		if (stl.getLat() > tl.getLat())
			newX = 85 - leftXOffset - 2.f;
		else if (sbr.getLat() < br.getLat())
			newX = -85 + rightXOffset + 2.f;
		else
			xInside = true;

		if (stl.getLon() < tl.getLon())
			newY = -180 + topYOffset;
		else if (sbr.getLon() > br.getLon())
			newY = 180 - bottomYOffset;
		else
			yInside = true;

		Location newL = new Location(newX, newY);

		if (!xInside || !yInside) {
			if (System.currentTimeMillis() - lastPan > 200) {
				lastPan = System.currentTimeMillis();
				panTo(newL);
			}
		}
	}
}
