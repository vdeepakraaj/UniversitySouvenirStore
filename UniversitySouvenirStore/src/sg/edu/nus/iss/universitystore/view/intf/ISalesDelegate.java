package sg.edu.nus.iss.universitystore.view.intf;

public interface ISalesDelegate {
	
	/**
	 * click add product btn from the view
	 */
	public void AddProduct();
	/**
	 * click check out btn form the view
	 */
	public void CheckOut();
	/**
	 * click cancel btn form the view
	 */
	public void Cancel();
	/**
	 * click member btn form the view
	 */
	public void LoginMember();
	/**
	 * click discount btn form the view,can be made to drop-down box 
	 */
	public void ChoseDiscount();
	
}