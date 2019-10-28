package fixme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hello world!
 */
public final class App {
	private App() {
	}

	/**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String input = br.readLine();
				if (input.toUpperCase().equals("EXIT")) {
					break;
				}
			} catch(IOException e) {
				System.out.println(e.getMessage());
			}
		}
    }
}
