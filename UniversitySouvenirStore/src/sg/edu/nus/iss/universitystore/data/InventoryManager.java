package sg.edu.nus.iss.universitystore.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.nus.iss.universitystore.constants.Constants;
import sg.edu.nus.iss.universitystore.exception.InventoryException;
import sg.edu.nus.iss.universitystore.exception.InventoryException.InventoryError;
import sg.edu.nus.iss.universitystore.model.Category;
import sg.edu.nus.iss.universitystore.model.Product;
import sg.edu.nus.iss.universitystore.model.Vendor;
import sg.edu.nus.iss.universitystore.utility.DateUtils;
import sg.edu.nus.iss.universitystore.validation.InventoryValidation;

/**
 * Manager Class used to handle Inventory
 * 
 * @author Sanjay
 *
 */
public class InventoryManager {
	
	/***********************************************************/
	// Enums
	/***********************************************************/

	/**
	 * Category Arguments
	 */
	public enum CategoryArg {
		CODE(0), NAME(1);

		private int position;

		private CategoryArg(int position) {
			this.position = position;
		}
	}

	/**
	 * Product Arguments
	 */
	public enum ProductArg {
		IDENTIFIER(0), NAME(1), QUANTITY(2), PRICE(3), BAR_CODE(4), REORDERTHRESHOLD(5), REORDERQUANTITY(
				6), DESCRIPTION(7);

		private int position;

		private ProductArg(int position) {
			this.position = position;
		}
	}

	/**
	 * Vendor Arguments
	 */
	public enum VendorArg {
		NAME(0), DECRIPTION(1);

		private int position;

		private VendorArg(int position) {
			this.position = position;
		}
	}

	/***********************************************************/
	// Instance Variables
	/***********************************************************/

	/**
	 * Instance of Inventory Manager
	 */
	private static InventoryManager instance;

	/**
	 * Product ID which is generated
	 */
	private static Integer productID;

	/**
	 * Category Data
	 */
	private DataFile<Category> categoryData;
	/**
	 * Product Data
	 */
	private DataFile<Product> productData;
	/**
	 * Vendor Map
	 */
	private HashMap<String, DataFile<Vendor>> vendorMap;

	/***********************************************************/
	// Singleton
	/***********************************************************/

	/**
	 * Get a single instance of Data File Manager
	 * 
	 * @return DataFileManager
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public static InventoryManager getInstance() throws InventoryException {
		if (instance == null) {
			synchronized (InventoryManager.class) {
				if (instance == null) {
					instance = new InventoryManager();
				}
			}
		}
		return instance;
	}

	/**
	 * Delete instance of Data File Manager
	 */
	public static void deleteInstance() {
		instance = null;
	}

	/***********************************************************/
	// Constructors
	/***********************************************************/

	/**
	 * Inventory Manager Constructor
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public InventoryManager() throws InventoryException {
		productID = (productID == null) ? Constants.Data.Product.INITIALIZED_COUNT : productID;
		initialize();
	}

	/***********************************************************/
	// Private Methods for Constructors
	/***********************************************************/

	/**
	 * Initialize Data used for inventory Category, Product and Vendor
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	private void initialize() throws InventoryException {
		try {
			categoryData = new DataFile<>(Constants.Data.FileName.CATEGORY_DAT);
			productData = new DataFile<>(Constants.Data.FileName.PRODUCT_DAT);
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}

		initializeVendors();
	}

	/**
	 * Initializes Data for Vendor
	 * 
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	private void initializeVendors() throws InventoryException {
		vendorMap = new HashMap<>();

		// Get all categories list
		ArrayList<Category> categoriesList = getAllCategories();

		for (Category category : categoriesList) {
			addVendorDataFile(category.getCode());
		}

	}

	/***********************************************************/
	// Public Methods for Category
	/***********************************************************/

