/**
 * 
 */
package edu.kit.aifb.eorg.checker;

/**
 * This class checks monotonic write consistency for a storage system by
 * consecutively issuing two consecutive writes to the same key. When running
 * the same checker a long period of time (e.g., 24h) later, this checker
 * asserts that the value of the second write is returned. It does so for a
 * number of keys. If (for any key) the result is the value of the first write,
 * then monotonic write consistency is violated
 * 
 * 
 * @author David Bermbach
 * 
 *         created on: 16.03.2012
 */
public abstract class AbstractMonotonicWriteConsistencyChecker {

	/** basically number of tests */
	private static final int numberOfKeys = 1000;

	/** if true: issues the writes. if false: reads the values back */
	private static final boolean isFirstRun = false;

	/** concrete implementation */
	private static AbstractMonotonicWriteConsistencyChecker checker = new S3MonotonicWriteConsistencyChecker();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (isFirstRun)
			issueWrites();
		else
			checkResults();

	}

	private static void checkResults() {
		boolean violated = false;
		int failures = 0, violations = 0;
		for (int i = 0; i < numberOfKeys; i++) {
			System.out.println("Test No.: " +i);
			String s = checker.readFromCloud("key" + i);
			if (s == null)
				failures++;
			else if (s.equals("0")) {
				violated = true;
				violations++;
			} else
				System.out.println("OK");
		}
		System.out.println("Out of " + numberOfKeys + " files " + failures
				+ " returned an error.");
		if (violated)
			System.out.println("Monotonic Write Consistency is violated in "
					+ violations + " out of " + numberOfKeys + " tests.");
		else {
			System.out.println("Could not find any violations during "
					+ numberOfKeys + " tests.");
		}
	}

	private static void issueWrites() {
		for (int i = 0; i < numberOfKeys; i++) {
			System.out.println("Test No.: " + i);
			checker.writeToCloud("key" + i, "0");
			checker.writeToCloud("key" + i, "1");
		}

	}

	/**
	 * stores value "value" under the key "key" in the corresponding storage
	 * system
	 * 
	 * @param key
	 * @param value
	 */
	public abstract void writeToCloud(String key, String value);

	/**
	 * 
	 * @param key
	 *            key in the storage system
	 * @return the value read from the storage system or null if an error
	 *         occured
	 */
	public abstract String readFromCloud(String key);

}
