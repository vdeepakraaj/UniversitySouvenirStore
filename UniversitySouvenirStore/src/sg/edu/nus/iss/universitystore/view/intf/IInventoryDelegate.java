/**
 * 
 */
package sg.edu.nus.iss.universitystore.view.intf;

/**
 * @author Samrat
 *
 */
public interface IInventoryDelegate {

	/***********************************************************/
	// Common
	/***********************************************************/
	/**
	 * Method will be called if the edit/delete are tapped without selecting a row of the table.
	 */
	public void rowNotSelected();
	
	/***********************************************************/
	// CRUD operations for Category
	/***********************************************************/
	
	/**
	 * Method to add a category.
	 */
	public void addCategoryClicked();
	
	/**
	 * Edit a category.
	 */
	public void editCategoryClicked(int index);
	
	/**
	 * Delete a category.
	 */
	public void deleteCategoryClicked(int index);
	
	/***********************************************************/
	// CRUD operations for Product
	/***********************************************************/
	
	/**
	 * Method to add a category.
	 */
	public void addProductClicked();
	
	/**
	 * Edit a category.
	 */
	public void editProductClicked(int index);
	
	/**
	 * Delete a category.
	 */
	public void deleteProductClicked(int index);
}