	/**
	 * Get All Categories from Data File
	 * 
	 * @return
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public ArrayList<Category> getAllCategories() throws InventoryException {
		ArrayList<Category> categoryList = new ArrayList<>();
		String[] categoriesStrList;
		try {
			categoriesStrList = categoryData.getAll();
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}

		for (String categoryStr : categoriesStrList) {

			String[] categoryStrSplt = categoryStr.split(Constants.Data.FILE_SEPTR);

			// If line in Data file is empty, skip line
			if (!isValidCategoryData(categoryStrSplt))
				continue;

			categoryList.add(new Category(categoryStrSplt[CategoryArg.CODE.ordinal()],
					categoryStrSplt[CategoryArg.NAME.ordinal()]));
		}

		return categoryList;
	}

	/**
	 * Add new Category Add new Vendor for new Category
	 * 
	 * @param category
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public boolean addCategory(String categoryCode, String categoryName) throws InventoryException {
		if (!InventoryValidation.Catgory.isValidData(categoryCode, categoryName))
			return false;

		Category category = new Category(categoryCode, categoryName);

		// Check if category code already exists
		if (hasCategory(categoryCode)) {
			throw new InventoryException(InventoryError.CATEGORY_ALREADY_PRESENT);
		} else {
			try {
				categoryData.add(category);
			} catch (IOException ioExp) {
				throw new InventoryException(InventoryError.UNKNOWN_ERROR);
			}

			// Add Vendor Data file
			addVendorDataFile(category.getCode());
		}

		return true;
	}

	/**
	 * Delete a Category
	 * 
	 * @param category
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public boolean deleteCategory(String categoryCode) throws InventoryException {
		boolean status = false;

		if (hasCategory(categoryCode)) {
			Category category = findCategory(categoryCode);

			try {
				if (deleteAllVendors(categoryCode)) {
					status = categoryData.delete(category.toString());
				}
			} catch (IOException ioExp) {
				throw new InventoryException(InventoryError.UNKNOWN_ERROR);
			}
		}

		return status;
	}

	/**
	 * Update a Category
	 * 
	 * @param oldCategory
	 *            The category object that needs to be updated.
	 * @param updatedCategory
	 *            The new category object.
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public void updateCategory(Category oldCategory, Category updatedCategory) throws InventoryException {
		// Check if the category exists
		if (hasCategory(oldCategory.getCode())) {
			// Get existing Vendor list
			ArrayList<Vendor> existingVendorLst = getAllVendors(oldCategory.getCode());
			// First, delete the category
			deleteCategory(oldCategory.getCode());
			// Next up, add the new category
			addCategory(updatedCategory.getCode(), updatedCategory.getName());
			// Add Vendors to new category
			for (Vendor vendor : existingVendorLst) {
				addVendor(updatedCategory.getCode(), vendor.getName(), vendor.getDescription());
			}
		}
	}

	/**
	 * Find Category
	 * 
	 * @param categoryCode
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public Category findCategory(String categoryCode) throws InventoryException {
		ArrayList<Category> categoryList = getAllCategories();
		Category categoryFound = null;

		for (Category category : categoryList) {
			if (category.getCode().equals(categoryCode))
				categoryFound = category;
		}

		return categoryFound;
	}

	/**
	 * Checks if Category exists
	 * 
	 * @param categoryCode
	 * @return
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public boolean hasCategory(String categoryCode) throws InventoryException {
		return InventoryValidation.Catgory.isValidCatgoryCode(categoryCode) && findCategory(categoryCode) != null;
	}

	/**
	 * Checks if category content is valid
	 * 
	 * @param categoryList
	 * @return Boolean
	 * @throws InventoryException
	 */
	private boolean isValidCategoryData(String[] categoryList) {
		boolean status = false;
		try {
			if (categoryList.length == 2 && InventoryValidation.Catgory
					.isValidData(categoryList[CategoryArg.CODE.ordinal()], categoryList[CategoryArg.NAME.ordinal()])) {
				status = true;
			}
		} catch (InventoryException e) {
			status = false;
		}
		return status;
	}

	/***********************************************************/
	// Public Methods for Product
	/***********************************************************/

	/**
	 * Get All Products from Data File
	 * 
	 * @return List of Products
	 * @throws IOException
	 */
	public ArrayList<Product> getAllProducts() throws InventoryException {
		ArrayList<Product> productList = new ArrayList<>();
		String[] productStrList;
		try {
			productStrList = productData.getAll();
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}

		for (String productStr : productStrList) {

			String[] productStrSpltLst = splitProductData(productStr);

			// If line in Data file is empty, skip line
			if (!isValidProductData(productStrSpltLst))
				continue;

			productList.add(new Product(productStrSpltLst[ProductArg.IDENTIFIER.ordinal()],
					productStrSpltLst[ProductArg.NAME.ordinal()], productStrSpltLst[ProductArg.DESCRIPTION.ordinal()],
					productStrSpltLst[ProductArg.QUANTITY.ordinal()], productStrSpltLst[ProductArg.PRICE.ordinal()],
					productStrSpltLst[ProductArg.BAR_CODE.ordinal()],
					productStrSpltLst[ProductArg.REORDERTHRESHOLD.ordinal()],
					productStrSpltLst[ProductArg.REORDERQUANTITY.ordinal()]));
		}

		return productList;
	}

