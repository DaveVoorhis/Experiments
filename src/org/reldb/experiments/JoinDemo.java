package org.reldb.experiments;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class JoinDemo {

	/**
	 *  The following methods are used to create random data for performance tests. 
	 */
	
	private static String getRandomItem(String[] items) {
		return items[(int)(Math.random() * (double)(items.length))];		
	}
	
	private static String getRandomColor() {
		final String[] items = {"Red", "Green", "Blue"};
		return getRandomItem(items);
	}

	private static String getRandomPart() {
		final String[] items = {"Nut", "Bolt", "Screw", "Cam", "Cog"};
		return getRandomItem(items);
	}

	private static String getRandomCity() {
		final String[] items = {"London", "Paris", "Oslo", "Athens"};
		return getRandomItem(items);
	}

	/** A generic Pair of references. */
	static record Pair<L, R>(L left, R right) {};

	/**
	 * Specify the matching expression for generic JOIN.
	 *
	 * @param <L> - the left operand's type
	 * @param <R> - the right operand's type
	 */
	public static interface Matcher<L, R> {
		public boolean isMatchingIn(L l, R r);
	}
	
	/**
	 * Perform a generic JOIN of a collection l to a collection r using a lambda evaluating to 'true' for every tuple match.
	 * 
	 * The algorithm here is sub-optimal.
	 * 
	 * @param <L> - the left collection's item type
	 * @param <R> - the right collection's item type
	 * @param l - the left collection
	 * @param r - the right collection
	 * @param matcher a Matcher that specifies the lambda expression that 
	 *       returns true for each item in the left operand that should match an item in the right operand.
	 * @return a Stream of joined L-to-R PairS.
	 */
	public static <L, R> Stream<Pair<L, R>> join(Collection<L> l, Collection<R> r, Matcher<L, R> matcher) {
		return l
				.stream()
				.flatMap(lTuple -> r
						.stream()
						.filter(rTuple -> matcher.isMatchingIn(lTuple, rTuple))
						.map(rTuple -> new Pair<>(lTuple, rTuple)));		
	}
	
	/** Define Parts heading. */
	static record PTuple(String pNum, String pName, String color, BigDecimal weight, String city) {};
	
	/** Define Suppliers heading. */
	static record STuple(String sNum, String sName, int status, String city) {};
	
	/**
	 * Perform a hard-wired join of collection of STupleS to a collection of PTupleS by the city attribute of both. 
	 *  
	 * The algorithm here is sub-optimal.
	 * 
	 * @param s - Collection of STupleS.
	 * @param p - Collection of PTupleS.
	 * @return a Stream of joined STuple-to-PTuple PairS.
	 */
	public static Stream<Pair<STuple, PTuple>> joinSCityPCity(Collection<STuple> s, Collection<PTuple> p) {
		return s
				.stream()
				.flatMap(sTuple -> p
						.stream()
						.filter(pTuple -> pTuple.city.equals(sTuple.city))
						.map(pTuple -> new Pair<>(sTuple, pTuple)));
	}
	
	/**
	 * A stopwatch to time a specified operation.
	 * 
	 * @param code - the operation to time
	 * @return - the duration in milliseconds
	 */
	private static long time(Runnable code) {
		var startTime = System.nanoTime();
		code.run();
		var endTime = System.nanoTime();
		return (endTime - startTime) / 1000000;		
	}

	/** 
	 * Run a specified operation a given number of times and obtain the average duration.
	 * 
	 * @param trials - number of trials to run
	 * @param code - the operation to time
	 * @param prompt - text prompt associated with each trial
	 * @return - average duration in milliseconds
	 */
	private static double averageTime(int trials, String prompt, Runnable code) {
		var totalTime = LongStream.range(1, trials + 1)
			.map(trial -> {
				System.out.print("Trial " + trial + ":\t" + prompt);
				var duration = time(code);
				System.out.println(" took " + duration + " milliseconds.");				
				return duration;
			})
			.sum();
		return (double)totalTime / (double)trials;
	}

	public static void main(String[] args) {
		// Create canonical S relation
		var s = new HashSet<STuple>();
		s.add(new STuple("S1", "Smith", 20, "London"));
		s.add(new STuple("S2", "Jones", 10, "Paris"));
		s.add(new STuple("S3", "Blake", 30, "Paris"));
		s.add(new STuple("S4", "Clark", 20, "London"));
		s.add(new STuple("S5", "Adams", 30, "Athens"));
		
		// Create canonical P relation
		var p = new HashSet<PTuple>();
		p.add(new PTuple("P1", "Nut", "Red", new BigDecimal("12.0"), "London"));
		p.add(new PTuple("P2", "Bolt", "Green", new BigDecimal("7.0"), "Paris"));
		p.add(new PTuple("P3", "Screw", "Blue", new BigDecimal("7.0"), "Oslo"));
		p.add(new PTuple("P4", "Screw", "Red", new BigDecimal("4.0"), "London"));
		p.add(new PTuple("P5", "Cam", "Blue", new BigDecimal("2.0"), "Paris"));
		p.add(new PTuple("P6", "Cog", "Red", new BigDecimal("9.0"), "London"));
		
		// Show S.
		System.out.println();
		System.out.println("====== s ======");
		s.forEach(System.out::println);
		
		// Show P.
		System.out.println();
		System.out.println("====== p ======");
		p.forEach(System.out::println);
		
		// Show result of hard-wired S JOIN P.
		System.out.println();
		System.out.println("====== s JOIN p (hard-wired join) ======");
		joinSCityPCity(s, p).forEach(System.out::println);
		
		// Show result of generic S JOIN P.
		System.out.println();
		System.out.println("====== s JOIN p (generic join) ======");
		join(s, p, (sTuple, pTuple) -> sTuple.city.equals(pTuple.city)).forEach(System.out::println);
				
		// Compare time performance of hard-wired JOIN vs generic JOIN.
		System.out.println();
		System.out.println("====== Hard-wired vs Generic JOIN ======");
		var speedTestS = new HashSet<STuple>();
		IntStream.range(0, 10000)
			.forEach(i -> speedTestS.add(new STuple("S" + i, "Name" + i, (int)(Math.random() * 30.0), getRandomCity())));
		var speedTestP = new HashSet<PTuple>();
		IntStream.range(0, 20000)
			.forEach(i -> speedTestP.add(new PTuple("P" + i, getRandomPart(), getRandomColor(), new BigDecimal(Math.random() * 20.0), getRandomCity())));
		final var trials = 10;
		var averageHardwiredTime = averageTime(trials, "hard-wired speedTestS JOIN speedTestP",
				() -> joinSCityPCity(speedTestS, speedTestP).forEach(result -> {}));
		var averageGenericTime = averageTime(trials, "generic speedTestS JOIN speedTestP",
				() -> join(speedTestS, speedTestP, (sTuple, pTuple) -> sTuple.city.equals(pTuple.city)).forEach(result -> {}));
		System.out.println();
		System.out.println("Average hard-wired join time is " + averageHardwiredTime + " milliseconds.");
		System.out.println("Average    generic join time is " + + averageGenericTime + " milliseconds.");
	}
}
