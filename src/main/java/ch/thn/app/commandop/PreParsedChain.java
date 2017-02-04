/**
 *    Copyright 2013 Thomas Naeff (github.com/thnaeff)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.thn.app.commandop;

/**
 * @author Thomas Naeff (github.com/thnaeff)
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
