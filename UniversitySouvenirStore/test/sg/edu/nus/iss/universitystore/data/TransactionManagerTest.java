package sg.edu.nus.iss.universitystore.data;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sg.edu.nus.iss.universitystore.constants.Constants;
import sg.edu.nus.iss.universitystore.constants.Constants.Data.Discount.Eligibility;
import sg.edu.nus.iss.universitystore.constants.JUnitConstants;
import sg.edu.nus.iss.universitystore.exception.DiscountException;
import sg.edu.nus.iss.universitystore.exception.InventoryException;
import sg.edu.nus.iss.universitystore.exception.MemberException;
import sg.edu.nus.iss.universitystore.exception.TransactionException;
import sg.edu.nus.iss.universitystore.exception.TransactionException.TransactionError;
import sg.edu.nus.iss.universitystore.intf.UniversityStoreJUnit;
import sg.edu.nus.iss.universitystore.messages.JUnitMessages;
import sg.edu.nus.iss.universitystore.model.Category;
import sg.edu.nus.iss.universitystore.model.Discount;
import sg.edu.nus.iss.universitystore.model.Member;
import sg.edu.nus.iss.universitystore.model.Product;
import sg.edu.nus.iss.universitystore.model.TransactionItem;
import sg.edu.nus.iss.universitystore.utility.JUnitUtility;

/**
 * JUnit for Transaction Manager
 * 
 * @author Sanjay
 *
 */
public class TransactionManagerTest extends UniversityStoreJUnit {

	/**
	 * Categories
	 */
	private Category category1, category2;
	/**
	 * Category Codes
	 */
	private String categoryCode1, categoryCode2, invalidCatgryCode1, invalidCatgryCode2, invalidCatgryCode3;
	/**
	 * Category Names
	 */
	private String categoryName1, categoryName2, invalidCatgryName2, invalidCatgryName3;

	/**
	 * Product ID
	 */
	private String productID1, productID2, productID3;
	/**
	 * Product Name
	 */
	private String productName1, productName2, productName3;
	/**
	 * Product Description
	 */
	private String productDescription1, productDescription2, productDescription3;
	/**
	 * Product Quality
	 */
	private Integer productQuantity1, productQuantity2, productQuantity3;
	/**
	 * Product Price
	 */
	private Double productPrice1, productPrice2, productPrice3;
	/**
	 * Product Bar Code
	 */
	private Integer productBarCode1, productBarCode2, productBarCode3, invalidProductBarCode;
	/**
	 * Product Reorder Threshold
	 */
	private Integer productReorderThreshold1, productReorderThreshold2, productReorderThreshold3;
	/**
	 * Product Reorder Quantity
	 */
	private Integer productReorderQuantity1, productReorderQuantity2, productReorderQuantity3;

	/**
	 * Member IDs
	 */
	private String memberID, invalidMemberID;
	/**
	 * Member Names
	 */
	private String memberName;
	/**
	 * Member Loyalty Points
	 */
	private String memberLoyaltyPoints;

	private String discountCode1, invalidDiscount;
	private String discountPercentage1;
	private String discountStartDate1;
	private String discountDesc1;
	private String discountPeriod1;

	private Discount discount1;

	private ArrayList<TransactionItem> transactionItemLst;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sg.edu.nus.iss.universitystore.intf.UniversityStoreJUnit#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Category Set 1
		categoryCode1 = "CLT";
		categoryName1 = "Cloths";
		category1 = new Category(categoryCode1, categoryName1);

		// Category Set 2
		categoryCode2 = "SHO";
		categoryName2 = "Shoes";
		category2 = new Category(categoryCode2, categoryName2);

		// Product Set 1
		productName1 = "Nike Cloths";
		productDescription1 = "Nike Cloths L";
		productQuantity1 = 50;
		productPrice1 = 50.00;
		productBarCode1 = 1;
		productReorderQuantity1 = 40;
		productReorderThreshold1 = 10;

		// Product Set 2
		productName2 = "Nike Cloths";
		productDescription2 = "Nike Cloths XL";
		productQuantity2 = 30;
		productPrice2 = 60.00;
		productBarCode2 = 11;
		productReorderQuantity2 = 20;
		productReorderThreshold2 = 10;

