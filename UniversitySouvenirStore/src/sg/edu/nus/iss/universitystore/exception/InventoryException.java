/**
 * 
 */
package sg.edu.nus.iss.universitystore.exception;

import sg.edu.nus.iss.universitystore.messages.Messages;

/**
 * Inventory Exception
 * 
 * @author Samrat
 *
 */
public class InventoryException extends Exception{
	/***********************************************************/
	// Enum listing all possible errors possible during inventory
	/***********************************************************/
	public enum InventoryError {
		// Category
		INVALID_CATEGORY_FIELDS(Messages.Error.Category.INVALID_CATEGORY_FIELDS),
		INVALID_CODE(Messages.Error.Common.INVALID_CODE),
		INVALID_CODE_LENTH(Messages.Error.Category.INVALID_CODE),
		CATEGORY_ALREADY_PRESENT(Messages.Error.Category.CATEGORY_ALREADY_PRESENT),
		CATEGORY_NOT_AVAILABLE(Messages.Error.Category.CATEGORY_NOT_AVAILABLE),
		CATEGORY_UNKNOWN_ERROR(Messages.Error.Category.CATEGORY_UNKNOWN_ERROR),
		// Product
		INVALID_NAME(Messages.Error.Product.INVALID_NAME),
		INVALID_QUANTITY(Messages.Error.Product.INVALID_QUANTITY),
		INVALID_REORDER_QUANTITY(Messages.Error.Product.INVALID_REORDER_QUANTITY),
		INVALID_REORDER_THRESHOLD(Messages.Error.Product.INVALID_REORDER_THRESHOLD),
		INVALID_PRICE(Messages.Error.Product.INVALID_PRICE),
		INVALID_BARCODE(Messages.Error.Product.INVALID_BARCODE),
		INVALID_PRODUCT_FIELDS(Messages.Error.Product.INVALID_PRODUCT_FIELDS),
		PRODUCT_NOT_AVAILABLE(Messages.Error.Product.PRODUCT_NOT_AVAILABLE),
		PRODUCT_ZERO(Messages.Error.Product.PRODUCT_ZERO),
		PRODUCT_BAR_CODE_EXISTS(Messages.Error.Product.PRODUCT_BAR_CODE_EXISTS),
		// Member
	    MEMBER_IDENTIFIER_ALREADY_PRESENT(Messages.Error.Member.MEMBER_IDENTIFIER_ALREADY_PRESENT),
	    MEMBER_NOT_PRESENT_IN_FILE(Messages.Error.Member.MEMBER_NOT_PRESENT_IN_FILE),
	    UNKNOWN_ERROR(Messages.Error.Common.UNKNOWN_ERROR)
	    ;
		
		/**
		 * Instance variable for holding the error message.
		 */
	    private final String message;

	    /**
	     * @param message The message associated with the enum.
	     */
	    private InventoryError(final String message) {
	        this.message = message;
	    }

	    /* (non-Javadoc)
	     * @see java.lang.Enum#toString()
	     */
	    @Override
	    public String toString() {
	        return message;
	    }
	}
	
	/***********************************************************/
	// Instance variables
	/***********************************************************/
	private InventoryError error;
	
	/***********************************************************/
	// Getters
	/***********************************************************/

	public InventoryError getError() {
		return error;
	}
	
	@Override
	public String getMessage() {
		return error.toString();
	}
	
	/***********************************************************/
	// Constructor
	/***********************************************************/
	
	public InventoryException(InventoryError error) {
		this.error = error;
	}
}
