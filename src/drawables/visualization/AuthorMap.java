package drawables.visualization;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import processing.core.PApplet;
import processing.core.PImage;
import acceleration.Acceleration;
import acceleration.MultiThreadPruning;
import data.Author;
import data.Database;
import data.Paper;
import data.University;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MarkerManager;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import drawables.BoundingBox;

public class AuthorMap {
	private PApplet applet;
	private PImage author;
	private PImage authorHighLight;
	private PImage paper;
	private PImage paperHighLight;
	private PImage paperRed;
	private PImage paperGreen;
	private UnfoldingMap map;
	private Acceleration<AuthorDrawable> authorAccel;
	private Acceleration<PaperDrawable> paperAccel;
	private Acceleration<ConnectionDrawable> connectionAccel;

	private Map<Author, AuthorDrawable> authorDrawablesMap = new HashMap<Author, AuthorDrawable>();
	private Map<Paper, PaperDrawable> paperDrawablesMap = new HashMap<Paper, PaperDrawable>();
	private HashSet<ConnectionDrawable> connections = new HashSet<ConnectionDrawable>();
	private HashSet<AuthorDrawable> authorDrawables = new HashSet<AuthorDrawable>();
	private HashSet<PaperDrawable> paperDrawables = new HashSet<PaperDrawable>();

	private float previousZoom = Float.POSITIVE_INFINITY;
	private Location previousTopLeftLocation;
	private Location previousBottomRightLocation;

	private List<AuthorDrawable> visibleAuthors;
	private List<PaperDrawable> visiblePapers;
	private List<ConnectionDrawable> visibleConnections;

	private AuthorDrawable highAuthor;
	private PaperDrawable highPaper;

	private MarkerManager<Marker> manager;
	private List<Marker> countryMarkers;

	private float lastMouseX = 0;
	private float lastMouseY = 0;
	private long timeout = 5000;
	private long timeSinceLastMovement = System.currentTimeMillis() - timeout;

	/**
	 * 
	 * @param applet
	 */
	public AuthorMap(PApplet applet) {
		this.applet = applet;
		applet.smooth();
		author = applet.loadImage("data/image/author.png");
		authorHighLight = applet.loadImage("data/image/authorHighLight.png");
		paper = applet.loadImage("data/image/paper.png");
		paperHighLight = applet.loadImage("data/image/paperHighLight.png");
		paperRed = applet.loadImage("data/image/paper_red.png");
		paperGreen = applet.loadImage("data/image/paper_green.png");

		map = new UnfoldingMap(applet);
		MapUtils.createDefaultEventDispatcher(applet, map);
		map.setTweening(true);
		map.setZoomRange(2.f, 256.f);
		map.zoomAndPanTo(new Location(50.85, 4.35), 8);

		lastMouseX = applet.mouseX;
		lastMouseY = applet.mouseY;

		List<Feature> countries = GeoJSONReader.loadData(getApplet(),
				"countries.geo.json");
		manager = new MarkerManager<Marker>();
		manager.setMap(map);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		for (Marker marker : countryMarkers) {
			marker.setColor(applet.color(240, 240, 240));
			marker.setStrokeWeight(3);
			marker.setStrokeColor(applet.color(0, 0, 0));
			manager.addMarker(marker);
		}

		initializeAuthors();
		initializePapers();
		initializeConnection();
	}
	
	public float getZoom() {
		return map.getZoom();
	}

	public PApplet getApplet() {
		return applet;
	}

	private void initializeAuthors() {
		Database d = Database.getInstance();

		for (Entry<University, List<Author>> e : d.getAffiliationAuthorMap()
				.entrySet()) {
			List<Author> authors = e.getValue();

			int currentBatch = Math.min(6, authors.size());
			int currentIndex = 0;
			float angle = (float) (2.0 * Math.PI / currentBatch);
			int circle = 0;
			while (currentIndex < authors.size()) {
				for (int i = 0; i < authors.size() - currentIndex; i++) {
					Author a = authors.get(i + currentIndex);
					AuthorDrawable draw = new AuthorDrawable(getApplet(), a,
							map, author, authorHighLight, e.getKey()
									.getLocation(), angle, i, circle,
							map.getZoom());
					authorDrawables.add(draw);
					authorDrawablesMap.put(a, draw);
				}

				currentIndex += currentBatch;
				currentBatch = (int) Math.floor(currentBatch * 1.5);
				circle++;

				int divisor = Math.min(currentBatch, authors.size()
						- currentIndex);
				angle = (float) (2.0 * Math.PI / divisor);
			}
		}
	}

