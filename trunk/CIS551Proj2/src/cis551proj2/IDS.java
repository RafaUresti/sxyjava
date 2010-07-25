package cis551proj2;

import java.io.File;

import ruleParser.RuleParser;
import ids.Simulator;
import jpcapParser.JpParser;
import net.sourceforge.jpcap.net.IPPacket;

/**
 * IDS Class
 * 
 * Wrapper class for the main function.
 * Creates a Simulator, loads the rule and jpcap parsers
 * Repeatidly feeds the packets into the Simulator
 *
 */
public class IDS {

	/**
	 * Main Function for IDS 
	 * 
	 * Creates a RuleParser, Packet Parser, and a simulator
	 * 
	 * Feeds the rules into the simulator, then feeds the packets into the simulator one by one
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		RuleParser ruleParser = null;
		Simulator sim = null;
		String homeAddress = null;
		
		if(args.length == 2){
			System.out.println("Starting IDS...");
			
			// Load the Rule File
			System.out.println("   Loading Rule File " + args[1]);
			File ruleFile = new File(args[1]);
			ruleParser = new RuleParser(ruleFile);
			
			// Mark the Host
			homeAddress = ruleParser.getHost();
			
			// Load the Trace File
			System.out.println("   Loading Trace File " + args[0]);

			
			// Create the Simulator
			sim = new Simulator(homeAddress, ruleParser.getRules());
		
			// Loop through the parsed packets, feed them into the simulator
			JpParser JP = new JpParser(args[0]);
			while(JP.handler.hasMorePacket()){
				sim.processPacket((IPPacket) JP.handler.getNextPacket());
			}
		}
		else{
			System.out.println("Usage:");
			System.out.println("  java -classpath src cis551proj2.IDS <trace file> <rule file> ");
		}
	}

}
  