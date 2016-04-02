package sg.edu.nus.iss.universitystore.controller;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import sg.edu.nus.iss.universitystore.constants.Constants;
import sg.edu.nus.iss.universitystore.constants.ViewConstants;
import sg.edu.nus.iss.universitystore.data.DiscountManager;
import sg.edu.nus.iss.universitystore.data.InventoryManager;
import sg.edu.nus.iss.universitystore.data.MemberManager;
import sg.edu.nus.iss.universitystore.data.TransactionManager;
import sg.edu.nus.iss.universitystore.exception.DiscountException;
import sg.edu.nus.iss.universitystore.exception.InventoryException;
import sg.edu.nus.iss.universitystore.exception.MemberException;
import sg.edu.nus.iss.universitystore.exception.TransactionException;
import sg.edu.nus.iss.universitystore.messages.Messages;
import sg.edu.nus.iss.universitystore.model.Discount;
import sg.edu.nus.iss.universitystore.model.Member;
import sg.edu.nus.iss.universitystore.model.Product;
import sg.edu.nus.iss.universitystore.model.TransactionItem;
import sg.edu.nus.iss.universitystore.utility.TableDataUtils;
import sg.edu.nus.iss.universitystore.utility.UIUtils;
import sg.edu.nus.iss.universitystore.utility.UIUtils.DialogType;
import sg.edu.nus.iss.universitystore.view.dialog.ConfirmationDialog;
import sg.edu.nus.iss.universitystore.view.dialog.MemberScanDialog;
import sg.edu.nus.iss.universitystore.view.dialog.ProductScanDialog;
import sg.edu.nus.iss.universitystore.view.dialog.ReceiptDialog;
import sg.edu.nus.iss.universitystore.view.intf.ISalesDelegate;
import sg.edu.nus.iss.universitystore.view.subpanel.SalesPanel;

public class SalesController implements ISalesDelegate {
	/***********************************************************/
	// Instance Variables
	/***********************************************************/
	
	/**
	 * The panel associated with this controller.
	 */
	private SalesPanel salesPanel;
	
	/**
	 * The list of transaction items
	 */
	private ArrayList<TransactionItem> transactionItemList = new ArrayList<TransactionItem>();
	
	/**
	 * The discount associated to the sale.
	 */
	private Discount currentDiscount;
	
	/**
	 * The member associated with the sale.
	 */
	private Member currentMember;
	
	/**
	 * The member dialog object.
	 */
	MemberScanDialog memberDialog;
	
	/**
	 * The product dialog object. 
	 */
	ProductScanDialog productDialog;

	/**
	 * Reference to Inventory Manager
	 */
	private InventoryManager inventoryManager;

	/**
	 * Reference to Member Manager
	 */
	private MemberManager memberManager;