		// Product Set 3
		productName3 = "Nike Shoes";
		productDescription3 = "Nike Running Shoes Size 9.5";
		productQuantity3 = 80;
		productPrice3 = 100.00;
		productBarCode3 = 111;
		productReorderQuantity3 = 55;
		productReorderThreshold3 = 25;

		// New Member
		memberID = "ABZW123KL";
		memberName = "Abzsde Klaoel";
		memberLoyaltyPoints = "50";

		// Invalid Member
		invalidMemberID = "INVALIDMEMBER";

		// Discount 1
		discountCode1 = "GME_WEEK";
		discountDesc1 = "Games Week";
		discountStartDate1 = "2016-03-31";
		discountPercentage1 = "10";
		discountPeriod1 = "7";
		discount1 = new Discount(discountCode1, discountDesc1, discountStartDate1, discountPeriod1, discountPercentage1,
				Eligibility.MEMBER);

		// Invalid Discount
		invalidDiscount = "INVALIDDISCOUNT";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sg.edu.nus.iss.universitystore.intf.UniversityStoreJUnit#tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		InventoryManager.deleteInstance();
		MemberManager.deleteInstance();
		DiscountManager.deleteInstance();
		TransactionManager.deleteInstance();

		// Destroy all category content;
		category1 = category2 = null;
		categoryCode1 = categoryCode2 = invalidCatgryCode1 = invalidCatgryCode2 = invalidCatgryCode3 = null;
		categoryName1 = categoryName2 = invalidCatgryName2 = invalidCatgryName3 = null;

		// Destroy all product content;
		productID1 = productID2 = productID3 = null;
		productName1 = productName2 = productName3 = null;
		productDescription1 = productDescription2 = productDescription3 = null;
		productQuantity1 = productQuantity2 = productQuantity3 = null;
		productPrice1 = productPrice2 = productPrice3 = null;
		productBarCode1 = productBarCode2 = productBarCode3 = invalidProductBarCode = null;
		productReorderThreshold1 = productReorderThreshold2 = productReorderThreshold3 = null;
		productReorderQuantity1 = productReorderQuantity2 = productReorderQuantity3 = null;

		memberID = invalidMemberID = null;
		memberName = null;
		memberLoyaltyPoints = null;

		discountCode1 = invalidDiscount = null;
		discountPercentage1 = null;
		discountStartDate1 = null;
		discountDesc1 = null;
		discountPeriod1 = null;

		discount1 = null;

