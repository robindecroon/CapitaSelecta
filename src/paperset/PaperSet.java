package paperset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import keywordmap.Drawable;
import wordcloud.WordCloudDrawable;
import wordcloud.WordCloudManager;
import data.Paper;
import data.PaperWordData;

public class PaperSet extends Drawable {
	private List<Paper> papers = new ArrayList<Paper>();
	private List<PaperDrawable> paperDrawables = new ArrayList<PaperDrawable>();
	private float alpha = 0.f;
	private boolean activate = true;
	private WordCloudManager manager;

	/**
	 * 
	 * @param papers
	 */
	public PaperSet(WordCloudManager manager, WordCloudDrawable drawable,
			String word, PaperWordData data) {
		super(manager.getVisualization());

		this.manager = manager;

		List<Paper> all = new ArrayList<Paper>(data.getPapers(word));
//		while (all.size() > 16)
//			all.remove(RNG.nextInt(all.size()));

		long seed = 0;
		for (Paper paper : all) {
			if (!papers.contains(paper)) {
				papers.add(paper);
				seed += paper.hashCode();
			}
		}

		Random random = new Random(seed);

		int batchSize = Math.min(6, papers.size());
		int index = 0;
		int circle = 0;

		while (index < papers.size()) {
			float angle = (float) (2.f * Math.PI / (float) batchSize);
			float offset = random.nextFloat() * 2.f * (float) Math.PI;

			for (int i = index; i < index + batchSize; i++) {
				Paper p = papers.get(i);
				PaperDrawable d = new PaperDrawable(getVisualization(), p,
						drawable, angle, offset, i, circle);
				paperDrawables.add(d);
			}

			index += batchSize;
			circle++;
			batchSize = Math.min(papers.size() - index,
					(int) Math.ceil(batchSize * 1.5));
		}
	}

	public void update() {
		if (activate)
			alpha = Math.min(1.f, alpha + 0.1f);
		else
			alpha = Math.max(0.f, alpha - 0.1f);
	}

	public void draw(float layeralpha) {
		float scale = getVisualization().getDrawScale();

		for (int i = paperDrawables.size() - 1; i >= 0; i--) {
			Paper paper = paperDrawables.get(i).getPaper();
			if (manager.getFilter().allowed(paper))
				paperDrawables.get(i).draw(scale, alpha * layeralpha);
		}

		for (int i = paperDrawables.size() - 1; i >= 0; i--) {
			Paper paper = paperDrawables.get(i).getPaper();
			if (manager.getFilter().allowed(paper))
				paperDrawables.get(i).drawName(scale, alpha * layeralpha);
		}
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
