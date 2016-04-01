package sg.edu.nus.iss.universitystore.constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Constants {

	public static final class DateTime {
		public static final LocalDate CURRENT_DATE = LocalDate.now();
	}

	public static final class Common {
		public static final String EMPTY_STR = "";
		public static final String NEW_LINE = "\n";

		public static final class Pattern {
			public static final String NUMBER_MATCH = "\\d+";
			public static final String FLOAT_MATCH = "\\d+(\\.\\d+)?";
			public static final String DATE_MATCH = "\\d{4}\\-\\d{2}\\-\\d{2}";
			public static final String CODE_MATCH = "[A-Z_]+";
			public static final String NAME_MATCH = "[a-zA-Z0-9 ]+";
		}

		public static final DateTimeFormatter YYYY_MM_DD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	public static final class Data {

		// TODO: Is this the best solution
		public enum FILE_FOLDER {
			TEST, DATA;
		}

		// TODO: Come up with better solution
		public static final String FILE_PATH_SEPTR = System.getProperty("os.name").toUpperCase().startsWith("WIN")
				? "\\" : "//";
		public static final String FILE_EXT = ".dat";
		public static final String FILE_SEPTR = ",";
		public static final String FILE_FLDR = "data";
		public static final String FILE_PATH = System.getProperty("user.dir") + FILE_PATH_SEPTR
				+ FILE_FOLDER.DATA.toString().toLowerCase() + FILE_PATH_SEPTR;

		public static final String ID_SEPTR = "/";

		public static final String TEST_FILE_PATH = System.getProperty("user.dir") + FILE_PATH_SEPTR
				+ FILE_FOLDER.TEST.toString().toLowerCase() + FILE_PATH_SEPTR
				+ FILE_FOLDER.DATA.toString().toLowerCase() + FILE_PATH_SEPTR;

		public static final class FileName {
			public static final String CATEGORY_DAT = "Category";
			public static final String DISCOUNT_DAT = "Discount";
			public static final String MEMBER_DAT = "Member";
			public static final String PRODUCT_DAT = "Product";
			public static final String STORE_KEEPER_DAT = "StoreKeeper";
			public static final String TRANSACTION_DAT = "Transaction";
			public static final String VENDOR_DAT = "Vendor";

		}

		public static final class Pattern {
			public static final String FILE_SEPTR_MATCH = "\\" + FILE_SEPTR;
			public static final String DESCRIPTION_MATCH = "(\\\"(?!\\\")(.*?)\\\"" + FILE_SEPTR_MATCH + ")";
		}

		public static final class Category {
			public static final class Pattern {
				public static final String CODE_MATCH = "[A-Z]{3}";
				public static final String NAME_MATCH = Common.Pattern.NAME_MATCH;
				public static final String LINE_MATCH = "^" + CODE_MATCH + Data.Pattern.FILE_SEPTR_MATCH + NAME_MATCH
						+ "$";
			}
		}

		public static final class Product {
			public static final class Pattern {
				public static final String ID_MATCH = "(\\w+)" + ID_SEPTR + "(\\d+)";
				public static final String CATEGORY_REPLACE = "$1";
				public static final String COUNT_REPLACE = "$2";
				public static final String NAME_MATCH = Common.Pattern.NAME_MATCH;
				public static final String QUANTITY_MATCH = Common.Pattern.NUMBER_MATCH;
				public static final String PRICE_MATCH = Common.Pattern.FLOAT_MATCH;
				public static final String REORDER_THRESHOLD_MATCH = Common.Pattern.NUMBER_MATCH;
				public static final String REORDER_QUANTITY_MATCH = Common.Pattern.NUMBER_MATCH;
				public static final String LINE_MATCH = "^(" + ID_MATCH + Data.Pattern.FILE_SEPTR_MATCH + NAME_MATCH
						+ Data.Pattern.FILE_SEPTR_MATCH + ")" + Data.Pattern.DESCRIPTION_MATCH + "(" + QUANTITY_MATCH
						+ Data.Pattern.FILE_SEPTR_MATCH + PRICE_MATCH + Data.Pattern.FILE_SEPTR_MATCH
						+ REORDER_THRESHOLD_MATCH + Data.Pattern.FILE_SEPTR_MATCH + REORDER_QUANTITY_MATCH + ")$";
				public static final String DESCRIPTION_REPLACE = "$5";
				public static final String OTHER_CNTNT_REPLACE = "$1$6";
			}
			public static final int PRODUCT_ZERO = 0;
			public static final int DATA_SPLT_LENGTH = 7;
			public static final int INITIALIZED_COUNT = 0;
		}

		public static final class Member {
			public static final int LOYALTY_NEW_MEMBER = -1;
			public static final class Pattern {
				public static final String MEMBER_ID_MATCH = "^[a-zA-Z0-9]+$";
				public static final String LOYALTY_POINTS_MATCH = "^((-1)|\\d+)$";
			}
		}

		public static final class Discount {
			public static final String ALWAYS = "ALWAYS";
			public static final int ALWAYS_VAL = -1;
			public static final class Pattern {
				public static final String CODE_MATCH = Common.Pattern.CODE_MATCH;
				public static final String START_DATE_MATCH = "((" + ALWAYS + ")|\\d{4}-\\d{2}-\\d{2})";
				public static final String PERIOD_MATCH = "((" + ALWAYS + ")|" + Common.Pattern.NUMBER_MATCH + ")";
				public static final String PERCENTAGE_MATCH = "\\d{1,3}(\\.\\d{1,2})?";
				public static final String ELIGIBILITY_MATCH = "[A|M]";
				public static final String LINE_MATCH = "^(" + CODE_MATCH + Data.Pattern.FILE_SEPTR_MATCH + ")"
						+ Data.Pattern.DESCRIPTION_MATCH + "(" + START_DATE_MATCH + Data.Pattern.FILE_SEPTR_MATCH
						+ PERIOD_MATCH + Data.Pattern.FILE_SEPTR_MATCH + PERCENTAGE_MATCH
						+ Data.Pattern.FILE_SEPTR_MATCH + ELIGIBILITY_MATCH + ")$";
				public static final String DESCRIPTION_REPLACE = "$3";
				public static final String OTHER_CNTNT_REPLACE = "$1$4";
			}

			public static final class Eligibility {
				public static final String MEMBER = "M";
				public static final String ALL = "A";
			}

			public static final class Member {
				public static final class New {
					public static final String CODE = "MEMBER_FIRST";
				}

				public static final class Existing {
					public static final String CODE = "MEMBER_SUBSEQ";
				}

				public static final class Public {
					public static final String CODE = "PUBLIC";
					public static final String DESCRIPTION = "No Discount applicable for Non-Member";
					public static final int PERIOD = 0;
					public static final float DEFAULT_DISCOUNT = 0;
				}
			}
		}
	}

	public static final class TableData {
		// Common
		public static final int FIRST_COLUMN = 0;
		public static final int SECOND_COLUMN = 1;
		public static final int THIRD_COLUMN = 2;
		public static final int FOURTH_COLUMN = 3;
		public static final int FIFTH_COLUMN = 4;
		public static final int SIXTH_COLUMN = 5;
		public static final int SEVENTH_COLUMN = 6;

		// Category
		public static final int NUMBER_OF_CATEGORY_TABLE_COLUMNS = 2;
		public static final String STR_CATEGORY_CODE = "Code";
		public static final String STR_CATEGORY_NAME = "Name";

		// Product
		public static final int NUMBER_OF_PRODUCT_TABLE_COLUMNS = 7;
		public static final String STR_PRODUCT_CODE = "Code";
		public static final String STR_PRODUCT_NAME = "Name";
		public static final String STR_PRODUCT_DESCRIPTION = "Description";
		public static final String STR_PRODUCT_QUANTITY = "Quantity";
		public static final String STR_PRODUCT_PRICE = "Price";
		public static final String STR_PRODUCT_REORDER_THRESHOLD = "Reorder Threshold";
		public static final String STR_PRODUCT_REORDER_QUANTITY = "Reorder Quantity";

		// Discount
		public static final int NUMBER_OF_DISCOUNT_TABLE_COLUMNS = 6;
		public static final String STR_DISCOUNT_CODE = "Code";
		public static final String STR_DISCOUNT_DESCRIPTION = "Description";
		public static final String STR_DISCOUNT_START_DATE = "Start Date";
		public static final String STR_DISCOUNT_PERIOD = "Period";
		public static final String STR_DISCOUNT_PERCENTAGE = "Rate";
		public static final String STR_DISCOUNT_ELIGIBILITY = "Eligibility";

		// Member
		public static final int NUMBER_OF_MEMBER_TABLE_COLUMNS = 3;
		public static final String STR_MEMBER_IDENTIFIER = "Identifier";
		public static final String STR_MEMBER_NAME = "Name";
		public static final String STR_MEMBER_LOYALTY_POINTS = "Loyalty Points";
		// TransactionITEM
		public static final int NUMBER_OF_TRANSACTION_ITEM_TABLE_COLUMNS = 6;
		public static final String STR_TRANSACTIONITEM_PRODUCT_IDENTIFIER = "Product Id";
		public static final String STR_TRANSACTIONITEM_PRODUCT_NAME = "Product Name";
		public static final String STR_TRANSACTIONITEM_PURCHASE_QUANTITY = "Purchased Quantity";
		public static final String STR_TRANSACTIONITEM_PRICE = "Price";
		public static final String STR_TRANSACTIONITEM_TOTAL = "Total";
		// Transaction
		public static final int NUMBER_OF_TRANSACTION_TABLE_COLUMNS = 4;
		public static final String STR_TRANSACTION_PRODUCT_IDENTIFIER = "Product Id";
		public static final String STR_TRANSACTION_PRODUCT_NAME = "Product Name";
		public static final String STR_TRANSACTION_PRODUCT_DESCRIPTION = "Product Description";
		public static final String STR_TRANSACTION_MEMBER_IDENTIFIER = "Member Id";
		public static final String STR_TRANSACTION_PURCHASE_QUANTITY = "Purchased Quantity";
		public static final String STR_TRANSACTION_DATE = "Date";
	}
}
