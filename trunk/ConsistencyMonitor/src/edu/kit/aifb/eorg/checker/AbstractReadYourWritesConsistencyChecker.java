/**
 * 
 */
package edu.kit.aifb.eorg.checker;

/**
 * @author David Bermbach
 * 
 *         created on: 16.03.2012
 */
public abstract class AbstractReadYourWritesConsistencyChecker {

	/** key which shall be used */
	private static final String key = "key";

	/** how many writes shall be issued */
	private static final int numberOfTests = 1000;

	/** how many reads shall be in between writes */
	private static final int numberOfReadsPerTest = 100;

	/** checker */
	private static AbstractReadYourWritesConsistencyChecker checker = new S3ReadYourWritesConsistencyChecker();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean violated = false;
		int violations = 0;
		for (int i = 0; i < numberOfTests; i++) {
			System.out.println("Test No.: "+i);
			if (!checker.writeToCloud(key, "" + i)) {
				i--;
				System.out.println("Write failed, retrying");
				continue;
			}
			for (int j = 0; j < numberOfReadsPerTest; j++) {
				String s = checker.readFromCloud(key);
				if (s == null) {
					j--;
					System.out.println("Read failed, retrying");
					continue;
				}
				if (!s.equals("" + i)) {
					violated = true;
					violations++;
				}

			}
		}
		if (violated) {
			System.out.println("Read your writes consistency is violated in "
					+ violations + " out of "
					+ (numberOfReadsPerTest * numberOfTests)
					+ " reads (total number of tests: " + numberOfTests + ")");
		} else {
			System.out.println("Could not find any violations in " + violations
					+ " out of " + (numberOfReadsPerTest * numberOfTests)
					+ " reads (total number of tests: " + numberOfTests + ")");
		}

	}

	/**
	 * stores value "value" under the key "key" in the corresponding storage
	 * system
	 * 
	 * @param key
	 * @param value
	 * @return true if write was successful, else false
	 */
	public abstract boolean writeToCloud(String key, String value);

	/**
	 * 
	 * @param key
	 *            key in the storage system
	 * @return the value read from the storage system or null if an error
	 *         occured
	 */
	public abstract String readFromCloud(String key);

}
