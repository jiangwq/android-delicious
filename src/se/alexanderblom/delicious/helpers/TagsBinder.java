package se.alexanderblom.delicious.helpers;

import java.util.List;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class TagsBinder {
	private static final float SATURATION = 0.7f;
	private static final float LIGHTNESS = 0.8f;
	
	// Hard space
	private static final char SEPARATOR = '\u00a0';
	
	// This gives nice looking colors and is consistent
	private static final int HASH_SEED = 1337;
	private static final HashFunction HASH_FUNCTION = Hashing.murmur3_32(HASH_SEED);

	public static Spannable buildTagList(List<String> tags) {
		SpannableStringBuilder builder = new SpannableStringBuilder();

		if (tags == null || tags.isEmpty()) {
			return builder;
		}
		
		for (String tag : tags) {
			int pos = builder.length();
			
			builder.append(tag);
			
			int color = generateColor(tag);
			builder.setSpan(new ForegroundColorSpan(color), pos, builder.length(), 0);
			
			builder.append(SEPARATOR);
			// Thin space, so we get 1.5 spaces between
			builder.append('\u2009');
		}
		
		// Remove last thin and hard space
		builder.delete(builder.length() - 2, builder.length());
		
		// Make the whole thing bold
		builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), 0);
		
		return builder;
	}

	public static int generateColor(String text) {
		float hue = floatFromText(text, 0f, 1f);
		return HSBtoColor(hue, SATURATION, LIGHTNESS);
	}

	private static float floatFromText(String text, float low, float high) {
		int hash = HASH_FUNCTION.hashString(text, Charsets.UTF_8).asInt();
		
		final float old_min = Integer.MIN_VALUE;
		final float old_max = Integer.MAX_VALUE;
		float hue = ((hash - old_min) / (old_max - old_min)) * (high - low);
		
		// Return limited in range (low, high)
		return Math.max(low, Math.min(hue, high));
	}

	/**
	 * Adapted from Color.java in Android
	 * 
	 * See https://github.com/android/platform_frameworks_base/blob/master/graphics/java/android/graphics/Color.java
	 */
	private static int HSBtoColor(float h, float s, float b) {
		//h = MathUtils.constrain(h, 0.0f, 1.0f);
		//s = MathUtils.constrain(s, 0.0f, 1.0f);
		//b = MathUtils.constrain(b, 0.0f, 1.0f);

		float red = 0.0f;
		float green = 0.0f;
		float blue = 0.0f;

		final float hf = (h - (int) h) * 6.0f;
		final int ihf = (int) hf;
		final float f = hf - ihf;
		final float pv = b * (1.0f - s);
		final float qv = b * (1.0f - s * f);
		final float tv = b * (1.0f - s * (1.0f - f));

		switch (ihf) {
			case 0: // Red is the dominant color
				red = b;
				green = tv;
				blue = pv;
				break;
			case 1: // Green is the dominant color
				red = qv;
				green = b;
				blue = pv;
				break;
			case 2:
				red = pv;
				green = b;
				blue = tv;
				break;
			case 3: // Blue is the dominant color
				red = pv;
				green = qv;
				blue = b;
				break;
			case 4:
				red = tv;
				green = pv;
				blue = b;
				break;
			case 5: // Red is the dominant color
				red = b;
				green = pv;
				blue = qv;
				break;
		}

		return 0xFF000000 | (((int) (red * 255.0f)) << 16)
				| (((int) (green * 255.0f)) << 8) | ((int) (blue * 255.0f));
	}
}