	/**
	 * Add a Product
	 * 
	 * @param categoryCode
	 * @param name
	 * @param description
	 * @param quantity
	 * @param price
	 * @param barCode
	 * @param reorderThreshold
	 * @param reorderQuantity
	 * @return Product
	 * @throws InventoryException
	 */
	public Product addProduct(String categoryCode, String name, String description, String quantity, String price,
			String barCode, String reorderThreshold, String reorderQuantity) throws InventoryException {

		if (!hasCategory(categoryCode))
			return null;

		if (productBarCodeExists(barCode))
			throw new InventoryException(InventoryError.PRODUCT_BAR_CODE_EXISTS);

		StringBuffer productID = new StringBuffer();
		productID.append(categoryCode);
		productID.append(Constants.Data.ID_SEPTR);
		productID.append(getProductId(categoryCode));

		Product product = new Product(productID.toString(), name, description, quantity, price, barCode,
				reorderThreshold, reorderQuantity);

		try {
			return productData.add(product) ? product : null;
		} catch (IOException inExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}
	}

	/**
	 * Find Product
	 * 
	 * @param productID
	 * @return Product, null if not found
	 * @throws InventoryException
	 */
	public Product findProduct(String productID) throws InventoryException {
		ArrayList<Product> productList = getAllProducts();

		Product productFound = null;

		for (Product product : productList) {
			if (product.getIdentifier().equals(productID)) {
				productFound = product;
				break;
			}
		}

		return productFound;
	}

	/**
	 * Find Product by BarCode
	 * 
	 * @param barCode
	 * @return Product, null if not found
	 * @throws InventoryException
	 */
	public Product findProductByBarCode(String barCode) throws InventoryException {
		ArrayList<Product> productList = getAllProducts();

		Product productFound = null;

		for (Product product : productList) {
			if (product.getBarCode().equals(barCode)) {
				productFound = product;
				break;
			}
		}

		return productFound;
	}

	/**
	 * Checks if BarCode already exists
	 * 
	 * @param barCode
	 * @return Boolean
	 * @throws InventoryException
	 */
	public boolean productBarCodeExists(String barCode) throws InventoryException {
		return findProductByBarCode(barCode) != null;
	}

	/**
	 * Check if the product id entered is valid.
	 * 
	 * @param productID
	 * @return Boolean
	 * @throws IOException
	 * @throws StoreException
	 * @throws InventoryException
	 */
	public boolean isValidProduct(String productID) throws InventoryException {
		boolean status = false;

		String categoryCode = productID.replaceAll(Constants.Data.Product.Pattern.ID_MATCH,
				Constants.Data.Product.Pattern.CATEGORY_REPLACE);
		if (hasCategory(categoryCode)) {
			status = findProduct(productID) != null;
		}

		return status;
	}

	/**
	 * Delete a Product from the store
	 * 
	 * @param product
	 * @return
	 * @throws InventoryException
	 */
	public boolean deleteProduct(Product product) throws InventoryException {
		boolean status = false;

		if (!isValidProduct(product.getIdentifier()))
			throw new InventoryException(InventoryError.PRODUCT_NOT_AVAILABLE);

		product = findProduct(product.getIdentifier());
		try {
			if (productData.delete(product.toString())) {
				status = true;
			}
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}

		return status;
	}

	/**
	 * Update details of the product
	 * 
	 * @param newProduct
	 * @return
	 * @throws InventoryException
	 */
	public boolean updateProduct(Product newProduct) throws InventoryException {
		boolean status = false;

		if (!isValidProduct(newProduct.getIdentifier()))
			throw new InventoryException(InventoryError.PRODUCT_NOT_AVAILABLE);

		if (productBarCodeExists(newProduct.getBarCode()))
			throw new InventoryException(InventoryError.PRODUCT_BAR_CODE_EXISTS);

		Product existingProduct = findProduct(newProduct.getIdentifier());
		if (deleteProduct(existingProduct)) {
			try {
				status = productData.add(newProduct);
			} catch (IOException ioExp) {
				throw new InventoryException(InventoryError.UNKNOWN_ERROR);
			}
		}

		return status;
	}