	private void initializePapers() {
		Database d = Database.getInstance();

		for (Paper paper : d.getPapers()) {
			PaperDrawable drawable = new PaperDrawable(getApplet(), paper, map,
					this.paper, this.paperHighLight, paperRed, paperGreen,
					map.getZoom());
			paperDrawables.add(drawable);
			paperDrawablesMap.put(paper, drawable);
		}
	}

	private void initializeConnection() {
		Database d = Database.getInstance();
		for (Paper paper : d.getPapers()) {
			for (Author author : paper.getAuthors()) {
				PaperDrawable pp = paperDrawablesMap.get(paper);
				AuthorDrawable aa = authorDrawablesMap.get(author);

				if (aa != null && pp != null)
					connections.add(new ConnectionDrawable(getApplet(), map,
							aa, pp));
			}
		}
		for (Author author : d.getAuthors()) {
			for (Paper paper : author.getPapers()) {
				PaperDrawable pp = paperDrawablesMap.get(paper);
				AuthorDrawable aa = authorDrawablesMap.get(author);

				if (aa != null && pp != null)
					connections.add(new ConnectionDrawable(getApplet(), map,
							aa, pp));
			}
		}
	}

	public void update(float scale) {
		boolean zoomed = false;
		boolean moved = false;
		boolean animated = true;

		PApplet a = getApplet();

		if (a.mouseX != lastMouseX || a.mouseY != lastMouseY) {
			animated = false;
			lastMouseX = a.mouseX;
			lastMouseY = a.mouseY;
			timeSinceLastMovement = System.currentTimeMillis();
		} else if (System.currentTimeMillis() < timeSinceLastMovement + timeout)
			animated = false;
		else if (highAuthor != null && highAuthor != null)
			animated = false;

		if (Math.abs(map.getZoom() - previousZoom) > 0.001) {
			zoomed = true;
			previousZoom = map.getZoom();
		}
		if (!map.getBottomRightBorder().equals(previousBottomRightLocation)
				|| !map.getTopLeftBorder().equals(previousTopLeftLocation)) {
			moved = true;
			previousBottomRightLocation = map.getBottomRightBorder();
			previousTopLeftLocation = map.getTopLeftBorder();
		}

		for (AuthorDrawable d : authorDrawables) {
			d.setZoom(map.getZoom());
			if (getApplet().mousePressed == false && animated) {
				d.update(scale, moved, zoomed);
			} else
				d.update(0, moved, zoomed);
		}

		for (PaperDrawable d : paperDrawables) {
			d.setZoom(map.getZoom());
			if (getApplet().mousePressed == false && animated) {
				d.update(scale, moved, zoomed);
			} else
				d.update(0, moved, zoomed);
		}
		for (ConnectionDrawable d : connections)
			d.update(0, moved, zoomed);

		// authorAccel = new BVH<AuthorDrawable>(authorDrawables,0);
		// paperAccel = new BVH<PaperDrawable>(paperDrawables,0);
		// connectionAccel = new BVH<ConnectionDrawable>(
		// connections,0);

		authorAccel = new MultiThreadPruning<AuthorDrawable>(authorDrawables);
		paperAccel = new MultiThreadPruning<PaperDrawable>(paperDrawables);
		connectionAccel = new MultiThreadPruning<ConnectionDrawable>(
				connections);

		BoundingBox view = getScreenBox();
		visibleAuthors = authorAccel.getElements(view);
		visiblePapers = paperAccel.getElements(view);
		visibleConnections = connectionAccel.getElements(view);

		updateHighlight();
	}

