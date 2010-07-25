package ruleParser;

/**
 * Class for tcp_stream_rule
 * @author sxycode
 *
 */
public class TcpStreamRule extends Rule{
	private String content;
	private String hostType;

	public String getHost() {
		return content;
	}
	
	void setContent(String content) {
		//Need to get rid of the preceding and ending quotation marks
		this.content = content.substring(1, content.trim().length() - 1);
	}
	
	/**
	 * The regex string will be here
	 * @param content
	 */
	public String getContent(){
		return content;
	}
	
	/**
	 * The host type is either "from_host" or "to_host"
	 * @return "from_host" or "to_host"
	 */
	public String getHostType() {
		return hostType;
	}
	
	/**
	 * Set the host type of the sub_rule to be either "from_host" or "to_host" 
	 * @param hostType
	 */
	void setHostType(String hostType) {
		if (hostType.trim().equals("from_host") || hostType.trim().equals("to_host")){
			this.hostType = hostType.trim();
		} else {
			throw new IllegalArgumentException("Host type is either \"from_host\" or \"to_host\"");
		}
	}
}
