/**
 * 
 */
package ch.thn.commandop;

/**
 * @author thomas
 *
 */
public class PreParsedChain extends PreParsedItem {
	
	private PreParsedChain next = null;
	private PreParsedChain parent = null;
	
	private int chainPos = 0;
	
	
	/**
	 * Creates a new chain piece, sets it's parent chain and 
	 * adds itself as a child to the given parent. It also sets the 
	 * flag if this chain is an option, a short option or a parameter.
	 * 
	 * @param arg
	 * @param parent
	 */
	public PreParsedChain(String arg, PreParsedChain parent) {
		super(arg);
		this.parent = parent;
		
		if (parent != null) {
			parent.setNext(this);
			chainPos = parent.getChainPos() + 1;
		}
		
		
	}
	
	/**
	 * 
	 * 
	 * @param chain
	 */
	protected void setNext(PreParsedChain chain) {
		this.next = chain;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public PreParsedChain getNext() {
		return next;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public PreParsedChain getParent() {
		return parent;
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return (next != null);
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public boolean hasPrevious() {
		return (parent != null);
	}
	
	/**
	 * 
	 * 
	 */
	public int getChainPos() {
		return chainPos;
	}
	
	
}
