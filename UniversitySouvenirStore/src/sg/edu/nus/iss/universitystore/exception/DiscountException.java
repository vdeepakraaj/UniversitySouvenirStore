package sg.edu.nus.iss.universitystore.exception;

import sg.edu.nus.iss.universitystore.messages.Messages;

/**
 * Discount Exception
 * 
 * @author Samrat
 *
 */
public class DiscountException extends Exception {

	/***********************************************************/
	// Enum listing all possible errors possible during discount
	/***********************************************************/
	public enum DiscountError {
	    EMPTY_DISCOUNT_FIELDS(Messages.Error.Discount.EMPTY_DISCOUNT_FIELDS),
	    DISCOUNT_NOT_PRESENT_IN_FILE(Messages.Error.Discount.DISCOUNT_NOT_PRESENT_IN_FILE),
	    DISCOUNT_ALREADY_PRESENT(Messages.Error.Discount.DISCOUNT_ALREADY_PRESENT),
	    MEMBER_NOT_PRESENT_IN_FILE(Messages.Error.Member.MEMBER_NOT_PRESENT_IN_FILE),
	    INVALID_START_DATE(Messages.Error.Discount.INVALID_START_DATE),
	    INVALID_PERIOD(Messages.Error.Discount.INVALID_PERIOD),
	    INVALID_PERCENTAGE(Messages.Error.Discount.INVALID_PERCENTAGE),
	    INVALID_CODE(Messages.Error.Common.INVALID_CODE),
	    INVALID_ELIGIBILITY(Messages.Error.Discount.INVALID_ELIGIBILITY),
	    INVALID_PERCENTAGE_RANGE(Messages.Error.Discount.INVALID_PERCENTAGE_RANGE),
	    NEWMEMBER_PERCENTAGE_ONLY(Messages.Error.Discount.NEWMEMBER_PERCENTAGE_ONLY),
	    EXTNGMEMBER_PERCENTAGE_ONLY(Messages.Error.Discount.EXTNGMEMBER_PERCENTAGE_ONLY),
	    DEFAULT_DISCOUNT_NOT_UPDATABLE(Messages.Error.Discount.DEFAULT_DISCOUNT_NOT_UPDATABLE),
	    DEFAULT_DISCOUNT_NOT_DELETABLE(Messages.Error.Discount.DEFAULT_DISCOUNT_NOT_DELETABLE),
	    START_DATE_PERIOD_ALWAYS(Messages.Error.Discount.START_DATE_PERIOD_ALWAYS),
	    PERIOD_START_DATE_ALWAYS(Messages.Error.Discount.PERIOD_START_DATE_ALWAYS),
	    UNKNOWN_ERROR(Messages.Error.Common.UNKNOWN_ERROR)
	    ;
		
		/**
		 * Instance variable for holding the error message.
		 */
	    private final String message;

	    /**
	     * @param message The message associated with the enum.
	     */
	    private DiscountError(final String message) {
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
	private DiscountError error;
	
	/***********************************************************/
	// Getters
	/***********************************************************/

	public DiscountError getError() {
		return error;
	}
	
	@Override
	public String getMessage() {
		return error.toString();
	}
	
	/***********************************************************/
	// Constructor
	/***********************************************************/
	
	public DiscountException(DiscountError error) {
		this.error = error;
	}
}
