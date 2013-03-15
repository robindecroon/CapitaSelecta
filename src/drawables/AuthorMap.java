package drawables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import processing.core.PApplet;
import processing.core.PImage;
import data.Author;
import data.Database;
import data.Paper;
import data.University;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;

public class AuthorMap extends Drawable {

	private PImage author;
	private PImage authorHighLight;
	private PImage paper;
	private PImage paperHighLight;
	private PImage paperRed;
	private PImage paperGreen;
	private UnfoldingMap map;

	private Map<Author, AuthorDrawable> authorDrawables = new HashMap<Author, AuthorDrawable>();
	private Map<Paper, PaperDrawable> paperDrawables = new HashMap<Paper, PaperDrawable>();

	/**
	 * 
	 * @param applet
	 */
	public AuthorMap(PApplet applet) {
		super(applet);

		applet.smooth();

		author = applet.loadImage("data/image/author.png");
		authorHighLight = applet.loadImage("data/image/authorHighLight.png");
		paper = applet.loadImage("data/image/paper.png");
		paperHighLight = applet.loadImage("data/image/paperHighLight.png");
		paperRed = applet.loadImage("data/image/paper_red.png");
		paperGreen = applet.loadImage("data/image/paper_green.png");

		map = new UnfoldingMap(applet);
		MapUtils.createDefaultEventDispatcher(applet, map);

		initializeAuthors();
		initializePapers();
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
					authorDrawables.put(a, draw);
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
			paperDrawables.put(paper, drawable);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#update(float)
	 */
	@Override
	public void update(float scale) {
		for (AuthorDrawable d : authorDrawables.values()) {
			if (getApplet().mousePressed == false) {
				d.setZoom(map.getZoom());
				d.update(scale);
			} else
				d.update(0);
		}

		for (PaperDrawable d : paperDrawables.values()) {
			if (getApplet().mousePressed == false) {
				d.setZoom(map.getZoom());
				d.update(scale);
			} else
				d.update(0);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see drawables.Drawable#draw()
	 */
	@Override
	public void draw() {
		PApplet a = getApplet();

		map.draw();

		a.fill(0, 0, 0);

		HashSet<AuthorDrawable> highAuthors = new HashSet<AuthorDrawable>();
		HashSet<PaperDrawable> highPapers = new HashSet<PaperDrawable>();

		a.stroke(0, 0, 0, 150);
		a.strokeWeight(3);
		for (AuthorDrawable drawable : authorDrawables.values()) {
			BoundingBox bbox = drawable.getBoundingBox();

			if (bbox.mouseIn(a.mouseX, a.mouseY)) {
				/*
				 * Draw the lines
				 */
				Author author = drawable.getAuthor();
				List<Paper> papers = author.getPapers();
				ScreenPosition from = drawable.getScreenPosition();
				for (Paper paper : papers) {
					PaperDrawable pDraw = paperDrawables.get(paper);

					if (highPapers.contains(pDraw))
						continue;
					highPapers.add(pDraw);

					BoundingBox bb = pDraw.getBoundingBox();
					a.text(paper.getName(), bb.x + bb.width, bb.y);

					ScreenPosition to = pDraw.getScreenPosition();
					a.line(from.x, from.y, to.x, to.y);

					pDraw.setHighLight(true);
					pDraw.draw();
					pDraw.setHighLight(false);
				}

				/*
				 * Draw the author
				 */
				a.text(author.getFullName(), bbox.x + bbox.width, bbox.y);
				drawable.setHighLight(true);
				drawable.draw();
				drawable.setHighLight(false);
				highAuthors.add(drawable);
			}
		}

		for (PaperDrawable drawable : paperDrawables.values()) {
			BoundingBox bbox = drawable.getBoundingBox();

			if (bbox.mouseIn(a.mouseX, a.mouseY)) {
				/*
				 * Draw the lines
				 */
				Paper paper = drawable.getPaper();
				List<Author> authors = paper.getAuthors();
				ScreenPosition from = drawable.getScreenPosition();
				for (Author author : authors) {
					AuthorDrawable aDraw = authorDrawables.get(author);
					if (highAuthors.contains(aDraw))
						continue;
					highAuthors.add(aDraw);

					BoundingBox bb = aDraw.getBoundingBox();
					a.text(author.getFullName(), bb.x + bb.width, bb.y);

					ScreenPosition to = aDraw.getScreenPosition();
					a.line(from.x, from.y, to.x, to.y);

					aDraw.setHighLight(true);
					aDraw.draw();
					aDraw.setHighLight(false);
				}

				/*
				 * Draw the paper
				 */
				a.text(paper.getName(), bbox.x + bbox.width, bbox.y);
				drawable.setHighLight(true);
				drawable.draw();
				drawable.setHighLight(false);
				highPapers.add(drawable);
			}
		}
		// for (AuthorDrawable drawable : authorDrawables.values()) {
		// BoundingBox bbox = drawable.getBoundingBox();
		// if (bbox.mouseIn(a.mouseX, a.mouseY)) {
		// /*
		// * Draw the lines
		// */
		// Author author = drawable.getAuthor();
		// List<Paper> papers = author.getPapers();
		// for (Paper paper : papers) {
		// PaperDrawable pDraw = paperDrawables.get(paper);
		// if (highPapers.contains(pDraw))
		// continue;
		// highPapers.add(pDraw);
		// }
		// highAuthors.add(drawable);
		// }
		// }
		//
		// for (PaperDrawable drawable : paperDrawables.values()) {
		// BoundingBox bbox = drawable.getBoundingBox();
		// if (bbox.mouseIn(a.mouseX, a.mouseY)) {
		// /*
		// * Draw the lines
		// */
		// Paper paper = drawable.getPaper();
		// List<Author> authors = paper.getAuthors();
		// for (Author author : authors) {
		// AuthorDrawable aDraw = authorDrawables.get(author);
		// if (highAuthors.contains(aDraw))
		// continue;
		// highAuthors.add(aDraw);
		// }
		// highPapers.add(drawable);
		// }
		// }

		a.stroke(0, 0, 0, 50);
		a.strokeWeight(1);

		for (AuthorDrawable drawable : authorDrawables.values()) {
			ScreenPosition from = drawable.getScreenPosition();
			for (Paper paper : drawable.getAuthor().getPapers()) {
				PaperDrawable pDraw = paperDrawables.get(paper);
				if (!highPapers.contains(pDraw))
					continue;
				ScreenPosition to = pDraw.getScreenPosition();
				a.line(from.x, from.y, to.x, to.y);
			}

			if (!highAuthors.contains(drawable))
				drawable.draw();
		}
		for (PaperDrawable drawable : paperDrawables.values()) {
			ScreenPosition from = drawable.getScreenPosition();
			for (Author author : drawable.getPaper().getAuthors()) {
				AuthorDrawable aDraw = authorDrawables.get(author);
				if (highAuthors.contains(aDraw))
					continue;
				ScreenPosition to = aDraw.getScreenPosition();
				a.line(from.x, from.y, to.x, to.y);
			}

			if (!highPapers.contains(drawable))
				drawable.draw();
		}
	}
}