		transactionItemLst = null;
	}

	private boolean initializeManagerDetails() {

		// Copy Test File Category.dat
		try {
			JUnitUtility.copyFile(Constants.Data.FileName.CATEGORY_DAT,
					JUnitConstants.Data.FILE_FOLDER.TRANSACTION.toString().toLowerCase()
							+ Constants.Data.FILE_PATH_SEPTR);
			JUnitUtility.copyFile(Constants.Data.FileName.MEMBER_DAT,
					JUnitConstants.Data.FILE_FOLDER.TRANSACTION.toString().toLowerCase()
							+ Constants.Data.FILE_PATH_SEPTR);
			JUnitUtility.copyFile(Constants.Data.FileName.DISCOUNT_DAT,
					JUnitConstants.Data.FILE_FOLDER.TRANSACTION.toString().toLowerCase()
							+ Constants.Data.FILE_PATH_SEPTR);
			JUnitUtility.copyFile(Constants.Data.FileName.PRODUCT_DAT,
					JUnitConstants.Data.FILE_FOLDER.TRANSACTION.toString().toLowerCase()
							+ Constants.Data.FILE_PATH_SEPTR);

			// Instantiate All Manager
			TransactionManager.getInstance();
			InventoryManager inventoryManager = InventoryManager.getInstance();
			MemberManager memberManager = MemberManager.getInstance();
			DiscountManager discountManager = DiscountManager.getInstance();

			// Check Initial Conditions
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.CATEGORY_DAT, 1));
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.VENDOR_DAT, 4));
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.DISCOUNT_DAT, 1));
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.MEMBER_DAT, 1));
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.TRANSACTION_DAT, 1));

			// Add Categories
			inventoryManager.addCategory(categoryCode1, categoryName1);
			inventoryManager.addCategory(categoryCode2, categoryName2);

			// Add Products
			productID1 = inventoryManager
					.addProduct(categoryCode1, productName1, productDescription1, String.valueOf(productQuantity1),
							String.valueOf(productPrice1), String.valueOf(productBarCode1),
							String.valueOf(productReorderThreshold1), String.valueOf(productReorderQuantity1))
					.getIdentifier();
			productID2 = inventoryManager
					.addProduct(categoryCode1, productName2, productDescription2, String.valueOf(productQuantity2),
							String.valueOf(productPrice2), String.valueOf(productBarCode2),
							String.valueOf(productReorderThreshold2), String.valueOf(productReorderQuantity2))
					.getIdentifier();
			productID3 = inventoryManager
					.addProduct(categoryCode2, productName3, productDescription3, String.valueOf(productQuantity3),
							String.valueOf(productPrice3), String.valueOf(productBarCode3),
							String.valueOf(productReorderThreshold3), String.valueOf(productReorderQuantity3))
					.getIdentifier();
			productID1 = inventoryManager.findProductByBarCode(String.valueOf(productBarCode1)).getIdentifier();
			productID2 = inventoryManager.findProductByBarCode(String.valueOf(productBarCode2)).getIdentifier();
			productID3 = inventoryManager.findProductByBarCode(String.valueOf(productBarCode3)).getIdentifier();

			// Add Member
			Assert.assertTrue(memberManager.addNewMember(memberID, memberName));
			Member newMember = memberManager.getMember(memberID);
			Member updatedMember = new Member(memberID, memberName, memberLoyaltyPoints);
			Assert.assertTrue(memberManager.updateMember(newMember, updatedMember));

			// Add Discount
			Assert.assertTrue(discountManager.addDiscount(discount1));

			return true;
		} catch (IOException ioExcpetion) {
			fail(JUnitMessages.Error.JUNIT_FAIL);
		} catch (TransactionException | InventoryException | MemberException | DiscountException transactionExp) {
			fail(JUnitMessages.Error.JUNIT_FAIL);
		}
		return false;
	}

	/**
	 * Initialize Discount Test
	 */
	@Test
	public void testInitializeDiscount() {
		try {
			// Instantiate Discount Manager
			TransactionManager.getInstance();

			// Check Initial Conditions
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.CATEGORY_DAT, 1));
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.DISCOUNT_DAT, 1));
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.MEMBER_DAT, 1));
			Assert.assertTrue(JUnitUtility.checkFileCount(Constants.Data.FileName.TRANSACTION_DAT, 1));

		} catch (TransactionException transactionExp) {
			fail(JUnitMessages.Error.JUNIT_FAIL);
		}
	}

	/**
	 * Test method for
	 * {@link sg.edu.nus.iss.universitystore.data.TransactionManager#getTransactionReport(java.time.LocalDate, java.time.LocalDate)}
	 * .
	 */
	@Test
	public void testGetTransactionReportLocalDateLocalDate() {
		try {
			Assert.assertTrue(initializeManagerDetails());

			// Instantiate Discount Manager
			TransactionManager transactionManager = TransactionManager.getInstance();
			InventoryManager invantoryManager = InventoryManager.getInstance();
			DiscountManager discountManager = DiscountManager.getInstance();

			// Create Transaction Items List
			ArrayList<TransactionItem> arrTransactionItmLst = new ArrayList<>();
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID1), 1));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID2), 2));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID3), 3));

			// Add Transaction Items to Transaction
			Assert.assertTrue(transactionManager.addTransaction(arrTransactionItmLst,
					discountManager.getDiscount(memberID).getCode(), memberID, 20));
			Assert.assertTrue(transactionManager.getTransactionReport(LocalDate.now(), LocalDate.now()).size() == 3);
			Assert.assertTrue(
					transactionManager.getTransactionReport(LocalDate.now(), LocalDate.now().plusDays(2)).size() == 3);
			Assert.assertTrue(transactionManager
					.getTransactionReport(LocalDate.now().minusDays(3), LocalDate.now().minusDays(1)).size() == 0);
		} catch (TransactionException | InventoryException | DiscountException exp) {
			fail(JUnitMessages.Error.JUNIT_FAIL);
		}
	}

	/**
	 * Test method for
	 * {@link sg.edu.nus.iss.universitystore.data.TransactionManager#getTotal(java.util.ArrayList, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetTotal() {
		try {
			Assert.assertTrue(initializeManagerDetails());

			// Instantiate Discount Manager
			TransactionManager transactionManager = TransactionManager.getInstance();
			InventoryManager invantoryManager = InventoryManager.getInstance();
			DiscountManager discountManager = DiscountManager.getInstance();

			// Create Transaction Items List
			ArrayList<TransactionItem> arrTransactionItmLst = new ArrayList<>();
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID1), 1));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID2), 2));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID3), 3));

			Product product1 = invantoryManager.findProduct(productID1);
			Product product2 = invantoryManager.findProduct(productID2);
			Product product3 = invantoryManager.findProduct(productID3);

			float discountCode = discountManager.getDiscount(memberID).getPercentage();

			double totalPrice = product1.getPrice() * 1 + product2.getPrice() * 2 + product3.getPrice() * 3;
			float expectedTotalPrice = (float) (totalPrice * ((100 - discountCode) / 100));

			// Add Transaction Items to Transaction
			float actualTotal = transactionManager.getTotal(arrTransactionItmLst,
					discountManager.getDiscount(memberID).getCode());

			Assert.assertEquals(expectedTotalPrice, actualTotal, 0);

		} catch (TransactionException | InventoryException | DiscountException exp) {
			fail(JUnitMessages.Error.JUNIT_FAIL);
		}
	}

	/**
	 * Test method for
	 * {@link sg.edu.nus.iss.universitystore.data.TransactionManager#addTransaction(java.util.ArrayList, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testAddTransaction() {
		try {
			Assert.assertTrue(initializeManagerDetails());

			// Instantiate Discount Manager
			TransactionManager transactionManager = TransactionManager.getInstance();
			InventoryManager invantoryManager = InventoryManager.getInstance();
			DiscountManager discountManager = DiscountManager.getInstance();
			MemberManager memberManager = MemberManager.getInstance();

			// Create Transaction Items List
			ArrayList<TransactionItem> arrTransactionItmLst = new ArrayList<>();
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID1), 1));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID2), 2));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID3), 3));

			// Get Loyalty Points before transaction
			int beforeTransaction = memberManager.getMember(memberID).getLoyaltyPoints();
			int usedLoyaltyPoints = 20;

			// Add Transaction Items to Transaction
			Assert.assertTrue(transactionManager.addTransaction(arrTransactionItmLst,
					discountManager.getDiscount(memberID).getCode(), memberID, usedLoyaltyPoints));
			Assert.assertTrue(transactionManager.getTransactionReport().size() == 3);

			// Check if Product Quantities have been reduced after Transaction
			Assert.assertTrue(invantoryManager.findProduct(productID1).getQuantity() == (productQuantity1 - 1));
			Assert.assertTrue(invantoryManager.findProduct(productID2).getQuantity() == (productQuantity2 - 2));
			Assert.assertTrue(invantoryManager.findProduct(productID3).getQuantity() == (productQuantity3 - 3));

			// Get Total of Transaction
			float actualTotal = transactionManager.getTotal(arrTransactionItmLst,
					discountManager.getDiscount(memberID).getCode());
			// Get Gained points
			int gainedPoints = (int) actualTotal
					/ Constants.Data.Transaction.CURRENCY_TO_LOYALTY_POINTS_CONVERSION_RATE;
			// Get Points for member after transaction
			int afterTransaction = memberManager.getMember(memberID).getLoyaltyPoints();
			// Check Loyalty Points gained
			Assert.assertTrue((beforeTransaction - usedLoyaltyPoints + gainedPoints) == afterTransaction);
		} catch (TransactionException | InventoryException | DiscountException | MemberException exp) {
			fail(JUnitMessages.Error.JUNIT_FAIL);
		}
	}

	/**
	 * Test Invalid Member Exception in method for
	 * {@link sg.edu.nus.iss.universitystore.data.TransactionManager#addTransaction(java.util.ArrayList, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testAddTransactionInvalidMember() {
		try {
			Assert.assertTrue(initializeManagerDetails());

			// Instantiate Discount Manager
			TransactionManager transactionManager = TransactionManager.getInstance();
			InventoryManager invantoryManager = InventoryManager.getInstance();
			DiscountManager discountManager = DiscountManager.getInstance();

			// Create Transaction Items List
			ArrayList<TransactionItem> arrTransactionItmLst = new ArrayList<>();
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID1), 1));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID2), 2));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID3), 3));

			// Add Transaction Items to Transaction
			Assert.assertFalse(transactionManager.addTransaction(arrTransactionItmLst,
					discountManager.getDiscount(memberID).getCode(), invalidMemberID, 20));
			fail(JUnitMessages.Error.JUNIT_FAIL);
		} catch (TransactionException | InventoryException | DiscountException exp) {
			Assert.assertEquals(TransactionError.INVALID_MEMBER_ID.toString(), exp.getMessage());
		}
	}

	/**
	 * Test Invalid Member Exception in method for
	 * {@link sg.edu.nus.iss.universitystore.data.TransactionManager#addTransaction(java.util.ArrayList, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testAddTransactionInvalidDiscount() {
		try {
			Assert.assertTrue(initializeManagerDetails());

			// Instantiate Discount Manager
			TransactionManager transactionManager = TransactionManager.getInstance();
			InventoryManager invantoryManager = InventoryManager.getInstance();

			// Create Transaction Items List
			ArrayList<TransactionItem> arrTransactionItmLst = new ArrayList<>();
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID1), 1));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID2), 2));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID3), 3));

			// Add Transaction Items to Transaction
			Assert.assertFalse(
					transactionManager.addTransaction(arrTransactionItmLst, invalidDiscount, invalidMemberID, 20));
			fail(JUnitMessages.Error.JUNIT_FAIL);
		} catch (TransactionException | InventoryException exp) {
			Assert.assertEquals(TransactionError.INVALID_DISCOUNT_ID.toString(), exp.getMessage());
		}
	}

	/**
	 * Test Loyalty Point Exception method for
	 * {@link sg.edu.nus.iss.universitystore.data.TransactionManager#addTransaction(java.util.ArrayList, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testAddTransactionInvalidLoyalPoint() {
		try {
			Assert.assertTrue(initializeManagerDetails());

			// Instantiate Discount Manager
			TransactionManager transactionManager = TransactionManager.getInstance();
			InventoryManager invantoryManager = InventoryManager.getInstance();
			DiscountManager discountManager = DiscountManager.getInstance();

			// Create Transaction Items List
			ArrayList<TransactionItem> arrTransactionItmLst = new ArrayList<>();
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID1), 1));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID2), 2));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID3), 3));

			// Add Transaction Items to Transaction with invalid Loyalty Point
			// of 70
			Assert.assertFalse(transactionManager.addTransaction(arrTransactionItmLst,
					discountManager.getDiscount(memberID).getCode(), memberID, 70));
			fail(JUnitMessages.Error.JUNIT_FAIL);
		} catch (TransactionException | InventoryException | DiscountException exp) {
			Assert.assertEquals(TransactionError.INVALID_LOYALTY_POINTS_APPLIED.toString(), exp.getMessage());
		}
	}

	/**
	 * Test Large Quantity Exception method for
	 * {@link sg.edu.nus.iss.universitystore.data.TransactionManager#addTransaction(java.util.ArrayList, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testAddTransactionLargeQuantity() {
		try {
			Assert.assertTrue(initializeManagerDetails());

			// Instantiate Discount Manager
			TransactionManager transactionManager = TransactionManager.getInstance();
			InventoryManager invantoryManager = InventoryManager.getInstance();
			DiscountManager discountManager = DiscountManager.getInstance();

			// Create Transaction Items List
			ArrayList<TransactionItem> arrTransactionItmLst = new ArrayList<>();
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID1), 1));
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID2), 2));
			// Added 1000 quantity
			arrTransactionItmLst.add(new TransactionItem(invantoryManager.findProduct(productID3), 1000));

			// Add Transaction Items to Transaction
			Assert.assertFalse(transactionManager.addTransaction(arrTransactionItmLst,
					discountManager.getDiscount(memberID).getCode(), memberID, 20));
			fail(JUnitMessages.Error.JUNIT_FAIL);
		} catch (TransactionException | InventoryException | DiscountException exp) {
			Assert.assertEquals(TransactionError.REQUESTED_QUANTITY_MORE_THAN_AVAILABLE.toString(), exp.getMessage());
		}
	}

}
