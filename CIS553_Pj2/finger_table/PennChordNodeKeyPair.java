

public class PennChordNodeKeyPair
{
  private int nodeAddr;
  private long nodeKey;
  
  public PennChordNodeKeyPair(int nodeAddr, long nodeKey){
    this.nodeAddr = nodeAddr;
    this.nodeKey = nodeKey;
    
  }
  
  public void setNodeAddr(int nodeAddr)
  {
    this.nodeAddr = nodeAddr;
  }
  
  public void setNodeKey(long nodeKey)
  {
    this.nodeKey = nodeKey;
  }
  
  public int getNodeAddr()
  {
    return nodeAddr;
  }
  
  public long getNodeKey()
  {
    return nodeKey;
  }
}
