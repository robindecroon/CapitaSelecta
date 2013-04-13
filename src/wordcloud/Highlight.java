package wordcloud;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Highlight {
	private boolean changed = false;
	private HighlightWordData currentHighLight = null;
	private float scale = 1.f;
	private final HashMap<String, HighlightWordData> words = new HashMap<String, Highlight.HighlightWordData>();

	public Highlight() {
	}

	public void setChanged(boolean canChange) {
		this.changed = canChange;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setHighlightedWord(String word) {
		if (isChanged())
			return;
		if (word == null && currentHighLight == null)
			return;
		if (word != null && word.equals(currentHighLight))
			return;

		if (currentHighLight != null)
			words.put(currentHighLight.word, currentHighLight);

		if (word != null) {
			currentHighLight = new HighlightWordData(word, getScale(word),
					getAlpha(word));
			words.remove(word);
		} else
			currentHighLight = null;
		setChanged(true);
	}

	public void update() {
		if (currentHighLight == null) {
			for (HighlightWordData data : words.values())
				data.update(1.f, -1.f);
			scale = update(scale, 0.04f, 0.2f, 1.f);
		} else {
			currentHighLight.update(1.f, 1.f);

			for (HighlightWordData data : words.values())
				data.update(-1.f, -1.f);
			scale = update(scale, -0.04f, 0.2f, 1.f);
		}

		Iterator<Entry<String, HighlightWordData>> it = words.entrySet()
				.iterator();
		while (it.hasNext()) {
			HighlightWordData d = it.next().getValue();
			if (d.getScale() == 1.f
					&& (d.getAlpha() == scale || d.getAlpha() == 1.f))
				it.remove();
		}
	}

	public float getAlpha(String word) {
		if (currentHighLight != null && currentHighLight.word.equals(word))
			return currentHighLight.getAlpha();
		else if (words.containsKey(word))
			return words.get(word).getAlpha();
		else
			return scale;
	}

	public float getScale(String word) {
		if (currentHighLight != null && currentHighLight.word.equals(word))
			return currentHighLight.getScale();
		if (words.containsKey(word))
			return words.get(word).getScale();
		return 1.f;
	}

	public String getHighlightedWord() {
		return currentHighLight == null ? null : currentHighLight.word;
	}

	public static float update(float value, float scale, float minimum,
			float maximum) {
		if (scale < 0.f)
			return Math.max(minimum, value + scale);
		else
			return Math.min(maximum, value + scale);

	}

	private class HighlightWordData {
		public final String word;
		private float _scale;
		private float _alpha;

		public HighlightWordData(String word, float startScale, float startAlpha) {
			if (word == null)
				throw new NullPointerException("The given word is null!");
			this.word = word;
			this._alpha = startAlpha;
			this._scale = startScale;
		}

		public void update(float alphascale, float scalescale) {
			this._alpha = Highlight.update(this._alpha, alphascale * 0.08f,
					0.2f, 1.f);
			this._scale = Highlight.update(this._scale, scalescale * 0.2f, 1.f,
					4.f);
		}

		public float getAlpha() {
			return _alpha;
		}

		public float getScale() {
			return _scale;
		}
	}
}
