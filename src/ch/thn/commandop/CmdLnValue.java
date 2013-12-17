/**
 * 
 */
package ch.thn.commandop;

/**
 * @author thomas
 *
 */
public class CmdLnValue extends CmdLnBase {
	
	/**
	 * 
	 * @param name
	 * @param defaultValue
	 * @param description
	 * @see CmdLnBase#CmdLnBase(String, String, String)
	 */
	protected CmdLnValue(String name, String defaultValue, String description) {
		super(name, defaultValue, description);
	}
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @see CmdLnBase#CmdLnBase(String, String)
	 */
	protected CmdLnValue(String name, String description) {
		super(name, null, description);
	}
	
	/**
	 * 
	 * @param name
	 * @see CmdLnBase#CmdLnBase(String)
	 */
	protected CmdLnValue(String name) {
		super(name, null, null);
	}
	
	/**
	 * 
	 * @see CmdLnBase#CmdLnBase()
	 */
	protected CmdLnValue() {
		super(null, null);
	}
	
	@Override
	public CmdLnValue getChild(String childName) {
		return super.getChild(childName);
	}
	
	
	@Override
	public String getValue() {
		return super.getValue();
	}
	
	@Override
	public String getValue(int multiValuePos) {
		return super.getValue(multiValuePos);
	}
	
	@Override
	public String getDefaultValue() {
		return super.getDefaultValue();
	}
	
	@Override
	public int getCmdLnPos() {
		return super.getCmdLnPos();
	}
	
	@Override
	public String getDescription() {
		return super.getDescription();
	}
	
	@Override
	public int getLevel() {
		return super.getLevel();
	}
	
	@Override
	public String getName() {
		return super.getName();
	}
	
	@Override
	public int getNumOfValues() {
		return super.getNumOfValues();
	}
	
	@Override
	public boolean isBoolean() {
		return super.isBoolean();
	}
	
	@Override
	public boolean isMandatory() {
		return super.isMandatory();
	}
	
	@Override
	public boolean isMultiValueItem() {
		return super.isMultiValueItem();
	}
	
	@Override
	public boolean isShortOption() {
		return super.isShortOption();
	}
	
	@Override
	protected boolean isParsed() {
		return super.isParsed();
	}
	
	

}
