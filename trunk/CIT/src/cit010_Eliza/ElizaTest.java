package cit010_Eliza;
import java.util.ArrayList;

import junit.framework.TestCase;


public class ElizaTest extends TestCase {
	ArrayList<String> pattern = new ArrayList<String>();
	{
		pattern.add("Q: mother");
		pattern.add("A: Tell me more about your family.");
		pattern.add("Q: I remember");
		pattern.add("S: What made you think of");
		pattern.add("Q: you");
		pattern.add("S: I");
		pattern.add("Q: your");
		pattern.add("S: my");
		pattern.add("Tell me more about it.");
		pattern.add("I'd like to hear more about it.");
		pattern.add("Then?");
		pattern.add("What's next?");
	}
	public void testEliza() {
		Eliza eliza = new Eliza(pattern, "I love my mother");
	}

	public void testGenerateAnswer() {
		Eliza eliza = new Eliza(pattern, "I love my mother");
		System.out.println("answer = "+ eliza.generateAnswer());
		assertTrue (eliza.generateAnswer().equalsIgnoreCase("Tell me more about your family."));
		Eliza eliza2 = new Eliza(pattern, "I remember the old good days.");
		System.out.println("answer = "+ eliza2.generateAnswer());
		assertTrue (eliza2.generateAnswer().equalsIgnoreCase("What made you think of the old good days."));
		Eliza eliza3 = new Eliza(pattern, "I remember your friend.");
		System.out.println("answer= "+ eliza3.generateAnswer());
		assertTrue(eliza3.generateAnswer().equalsIgnoreCase("What made you think of my friend."));
		Eliza eliza4 = new Eliza(pattern, "The mountain is high");
		System.out.println(eliza4.generateAnswer());
		Eliza eliza5 = new Eliza(pattern, "You");
		System.out.println(eliza5.generateAnswer());
	}
}
