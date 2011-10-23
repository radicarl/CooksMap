package de.radicarlprogramming.minecraft.cooksmap.util;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import de.radicarlprogramming.minecraft.cooksmap.Distance;
import de.radicarlprogramming.minecraft.cooksmap.Landmark;

public class Filter {

	private static final int INDEX_FIELDNAME = 1;
	private static final int INDEX_COMPARATOR = 2;
	private static final int INDEX_VALUE = 4;
	private static final String REGEX = "([a-zA-z]*)(%|!?=|(<|>)=?)([^<>=!].*)";
	private final Pattern pattern = Pattern.compile(Filter.REGEX, Pattern.CASE_INSENSITIVE);

	@SuppressWarnings("rawtypes")
	private Comparable value;
	private Method getter;
	private Method comparator;
	private final Player player;
	private String fieldName;

	public Filter(String arg, Player player) throws NoSuchMethodException, InvalidFilterException {
		this.player = player;
		Matcher matcher = this.pattern.matcher(arg);
		if (matcher.matches()) {
			this.setGetter(matcher);
			this.setComparator(matcher);
			this.setValue(matcher);
		} else {
			throw new InvalidFilterException(arg);
		}
	}

	private void setValue(Matcher matcher) {
		String pattern = matcher.group(Filter.INDEX_VALUE);
		try {
			if (this.filterByDistance()) {
				String[] distance = pattern.split("/");
				this.value = new Distance(distance);
			} else {
				this.value = Integer.valueOf(pattern);
			}
		} catch (NumberFormatException e) {
			this.value = pattern;
		}
	}

	private void setComparator(Matcher matcher) throws NoSuchMethodException {
		String compare = matcher.group(Filter.INDEX_COMPARATOR);
		if ("<=".equals(compare)) {
			this.comparator = Filter.class.getDeclaredMethod("isLessOrEqual", Comparable.class);
		} else if ("<".equals(compare)) {
			this.comparator = Filter.class.getDeclaredMethod("isLess", Comparable.class);
		} else if (">=".equals(compare)) {
			this.comparator = Filter.class.getDeclaredMethod("isGreaterOrEqual", Comparable.class);
		} else if (">".equals(compare)) {
			this.comparator = Filter.class.getDeclaredMethod("isGreater", Comparable.class);
		} else if ("=".equals(compare)) {
			this.comparator = Filter.class.getDeclaredMethod("isEqual", Comparable.class);
		} else if ("!=".equals(compare)) {
			this.comparator = Filter.class.getDeclaredMethod("isNotEqual", Comparable.class);
		} else if ("%".equals(compare)) {
			this.comparator = Filter.class.getDeclaredMethod("match", Comparable.class);
		}
	}

	private void setGetter(Matcher matcher) throws NoSuchMethodException {
		this.fieldName = matcher.group(Filter.INDEX_FIELDNAME).toLowerCase();
		String methodName = "get" + this.fieldName.substring(0, 1).toUpperCase() + this.fieldName.substring(1);
		// TODO: use shortcuts c,n,v,i,d
		if (this.filterByDistance()) {
			this.getter = Landmark.class.getDeclaredMethod(methodName, Player.class);
		} else {
			this.getter = Landmark.class.getDeclaredMethod(methodName);
		}
	}

	public boolean fullfills(Landmark landmark) {
		try {
			Object field;
			if (this.filterByDistance()) {
				field = this.getter.invoke(landmark, this.player);
			} else {
				field = this.getter.invoke(landmark);
			}
			return (Boolean) this.comparator.invoke(this, field);
		} catch (Exception e) {
		}
		return false;
	}

	private boolean filterByDistance() {
		return "distance".equals(this.fieldName);
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private boolean isEqual(Comparable field) {
		return field.compareTo(this.value) == 0;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private boolean isGreater(Comparable field) {
		return field.compareTo(this.value) > 0;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private boolean isGreaterOrEqual(Comparable field) {
		return field.compareTo(this.value) >= 0;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private boolean isLess(Comparable field) {
		return field.compareTo(this.value) < 0;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private boolean isLessOrEqual(Comparable field) {
		return field.compareTo(this.value) <= 0;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private boolean isNotEqual(Comparable field) {
		return field.compareTo(this.value) != 0;
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	private boolean match(Comparable field) {
		if (this.value instanceof String && field instanceof String) {
			return ((String) field).matches(".*(?i)" + (String) this.value + ".*");
		}
		return false;
	}
}
