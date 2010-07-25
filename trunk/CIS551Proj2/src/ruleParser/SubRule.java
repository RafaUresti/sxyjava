package ruleParser;

/**
 * Class for sub_rule in protocol_rule 
 * @author sxycode
 */
public class SubRule {
	private String hostType;
	private String content;
	private boolean [] flags = null;
	public enum Flag {S, A, F, R, P, U};
	
	SubRule(){
		
	}
	public String getHostType() {
		return hostType;
	}
	void setHostType(String hostType) {
		if (hostType.trim().equalsIgnoreCase("from_host")){
			this.hostType = "from_host";
		} else if (hostType.trim().equalsIgnoreCase("to_host")){
			this.hostType = "to_host";
		} else {
			throw new IllegalArgumentException("Host type is either \"from_host\" or \"to_host\"");
		}
		
	}
	
	public String getContent() {
		return content;
	}
	
	void setContent(String content) {
		// Need to get rid of the preceding and ending quotation marks
		this.content = content.substring(1, content.trim().length() - 1);
	}

	/**
	 * Flags are defined in the order of S, A, F, R, P, U with 
	 * <code>true</code> meaning the flag is on, otherwise off.
	 * @return
	 */
	public boolean [] getFlags() {
		return flags;
	}
	/**
	 * Flags are S, A, F, R, P, U
	 * @param flags 
	 */
	void setFlags(String flagString) {
		if (flagString == null){//No flag restriction posed
			return;
		}
		if (flagString.trim().length() == 0){//No flag should be present
			flags = new boolean[] {false, false, false, false, false ,false};
			return;
		}
		boolean[] flags = new boolean[6];
		if (flagString.length() > 6){
			throw new IllegalArgumentException("Too many flags");
		}
		for (int i = 0; i < flags.length; i ++){
			flags[i] = false;
		}
		for (int i = 0; i < flagString.length() && i < 6; i ++){
			if(flagString.charAt(i) == 'S'){
				flags[SubRule.Flag.S.ordinal()] = true;
			} else if(flagString.charAt(i) == 'A'){
				flags[SubRule.Flag.A.ordinal()] = true;
			} else if(flagString.charAt(i) == 'F'){
				flags[SubRule.Flag.F.ordinal()] = true;
			} else if(flagString.charAt(i) == 'R'){
				flags[SubRule.Flag.R.ordinal()] = true;
			} else if(flagString.charAt(i) == 'P'){
				flags[SubRule.Flag.P.ordinal()] = true;
			} else if(flagString.charAt(i) == 'U'){
				flags[SubRule.Flag.U.ordinal()] = true;
			} else {
				throw new IllegalArgumentException("Flag format error");
			}
		}
		if (flags.length != 6){
			throw new IllegalArgumentException("Flag format error!");
		}
		this.flags = flags;
	}
	
}