	private void updateHighlight() {
		PApplet a = getApplet();
		Collections.sort(visibleAuthors);
		Collections.sort(visiblePapers);

		if (highAuthor != null
				&& highAuthor.getScreenBox().mouseIn(a.mouseX, a.mouseY))
			return;
		else if (highAuthor != null) {
			highAuthor.setHighLight(false);
			for (Paper paper : highAuthor.getAuthor().getPapers())
				paperDrawablesMap.get(paper).setHighLight(false);
			highAuthor = null;
		}

		if (highPaper != null
				&& highPaper.getScreenBox().mouseIn(a.mouseX, a.mouseY))
			return;
		else if (highPaper != null) {
			highPaper.setHighLight(false);
			for (Author author : highPaper.getPaper().getAuthors())
				authorDrawablesMap.get(author).setHighLight(false);
			highPaper = null;
		}

		for (int i = 0; i < visibleAuthors.size(); i++)
			if (visibleAuthors.get(i).getScreenBox()
					.mouseIn(a.mouseX, a.mouseY)) {
				highAuthor = visibleAuthors.get(i);
				highAuthor.setHighLight(true);

				for (Paper paper : highAuthor.getAuthor().getPapers()) {
					PaperDrawable pp = paperDrawablesMap.get(paper);
					pp.setHighLight(true);
				}
				highPaper = null;
				return;
			}
		for (int i = 0; i < visiblePapers.size(); i++)
			if (visiblePapers.get(i).getScreenBox().mouseIn(a.mouseX, a.mouseY)) {
				highPaper = visiblePapers.get(i);
				highPaper.setHighLight(true);
				for (Author author : highPaper.getPaper().getAuthors()) {
					AuthorDrawable aa = authorDrawablesMap.get(author);
					aa.setHighLight(true);
				}
				highAuthor = null;
				return;
			}

	}

	public void draw() {
		PApplet a = getApplet();
		a.background(135, 206, 250);

		map.draw();
		// manager.draw();
		// a.stroke(0, 0, 0);
		// a.fill(230);
		// a.strokeWeight(3);
		// for (Marker marker : countryMarkers) {
		// try {
		// SimplePolygonMarker shape = (SimplePolygonMarker) marker;
		//
		// List<Location> locations = shape.getLocations();
		//
		// for (int i = 0; i < locations.size(); i++) {
		// ScreenPosition p1 = map.getScreenPosition(locations.get(i));
		// ScreenPosition p2 = map.getScreenPosition(locations
		// .get((i + 1) % locations.size()));
		// a.line(p1.x, p1.y, p2.x, p2.y);
		//
		// }
		// } catch (ClassCastException e) {
		//
		// }
		// }
		Collections.sort(visibleAuthors);
		Collections.sort(visiblePapers);
		/**
		 * Draw all the connections
		 */
		for (ConnectionDrawable dd : visibleConnections) {
			AuthorDrawable aa = dd.getAuthorDrawable();
			PaperDrawable pp = dd.getPaperDrawable();
			dd.setHighlight(aa.getHighLight() && pp.getHighLight());
			dd.draw();
		}
		for (PaperDrawable pp : visiblePapers) {
			if (pp.getHighLight())
				continue;
			pp.draw();
		}
		for (AuthorDrawable aa : visibleAuthors) {
			if (aa.getHighLight())
				continue;
			aa.draw();
		}

		if (highAuthor != null) {
			BoundingBox bb;
			for (Paper paper : highAuthor.getAuthor().getPapers()) {
				PaperDrawable pp = paperDrawablesMap.get(paper);
				pp.draw();
				bb = pp.getScreenBox();
				a.fill(0);
				a.text(pp.getPaper().getName(), bb.x + bb.width, bb.y);

			}
			highAuthor.draw();
			bb = highAuthor.getScreenBox();
			a.fill(0);
			a.text(highAuthor.getAuthor().getFullName(), bb.x + bb.width, bb.y);
		}
		if (highPaper != null) {
			BoundingBox bb = highPaper.getScreenBox();
			a.fill(0);
			a.text(highPaper.getPaper().getName(), bb.x + bb.width, bb.y);
			highPaper.draw();
			for (Author author : highPaper.getPaper().getAuthors()) {
				AuthorDrawable aa = authorDrawablesMap.get(author);
				aa.draw();
				bb = aa.getScreenBox();
				a.fill(0);
				a.text(aa.getAuthor().getFullName(), bb.x + bb.width, bb.y);

			}
		}
	}

	public BoundingBox getScreenBox() {
		ScreenPosition top = map.getScreenPosition(map.getTopLeftBorder());
		ScreenPosition bottom = map.getScreenPosition(map
				.getBottomRightBorder());
		return new BoundingBox(Math.min(top.x, bottom.x), Math.min(top.y,
				bottom.y), Math.abs(bottom.x - top.x), Math.abs(bottom.y
				- top.y));
	}

	public BoundingBox getLocationBox() {
		Location top = map.getTopLeftBorder();
		Location bottom = map.getBottomRightBorder();
		return new BoundingBox(Math.min(top.x, bottom.x), Math.min(top.y,
				bottom.y), Math.abs(bottom.x - top.x), Math.abs(bottom.y
				- top.y));
	}
}
