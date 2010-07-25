package ruleParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class to take in and parses a rule file and
 * builds host field and a list of Rule objects.
 * @author Xiaoyi Sheng
 *
 */
public class RuleParser {
	private Scanner scanner;
	private String host;
	private ArrayList<Rule> rules;
	private int lineNum; // records the current parsing line in the scanner for error reporting

	public RuleParser(File file){
		rules = new ArrayList<Rule>();
		lineNum = 0;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		parse();
	}

	private void parse() {
		if (!isRuleFile()){
			throw new IllegalArgumentException("Error parsing rule file!");
		}
	}

	/**
	 * Checks if the file is a rule file
	 * @return <code>true</code> if is rule file
	 */
	private boolean isRuleFile() {
		if (!scanner.hasNextLine()){
			return false;
		}
		String hostLine = scanner.nextLine(); lineNum++;
		String[] hostResults = hostLine.split("=", 2); //only parse the first "=" sign
		if (!hostResults[0].trim().equals("host")){
			alert("Error in host line");
			return false;
		} else if (!isIp(hostResults[1].trim())){
			alert("Host IP address is invalid");
			return false;
		} else {
			host = hostResults[1].trim();
			if(scanner.hasNextLine()){
				lineNum++;
				if (scanner.nextLine().trim().length() > 0){
					alert("No empty line available after host at line " + lineNum);
					return false; //makes sure that there is an empty line after host
				}
			}
			while (isRule()){}
			return true;
		}
	}