	/***********************************************************/
	// Constructors
	/***********************************************************/
	public SalesController() {
		salesPanel = new SalesPanel(this);
		refreshSalesData(ViewConstants.Labels.STR_PUBLIC, false);
		try {
			inventoryManager = InventoryManager.getInstance();
		} catch (InventoryException e) {
			UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
					DialogType.ERROR_MESSAGE);
		}

	}

	/***********************************************************/
	// Public Methods
	/***********************************************************/
	public SalesPanel getSalesPanel() {
		return salesPanel;
	}

	/**
	 * after click addProduct btn,the function should generate a dialog for
	 * product code input
	 */
	@Override
	public void addProduct() {
		try {
			if (inventoryManager.getAllProducts().size() == 0) {
				UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR,
						Messages.Error.Controller.NO_PRODUCTS_PRS, DialogType.ERROR_MESSAGE);
			} else {
				productDialog = new ProductScanDialog((JFrame) SwingUtilities.getWindowAncestor(salesPanel),
						"Scan Product") {
					@Override
					public boolean onProductScanResult(String productCode, int quantity) {
						// add query product entity
						addProductByBarCode(productCode, quantity);
						return true;
					}
				};
				productDialog.setVisible(true);
			}
		} catch (Exception e) {
			UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
					DialogType.ERROR_MESSAGE);
		}

	}

	/**
	 * test space in barcode FIXME no invoke from other code,reserve for scanner
	 * function
	 */
	public void scanBarCode() {
		String productCode = "test";
		addProductByBarCode(productCode, 1);

	}

	/**
	 * AddProduct by barCode
	 * 
	 * @param productCode
	 * @param quantity
	 */
	private void addProductByBarCode(String productCode, int quantity) {
		// FIXME the same Item exist in the list need directly add
		try {
			Product product = InventoryManager.getInstance().findProduct(productCode);
			if (product == null) {
				UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR,
						ViewConstants.ValidationMessage.PRODUCT_ID_NotExist, DialogType.WARNING_MESSAGE);
			} else {
				if (product.getQuantity() < quantity) {
					// validate quantity before check out
					UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR,
							ViewConstants.ValidationMessage.PRODUCT_QUANTITY_NOTEnough, DialogType.WARNING_MESSAGE);
					return;
				}
				// add transaction Item and show
				TransactionItem item = new TransactionItem(product, quantity);
				boolean isAdd = true;
				// loop to make sure whether there is repeated scanned
				// product,if repeated then increased
				for (int i = 0; i < transactionItemList.size(); i++) {
					if (transactionItemList.get(i).getProduct().getIdentifier()
							.equals(item.getProduct().getIdentifier())) {
						int old_quantity = transactionItemList.get(i).getQuantity();
						transactionItemList.get(i).setQuantity(old_quantity + quantity);
						isAdd = false;
					}
				}
				if (isAdd) {
					transactionItemList.add(item);
				}
				// calculate total and setText
				float total;
				if (currentDiscount != null) {
					total = TransactionManager.getInstance().getTotal(transactionItemList, currentDiscount.getCode());
				} else {
					total = TransactionManager.getInstance().getTotal(transactionItemList, null);
				}

				// modify UI
				salesPanel.updateTable(TableDataUtils.getFormattedTransactionItemListForTable(transactionItemList),
						TableDataUtils.getHeadersForTransactionItemTable());
				salesPanel.setTotal(total);
				// dispose dialog if no TransactionException
				productDialog.dispose();
				productDialog.setVisible(false);
			}
		} catch (Exception e) {
			UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
					DialogType.WARNING_MESSAGE);
		}

	}

	/**
	 * after click checkout,this function will 1.
	 * 
	 */
	@Override
	public void checkOut() {
		// generate a receipt
		if (transactionItemList.size() <= 0) {
			UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR,
					"No products added for checking out.", DialogType.ERROR_MESSAGE);
			return;
		}
		ConfirmationDialog dlg = new ConfirmationDialog((JFrame) SwingUtilities.getWindowAncestor(salesPanel),
				"Check OUT", "Do you confirm to check out?") {

			@Override
			protected boolean confirmClicked() {
				if (salesPanel.checkOUTable()) {
					try {
						TransactionManager.getInstance().addTransaction(transactionItemList,
								currentDiscount.getCode() == Constants.Data.Discount.Member.Public.CODE ? null
										: currentDiscount.getCode(),
								currentMember == null ? ViewConstants.Labels.STR_PUBLIC
										: currentMember.getIdentifier(),Integer.valueOf(salesPanel.getTotal()[3]));
						createReceipt();
						// show receipt here
						// clear salesPanel here
						clearSalesPanel();
						// Show the threshold dialog if required.
						checkIfAnyProductHasReachedThreshold();
					} catch (TransactionException e) {
						e.printStackTrace();
					}
				} else {
					UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.WARNING, "no enough payment",
							DialogType.WARNING_MESSAGE);
				}
				return true;
			}

		};
		dlg.setVisible(true);
	}
	
	private void createReceipt(){
		ReceiptDialog receiptDlg;
		String discountPercentage = currentDiscount == null ? ViewConstants.SalesPanel.NONE_DISCOUNT : currentDiscount.getPercentage()+"%";
		String memberId = currentMember == null ? ViewConstants.Labels.STR_PUBLIC : currentMember.getIdentifier();
		receiptDlg = new ReceiptDialog((JFrame) SwingUtilities.getWindowAncestor(salesPanel), transactionItemList,
				salesPanel.getTotal(), discountPercentage, memberId);
		receiptDlg.setVisible(true);
	}

	private void clearSalesPanel() {
		// clear transaction item list
		transactionItemList.clear();
		// clear calculation part
		salesPanel.clear();
		// clear table update
		salesPanel.updateTable(TableDataUtils.getFormattedTransactionItemListForTable(transactionItemList),
				TableDataUtils.getHeadersForTransactionItemTable());
		// clear memberInfo part
		refreshSalesData(ViewConstants.Labels.STR_PUBLIC, false);
	}

	/**
	 * click one line
	 */
	@Override
	public void cancel(int row) {
		ConfirmationDialog dlg;
		if (row < 0) {
			dlg = new ConfirmationDialog((JFrame) SwingUtilities.getWindowAncestor(salesPanel), "deleteConfirm",
					"Do you want to remove all the productItem") {

				@Override
				protected boolean confirmClicked() {
					transactionItemList.clear();
					salesPanel.setTotal((float) 0.0);
					salesPanel.updateTable(TableDataUtils.getFormattedTransactionItemListForTable(transactionItemList),
							TableDataUtils.getHeadersForTransactionItemTable());
					return true;
				}
			};
		} else {
			dlg = new ConfirmationDialog((JFrame) SwingUtilities.getWindowAncestor(salesPanel), "deleteConfirm",
					"Do you want to remove productItem:" + transactionItemList.get(row).getProduct().getName()) {

				@Override
				protected boolean confirmClicked() {
					try {
						transactionItemList.remove(row);
						if (currentDiscount != null) {
							salesPanel.setTotal(TransactionManager.getInstance().getTotal(transactionItemList,
									currentDiscount.getCode()));
						} else {
							salesPanel.setTotal(TransactionManager.getInstance().getTotal(transactionItemList, null));
						}
						salesPanel.updateTable(
								TableDataUtils.getFormattedTransactionItemListForTable(transactionItemList),
								TableDataUtils.getHeadersForTransactionItemTable());
					} catch (TransactionException e) {
						e.printStackTrace();
					}
					return true;
				}
			};
		}
		dlg.setVisible(true);
	}

	/**
	 * after scan memberCode,show memberName,max discount,available loyalPoint
	 * in customInfo Panel
	 */
	@Override
	public void memberIdentification() {
		try {
			memberManager = MemberManager.getInstance();
			if (memberManager.getAllMembers().size() == 0) {
				UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR,
						"No members enrolled in the store.", DialogType.ERROR_MESSAGE);
			} else {
				memberDialog = new MemberScanDialog((JFrame) SwingUtilities.getWindowAncestor(salesPanel),
						"Enter Member Details") {

					@Override
					public boolean onMemberIdentification(String MemberCode) {
						if (MemberCode.length() == 0) {
							refreshSalesData(ViewConstants.Labels.STR_PUBLIC, true);
						} else {
							refreshSalesData(MemberCode, true);
						}
						return true;
					}
				};
				memberDialog.setVisible(true);
			}
		} catch (MemberException e) {
			UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.ERROR, e.getMessage(),
					DialogType.ERROR_MESSAGE);
		}
	}

	@Override
	public void onSalesPanelVisible() {
		if (currentMember != null) {
			// check whether member Data changed during the transaction
			refreshSalesData(currentMember.getIdentifier(), true);
		} else {
			// previous customer is not a member,purpose of the code is to check
			// whether discount changed
			refreshSalesData(ViewConstants.Labels.STR_PUBLIC, false);
		}
	}

	/**
	 * refresh memberInfo part and calculation part
	 * 
	 * @param Membercode
	 */
	private void refreshSalesData(String memberCode, boolean isShowTip) {
		// set member part
		try {
			Member member = MemberManager.getInstance().getMember(memberCode);
			currentMember = member;
			if (member != null) {
				String loyaltyPoints = (member.getLoyaltyPoints() == -1) ? "0"
						: String.valueOf(member.getLoyaltyPoints());
				salesPanel.onMemberIdentification(member.getName(), loyaltyPoints);
			} else {
				salesPanel.onMemberIdentification(ViewConstants.SalesPanel.MEMBER_OPTION_LABEL, "0");
			}
		} catch (MemberException e) {
			salesPanel.onMemberIdentification(ViewConstants.SalesPanel.MEMBER_OPTION_LABEL, "0");
		}
		// set discount part
		try {
			Discount discount;
			if (currentMember != null) {
				discount = DiscountManager.getInstance().getDiscount(memberCode);
			} else {
				discount = DiscountManager.getInstance().getDiscount(ViewConstants.SalesPanel.MEMBER_OPTION_LABEL);
			}
			currentDiscount = discount;
			if (discount != null) {
				salesPanel.onSetDiscount(discount.getCode(), discount.getPercentage() + "%");
				salesPanel
						.setTotal((TransactionManager.getInstance().getTotal(transactionItemList, discount.getCode())));
			} else {
				salesPanel.onSetDiscount("none discount", "0.0%");
				salesPanel.setTotal(TransactionManager.getInstance().getTotal(transactionItemList, null));
			}
		} catch (DiscountException e) {
			salesPanel.onSetDiscount("none discount", "0.0%");
			try {
				salesPanel.setTotal(TransactionManager.getInstance().getTotal(transactionItemList, null));
			} catch (TransactionException e1) {
				e1.printStackTrace();
			}
		} catch (TransactionException e) {
			if (isShowTip) {
				UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.WARNING, e.getMessage(),
						DialogType.WARNING_MESSAGE);
			}
			e.printStackTrace();
		}
	}
	/***********************************************************/
	// Private Methods
	/***********************************************************/

	/**
	 * Method to check whether any product has reached its threshold value after
	 * checkout.
	 */
	private void checkIfAnyProductHasReachedThreshold() {
		try {
			// Get the array with the list of threshold products
			ArrayList<Product> arrThreshold = inventoryManager.getProductsBelowThreshold();
			// Only if any threshold value has been reached, we will proceed.
			if (arrThreshold.size() != 0) {
				// Create the custom message
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("The following products are below threshold quantity:\n");
				for (Product product : arrThreshold) {
					stringBuilder.append(product.getIdentifier() + " " + "-" + " ");
					stringBuilder.append(product.getName() + "\n");
				}
				// Display a dialog to inform the same
				UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.WARNING, stringBuilder.toString(),
						DialogType.WARNING_MESSAGE);

				// Now update the announcement pane.
				UIUtils.getDashBoardController().getDashboardPanel().setAnnouncementPaneText(stringBuilder.toString());
			}
		} catch (InventoryException e) {
			UIUtils.showMessageDialog(salesPanel, ViewConstants.StatusMessage.WARNING, e.getMessage(),
					DialogType.WARNING_MESSAGE);
		}
	}
}