	/**
	 * Update details of the product
	 * 
	 * @param newProduct
	 * @return
	 * @throws InventoryException
	 */
	public boolean updateProductForTransaction(Product newProduct) throws InventoryException {
		boolean status = false;

		if (!isValidProduct(newProduct.getIdentifier()))
			throw new InventoryException(InventoryError.PRODUCT_NOT_AVAILABLE);

		Product existingProduct = findProduct(newProduct.getIdentifier());
		if (deleteProduct(existingProduct)) {
			try {
				status = productData.add(newProduct);
			} catch (IOException ioExp) {
				throw new InventoryException(InventoryError.UNKNOWN_ERROR);
			}
		}

		return status;
	}

	/**
	 * Get List of Products below Threshold
	 * 
	 * @return List of Products
	 * @throws InventoryException
	 */
	public ArrayList<Product> getProductsBelowThreshold() throws InventoryException {
		ArrayList<Product> productList = getAllProducts();
		if (productList.size() == Constants.Data.Product.PRODUCT_ZERO)
			throw new InventoryException(InventoryError.PRODUCT_ZERO);

		// List of Products below Threshold
		ArrayList<Product> blwTheshProdList = new ArrayList<>();

		for (Product product : productList) {
			// Check if Quantity is less than Reorder Threshold
			if (product.getQuantity() <= product.getReorderThreshold())
				blwTheshProdList.add(product);
		}

		return blwTheshProdList;
	}

	/**
	 * Checks if Data is Valid
	 * 
	 * @param productList
	 * @return Boolean
	 */
	public boolean isValidProductData(String[] productList) {
		boolean status = false;

		try {
			if (productList.length == Constants.Data.Product.DATA_SPLT_LENGTH
					&& InventoryValidation.Product.isValidData(productList[ProductArg.NAME.ordinal()],
							productList[ProductArg.DESCRIPTION.ordinal()], productList[ProductArg.QUANTITY.ordinal()],
							productList[ProductArg.PRICE.ordinal()], productList[ProductArg.BAR_CODE.ordinal()],
							productList[ProductArg.REORDERTHRESHOLD.ordinal()],
							productList[ProductArg.REORDERQUANTITY.ordinal()])) {
				String categoryCode = productList[ProductArg.IDENTIFIER.ordinal()].replaceAll(
						Constants.Data.Product.Pattern.ID_MATCH, Constants.Data.Product.Pattern.CATEGORY_REPLACE);
				status = hasCategory(categoryCode);
			}
		} catch (InventoryException inventoryExp) {
			status = false;
		}

		return status;
	}

	/**
	 * Splits Row of Data File into a list of Strings
	 * 
	 * @param line
	 * @return Boolean
	 */
	private String[] splitProductData(String productRow) {

		return DateUtils.extractContent(productRow, Constants.Data.Product.Pattern.LINE_MATCH,
				Constants.Data.Product.Pattern.DESCRIPTION_REPLACE, Constants.Data.Product.Pattern.OTHER_CNTNT_REPLACE);
	}

	/***********************************************************/
	// Private Methods for Product
	/***********************************************************/

	/**
	 * Method to get the product Id that will be assigned to the new product.
	 * 
	 * @param The
	 *            category code to which the product belongs to.
	 * @return The product Id that will be assigned to the new product.
	 */
	private int getProductId(String categoryCode) throws InventoryException {
		// Initialize the return value
		int productId = 0;

		// Check the category code in file
		// Get all categories
		ArrayList<Product> arrProducts = getAllProducts();

		for (Product product : arrProducts) {
			String[] parts = product.getIdentifier().split(Constants.Data.ID_SEPTR);
			// If found, then check if the current value is greater than found
			// value
			if (parts[0].equals(categoryCode)) {
				int parsedValue = Integer.parseInt(parts[1]);
				if (productId < parsedValue) {
					productId = parsedValue;
				}
			}
		}

		// Add one to the new product.
		return ++productId;
	}
	/***********************************************************/
	// Public Methods for Vendor
	/***********************************************************/