	/**
	 * Checks if the rest of the file begins with a rule and if so creates
	 * a rule object that's added to the rules, and moves the scanner over
	 * the checked region.
	 * @return <code>true</code> if the rest of the file begins with a rule
	 */
	private boolean isRule() {
		String nextLine = "";
		//Get rid of empty lines between rules and at the end of the file
		//to eliminate false negatives
		while(scanner.hasNextLine()){
			nextLine = scanner.nextLine().trim(); lineNum ++;
			if (nextLine.length() > 0)
				break;
		}
		if (nextLine.length() == 0){
			return false;
		}
		String[] nameResults = nextLine.split("=", 2);
		if (!nameResults[0].trim().equals("name")){
			alert("Error in rule line at line " + lineNum);
			return false;
		} else if (0 == nameResults[1].trim().length()){
			alert("Please provide rule name at line " + lineNum);
			return false;
		}
		String ruleName = nameResults[1];
		if (!scanner.hasNextLine()){
			alert("Rule content missing after rule: "+ruleName);
			return false;
		}
		String[] typeResults = scanner.nextLine().split("=", 2); lineNum++;
		if (!typeResults[0].trim().equals("type")){
			alert("Error in type line at line " + lineNum);
			return false;
		}
		if (typeResults[1].trim().equals("tcp_stream")){
			TcpStreamRule tcpStreamRule = new TcpStreamRule();
			tcpStreamRule.setName(ruleName);
			if (isTcpStreamRule(tcpStreamRule)){
				rules.add(tcpStreamRule);
				return true;
			} else {
				return false;
			}
		}
		if (typeResults[1].trim().equals("protocol")){
			ProtocolRule protocolRule = new ProtocolRule();
			protocolRule.setName(ruleName);
			if (isProtocolRule(protocolRule)){
				rules.add(protocolRule);
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	/**
	 * Checks if the rest of the file begins with the body of a 
	 * protocol rule, and if so fills the parameters of the
	 * protocolRule, and moves the scanner over.
	 * @param protocolRule The ProtocolRule object to fill
	 * @return <code>true</code> if there is a protocol rule
	 */
	private boolean isProtocolRule(ProtocolRule protocolRule) {
		if (!scanner.hasNextLine()){
			alert("proto missing at the end");
			return false;
		}
		String[] protoResults =scanner.nextLine().split("=", 2); lineNum++;

		if (!protoResults[0].trim().equals("proto")){
			alert("Error in proto line at line " + lineNum);
			return false;
		}
		try{
			protocolRule.setProto(protoResults[1]);
		} catch (IllegalArgumentException e){
			alert("Error in proto line at line " + lineNum + e.getMessage());
			return false;
		}

		if (!isRulePart(protocolRule)){
			return false;
		}
		ArrayList<SubRule> subRules = new ArrayList<SubRule>(); 
		if (!isSubRule(subRules)){
			alert("sub rule missing after line " + lineNum);
			return false;
		}
		while (isSubRule(subRules)){}
		protocolRule.setSubRules(subRules);
		if (protocolRule.getProto().equals("udp")){
			for (SubRule s: subRules){
				if (s.getFlags()!= null){
					alert("No flags for udp!");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the following is a sub rule. If so, builds a sub rule and
	 * add to the list of subRules. If not, scanner is not advanced.
	 * @param subRules The list of subRules to add to 
	 * @return <code>true</code> if is a sub rule
	 */
	private boolean isSubRule(ArrayList<SubRule> subRules) {
		String subRuleLine;
		if ((subRuleLine = scanner.findInLine("(from|to)_host=.*")) == null){
			return false;
		} else {
			scanner.nextLine(); //fineInLine doesn't move scanner past line breakers
			lineNum ++;
		}
		String[] hostRules = subRuleLine.split("=", 2);
		if (!hostRules[0].trim().equals("from_host") && 
				!hostRules[0].trim().equals("to_host")){
			alert("Error in sub_rule line at line " + lineNum);
			return false;
		}
		SubRule subRule = new SubRule();
		subRule.setHostType(hostRules[0]);
		if (hostRules[1].contains("with flags=")){
			String[] regFlags = hostRules[1].split("with flags=");
			if (0 == regFlags[0].trim().length()){//the regular expression part
				alert("Regular expression missing for from_host or to_host at line " + lineNum);
				return false;
			} 
			subRule.setContent(regFlags[0].trim());
			if (regFlags.length > 1){
				String flags = regFlags[1].trim();
				try{
					subRule.setFlags(flags);
				} catch (IllegalArgumentException e){
					alert(e.getMessage() + " at line " + lineNum);
				}
			} else {//Nothing follows the "with flags="
				subRule.setFlags("");
			}
		} else {//no flag requirement
			subRule.setContent(hostRules[1].trim());
		}
		subRules.add(subRule);
		return true;
	}

	/**
	 * Checks if the rest of the file begins with the body of a 
	 * tcp stream rule, and if so fills the parameters of the
	 * tcpStreamRule, and moves the scanner over.
	 * @param tcpStreamRule The TcpStreamRule object to fill
	 * @return <code>true</code> if there is a tcp stream rule
	 */
	private boolean isTcpStreamRule(TcpStreamRule tcpStreamRule) {

		if (!isRulePart(tcpStreamRule)){
			return false;
		}
		//from_host|to_host line
		if (!scanner.hasNextLine()){
			alert("from_host or to_host missing at the end");
			return false;
		}
		String[] hostRules = scanner.nextLine().split("=", 2); lineNum++;
		if (!hostRules[0].trim().equals("from_host") && 
				!hostRules[0].trim().equals("to_host")){
			alert("Error in attacker line at line " + lineNum);
			return false;
		}
		tcpStreamRule.setHostType(hostRules[0]);
		if (0 == hostRules[1].trim().length()){
			alert("Regular expression missing for from_host or to_host at line " + lineNum);
			return false;
		} else {
			tcpStreamRule.setContent(hostRules[1]);
		}
		return true;
	}

	/**
	 * Checks if the rest of the file begins with the common rule
	 * parts (host_port, attacker_port and attacker) and builds
	 * the rule.
	 * @param rule the rule to build
	 * @return <code>true</code> if is the common rule part
	 */
	private boolean isRulePart(Rule rule) {
		//host_port line
		if (!scanner.hasNextLine()){
			alert("host_port missing at the end");
			return false;
		}
		String[] hostPortResults =scanner.nextLine().split("=", 2); lineNum++;

		if (!hostPortResults[0].trim().equals("host_port")){
			alert("Error in host_port line at line " + lineNum);
			return false;
		}
		if (hostPortResults[1].trim().equals("any")){
			rule.setHostPort(-1);
		} else if (isPort(hostPortResults[1])){
			rule.setHostPort(Integer.parseInt(hostPortResults[1]));
		} else {
			alert("Format error in host_port at line " + lineNum);
			return false;
		}
		//attacker_port line
		if (!scanner.hasNextLine()){
			alert("attacker_port missing at line " + lineNum);
			return false;
		}
		String[] attackerPortRules = scanner.nextLine().split("=", 2); lineNum++;
		if (!attackerPortRules[0].trim().equals("attacker_port")){
			alert("Error in attacker_port line at line " + lineNum);
			return false;
		}
		if (!attackerPortRules[1].trim().equals("any") && !isPort(attackerPortRules[1])){
			alert("Format error for attacker_port at line " + lineNum);
			return false;
		} else {
			rule.setAttackerPort(attackerPortRules[1]);
		}
		//attacker line
		if (!scanner.hasNextLine()){
			alert("attacker missing");
			return false;
		}
		String[] attackerRules = scanner.nextLine().split("=", 2); lineNum++;
		if (!attackerRules[0].trim().equals("attacker")){
			alert("Error in attacker line at line " + lineNum);
			return false;
		}
		if (!attackerRules[1].trim().equals("any") && !isIp(attackerRules[1])){
			alert("Format error for attacker at line " + lineNum);
			return false;
		} else {
			rule.setAttacker(attackerRules[1]);
		}
		return true;
	}


	/**
	 * Checks if a given string is a legal IP address
	 * @param ip The IP string to check
	 * @return <code>true</code> if legal
	 */
	private static boolean isIp(String ip){
		String[] ipComponents = ip.split("\\.");
		if (ipComponents.length != 4){
			return false;
		} else {
			for (int i = 0; i < 4; i ++){
				try{
					int num = Integer.parseInt(ipComponents[i]);
					if (num < 0 || num > 255){
						return false;
					}
				} catch (NumberFormatException e){
					return false;
				}

			}
		}
		return true;
	}

	/**
	 * Checks if port represents a number between 0 and 65535
	 * @param port The port number to check
	 * @return <code>true</code> if is within range
	 */
	private static boolean isPort(String port){
		try{
			int portNum = Integer.parseInt(port);
			if (portNum < 0 || portNum > 65535){
				return false;
			}
		} catch (NumberFormatException e){
			return false;
		}
		return true;
	}
	public ArrayList<Rule> getRules(){
		return rules;
	}

	public static void alert(String alert){
		//TODO implement alert to notify errors
		System.out.println(alert);
	}
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
