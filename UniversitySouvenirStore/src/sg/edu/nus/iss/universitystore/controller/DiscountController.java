package sg.edu.nus.iss.universitystore.controller;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import sg.edu.nus.iss.universitystore.constants.Constants;
import sg.edu.nus.iss.universitystore.constants.ViewConstants;
import sg.edu.nus.iss.universitystore.data.DiscountManager;
import sg.edu.nus.iss.universitystore.exception.DiscountException;
import sg.edu.nus.iss.universitystore.messages.Messages;
import sg.edu.nus.iss.universitystore.model.Discount;
import sg.edu.nus.iss.universitystore.utility.TableDataUtils;
import sg.edu.nus.iss.universitystore.utility.UIUtils;
import sg.edu.nus.iss.universitystore.utility.UIUtils.DialogType;
import sg.edu.nus.iss.universitystore.validation.DiscountValidation;
import sg.edu.nus.iss.universitystore.view.dialog.ConfirmationDialog;
import sg.edu.nus.iss.universitystore.view.dialog.DiscountDialog;
import sg.edu.nus.iss.universitystore.view.intf.IDiscountDelegate;
import sg.edu.nus.iss.universitystore.view.subpanel.DiscountPanel;

public class DiscountController implements IDiscountDelegate {
	/***********************************************************/
	// Instance Variables
	/***********************************************************/
	/**
	 * The panel associated with this controller.
	 */
	private DiscountPanel discountPanel;
	/**
	 * Reference to Discount Manager
	 */
	private DiscountManager discountManager;
	/**
	 * the List of discountItem
	 */
	private ArrayList<Discount> discountList;

	/***********************************************************/
	// Constructors
	/***********************************************************/
	/**
	 * Discount Controller Constructor
	 */
	public DiscountController() {
		try {
			discountManager = DiscountManager.getInstance();
			discountList = discountManager.getAllDiscounts();
		} catch (DiscountException e) {
			UIUtils.showMessageDialog(discountPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
					DialogType.ERROR_MESSAGE);
		}
		discountPanel = new DiscountPanel(this);
		discountPanel.updateTable(TableDataUtils.getFormattedDiscountListForTable(discountList),
				TableDataUtils.getHeadersForDiscountTable());
	}

	/***********************************************************/
	// Public Methods
	/***********************************************************/
	public DiscountPanel getDiscountPanel() {
		return discountPanel;
	}

	/***********************************************************/
	// Abstract Method Implementation
	/***********************************************************/
	@Override
	public void addDiscount() {
		DiscountDialog dlg = new DiscountDialog((JFrame) SwingUtilities.getWindowAncestor(discountPanel),
				ViewConstants.Labels.STR_ADD_DISCOUNT) {

			@Override
			public boolean onDiscountCallBack(String code, String description, String startDate, String period,
					String percentage, String eligibilty) {
				boolean flag = false;//used to judge whether the data operation is success
				
				try {
					if (DiscountValidation.isValidData(code.toUpperCase(), description, startDate.toUpperCase(),
							period.toUpperCase(), percentage, eligibilty)) {
						Discount discount = new Discount(code.toUpperCase(), description, startDate.toUpperCase(),
								period.toUpperCase(), percentage, eligibilty);
						flag = discountManager.addDiscount(discount);//add discount operation
						discountList = discountManager.getAllDiscounts();
					}
				} catch (DiscountException e) {
					UIUtils.showMessageDialog(discountPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
							DialogType.ERROR_MESSAGE);
					return false;
				}
				return isUpdateUI(flag);
			}

		};
		dlg.setVisible(true);
	}

	@Override
	public void deleteDiscount(int row) {
		// Only if the count is greated than five, we proceed with deleting.
		if(discountList.size() > Constants.Data.Discount.DISCOUNT_MINIMUM_COUNT){
			Discount discount = discountList.get(row);
			new ConfirmationDialog((JFrame) SwingUtilities.getWindowAncestor(discountPanel), ViewConstants.Labels.STR_DELETE_DISCOUNT,
					ViewConstants.Controller.Discount.DELETE_DISCOUNT + discount.getCode()) {			

				@Override
				protected boolean confirmClicked() {
					boolean flag = false;//used to judge whether the data operation is success
					try {
						flag = discountManager.deleteDiscount(discount.getCode(), false);//delete discount operation
						discountList = discountManager.getAllDiscounts();
					} catch (DiscountException e) {
						UIUtils.showMessageDialog(discountPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
								DialogType.ERROR_MESSAGE);
					}
					return isUpdateUI(flag);
				}
			}.setVisible(true);
		}else {
			UIUtils.showMessageDialog(discountPanel, ViewConstants.StatusMessage.ERROR, Messages.Error.Discount.DISCOUNT_COUNT_LESS_THAN_EXPECTED,
					DialogType.ERROR_MESSAGE);
		}
	}

	@Override
	public void updateDiscount(int row) {
		if (row < 0) {
			return;
		}

		// Get the object at the index
		Discount oldDiscount = discountList.get(row);

		DiscountDialog updateDlg = new DiscountDialog((JFrame) SwingUtilities.getWindowAncestor(discountPanel),
				ViewConstants.Labels.STR_EDIT_DISCOUNT) {

			@Override
			public boolean onDiscountCallBack(String code, String description, String startDate, String period,
					String percentage, String eligibilty) {
				boolean flag = false;
				
				try {
					if (DiscountValidation.isValidData(code.toUpperCase(), description, startDate.toUpperCase(),
							period.toUpperCase(), percentage, eligibilty)) {

						Discount newDiscount = new Discount(code.toUpperCase(), description, startDate.toUpperCase(),
								period.toUpperCase(), percentage, eligibilty);

						flag = discountManager.updateDiscount(oldDiscount, newDiscount);//update discount operation
						discountList = discountManager.getAllDiscounts();
					}
				} catch (DiscountException e) {
					UIUtils.showMessageDialog(discountPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
							DialogType.ERROR_MESSAGE);
					
					return false;
				}
				return isUpdateUI(flag);
			}
		};
		updateDlg.setDiscountData(discountList.get(row));
		updateDlg.setVisible(true);
	}

	@Override
	public void rowNotSelected() {
		// Display message for error.
		UIUtils.showMessageDialog(discountPanel, ViewConstants.StatusMessage.ERROR, ViewConstants.Controller.PLEASE_SELECT_ROW,
				DialogType.WARNING_MESSAGE);
	}
	
	/***********************************************************/
	// Public Methods
	/***********************************************************/
	/**
	 * judge whether updateUI according to flag
	 * 
	 * @param flag
	 * @return
	 */
	private boolean isUpdateUI(boolean flag) {
		if (flag) {
			discountPanel.updateTable(TableDataUtils.getFormattedDiscountListForTable(discountList),
					TableDataUtils.getHeadersForDiscountTable());
			return true;
		} else {
			return false;
		}
	}
}
