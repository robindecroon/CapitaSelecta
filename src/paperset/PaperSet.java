package paperset;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import util.RNG;
import wordcloud.WordCloudDrawable;
import data.Paper;
import data.PaperWordData;
import de.fhpotsdam.unfolding.UnfoldingMap;

public class PaperSet {
	private List<Paper> papers = new ArrayList<Paper>();
	private List<PaperDrawable> paperDrawables = new ArrayList<PaperDrawable>();

	private float alpha = 0.f;
	private boolean activate = true;

	/**
	 * 
	 * @param papers
	 */
	public PaperSet(PApplet applet, UnfoldingMap map, WordCloudDrawable drawable,
			String word, PaperWordData data) {
		papers.addAll(data.getPapers(word));
		
		while(papers.size() > 16)
			papers.remove(RNG.nextInt(papers.size()));
		
		int currentBatch = Math.min(6, papers.size());
		int currentIndex = 0;
		float angle = (float) (2.0 * Math.PI / currentBatch);
		int circle = 0;
		while (currentIndex < papers.size()) {
			for (int i = 0; i < papers.size() - currentIndex; i++) {
				Paper p = papers.get(i + currentIndex);
				PaperDrawable draw = new PaperDrawable(applet, map, p,
						drawable, angle, i, circle);
				paperDrawables.add(draw);
			}

			currentIndex += currentBatch;
			currentBatch = (int) Math.floor(currentBatch * 1.5);
			circle++;

			int divisor = Math.min(currentBatch, papers.size() - currentIndex);
			angle = (float) (2.0 * Math.PI / divisor);
		}		
	}

	public void update() {
		if (activate)
			alpha = Math.min(1.f, alpha + 0.1f);
		else
			alpha = Math.max(0.f, alpha - 0.1f);
	}
	
	public void draw(float scale, float layeralpha) {
		for(int i=paperDrawables.size()-1;i>=0;i--)
			paperDrawables.get(i).draw(scale,alpha*layeralpha);
	}

	public void activate() {
		activate = true;

	}

	public void deactivate() {
		activate = false;
	}

	public boolean canBeDeleted() {
		return !activate && alpha == 0.f;
	}
}