	/**
	 * Get All Vendors from Data File
	 * 
	 * @return List of Vendors
	 * @throws IOException
	 */
	public ArrayList<Vendor> getAllVendors(String categoryCode) throws InventoryException {
		ArrayList<Vendor> vendorList = new ArrayList<>();
		DataFile<Vendor> vendorData = vendorMap.get(categoryCode);
		String[] vendorStrList;
		try {
			vendorStrList = vendorData.getAll();
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}

		for (String vendorStr : vendorStrList) {

			// If line in Data file is empty, skip line
			if (vendorStr.isEmpty())
				continue;

			String[] vendorStrSplt = vendorStr.split(Constants.Data.FILE_SEPTR);

			vendorList.add(
					new Vendor(vendorStrSplt[VendorArg.NAME.ordinal()], vendorStrSplt[VendorArg.DECRIPTION.ordinal()]));
		}

		return vendorList;
	}

	/**
	 * Add New Vendor Data File
	 * 
	 * @param categoryCode
	 * @throws InventoryException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void addVendorDataFile(String categoryCode) throws InventoryException {
		try {
			vendorMap.put(categoryCode, new DataFile<Vendor>(Constants.Data.FileName.VENDOR_DAT + categoryCode));
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}
	}

	/**
	 * Add a Vendor
	 * 
	 * @param categoryCode
	 * @param vendorName
	 * @param vendorDescription
	 * @return Status
	 * @throws IOException
	 */
	public boolean addVendor(String categoryCode, String vendorName, String vendorDescription)
			throws InventoryException {
		if (!hasCategory(categoryCode)) {
			throw new InventoryException(InventoryError.CATEGORY_NOT_AVAILABLE);
		}

		DataFile<Vendor> vendorList = vendorMap.get(categoryCode);
		if (vendorList == null) {
			throw new InventoryException(InventoryError.CATEGORY_UNKNOWN_ERROR);
		}

		try {
			return vendorList.add(new Vendor(vendorName, vendorDescription));
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}
	}

	/**
	 * Delete all Vendors
	 * 
	 * @param categoryCode
	 * @return
	 * @throws InventoryException
	 */
	public boolean deleteAllVendors(String categoryCode) throws InventoryException {
		boolean status = false;
		if (!hasCategory(categoryCode)) {
			throw new InventoryException(InventoryError.CATEGORY_NOT_AVAILABLE);
		}

		DataFile<Vendor> vendorData = vendorMap.get(categoryCode);

		if (vendorData != null) {
			status = vendorData.delete() && vendorMap.remove(categoryCode, vendorData);
		}

		return status;
	}

	/**
	 * Delete a Vendor from a category
	 * 
	 * @param categoryCode
	 * @param vendorName
	 * @return
	 * @throws IOException
	 */
	public boolean deleteVendor(String categoryCode, String vendorName) throws InventoryException {
		boolean status = false;
		if (!hasCategory(categoryCode)) {
			throw new InventoryException(InventoryError.CATEGORY_NOT_AVAILABLE);
		}

		DataFile<Vendor> vendorData = vendorMap.get(categoryCode);
		Vendor vendor = findVendor(categoryCode, vendorName);
		if (vendorData == null || vendor == null) {
			throw new InventoryException(InventoryError.CATEGORY_UNKNOWN_ERROR);
		}

		try {
			status = vendorData.delete(vendor.toString());
		} catch (IOException ioExp) {
			throw new InventoryException(InventoryError.UNKNOWN_ERROR);
		}

		return status;
	}

	/**
	 * Find Vendor
	 * 
	 * @param categoryCode
	 * @param vendorName
	 * @return Vendor as per Category Code and Vendor Name, returns null if not
	 *         found.
	 * @throws InventoryException
	 */
	public Vendor findVendor(String categoryCode, String vendorName) throws InventoryException {
		ArrayList<Vendor> vendorList = getAllVendors(categoryCode);
		Vendor vendorFound = null;

		for (Vendor vendor : vendorList) {
			if (vendor.getName().equals(categoryCode)) {
				vendorFound = vendor;
				break;
			}
		}

		return vendorFound;
	}

	/**
	 * Get Vendors based on Product
	 * 
	 * @param product
	 * @return List of Vendors
	 * @throws IOException
	 */
	public ArrayList<Vendor> getVendorBasedOnProduct(Product product) throws InventoryException {
		ArrayList<Vendor> vendorList = new ArrayList<>();
		// TODO: GOOD to have a check if category is valid

		String categoryCode = product.getIdentifier().split(Constants.Data.ID_SEPTR)[0];

		return getAllVendors(categoryCode);
	}

}
