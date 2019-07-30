package com.nikvay.cnp_master.common;

/**
 * Created by callidus on 1/11/17.
 */

public class ServerConstants {


    public static final String URL = "url";


    public class serverUrl {

        //public static final String BASE_URL = "http://www.nikvay.com/admin/";
        public static final String BASE_URL ="http://cnpmasterapp.com/admin/"; //Live Url
       // public static final String BASE_URL = "http://www.cnpindiaapp.com/admin_new/";
        public static final String IMAGE_BASE_URL = BASE_URL + "media/user-profile-pictures/";
        public static final String LOGIN = BASE_URL + "ws-login";
        public static final String CHANGE_PASSWORD = BASE_URL + "ws-change_password";
        public static final String USER_INFO = BASE_URL + "ws-view-userprofile";
        public static final String UPDATE_USER_INFO = BASE_URL + "ws-edit-userprofile";
        //public static final String UPLOAD_PROFILE_PIC = BASE_URL + "ws-update-profile-pic";

        public static final String PRICE_LIST = BASE_URL + "ws-get-price-list";
        public static final String PRICE_SEARCH = BASE_URL + "ws-price-search";

        public static final String STOCK_LIST = BASE_URL + "ws-get-stock-list";
        public static final String STOCK_DETAILS = BASE_URL + "ws-get-stock-details";
        public static final String STOCK_SEARCH = BASE_URL + "ws-stock-search";
        public static final String MY_CUSTOMER_LIST = BASE_URL + "ws-customer-list";
        public static final String ADD_MY_CUSTOMER_LIST = BASE_URL + "ws-customer-registration";
        public static final String ADD_VISITS = BASE_URL + "ws-add-visits";
        public static final String COLLECTION = BASE_URL + "ws-collection-amount";
        public static final String VIEW_COLLECTION = BASE_URL + "ws-list-collection";
        public static final String VIEW_VISITS = BASE_URL + "ws-list-visits";
        public static final String BRANCH_LIST = BASE_URL + "ws-branch-list";
        public static final String GET_PRODUCT = BASE_URL + "ws-get-product-list";
        public static final String ADD_QUOTATION = BASE_URL + "ws-add-quotation";
        public static final String QUOTATION_LIST = BASE_URL + "ws-list-quotation";
        public static final String VIEW_QUOTATION = BASE_URL + "ws-view-quotation";
        public static final String CHANGE_STATUS = BASE_URL + "ws-change-status";


        public static final String quote_summary = BASE_URL + "ws-update-summery";


        public static final String SEND_MAIL = BASE_URL + "ws-send-quote-mail";
        public static final String GET_OUTSTANDING_AMOUNT = BASE_URL + "ws-get-outstanding-amount";
        public static final String GET_OUTSTANDING_RECEIPT = BASE_URL + "ws-get-recept";
        public static final String POST_OUTSTANDING_MAIL = BASE_URL + "ws-send-outstanding-mail";
        public static final String POST_PRODUCT_TYPE = BASE_URL + "ws-get-product-type";
        public static final String UPDATE_DISCOUNT = BASE_URL + "ws-update-quote-discount";
        public static final String UPDATE_QUANTITY = BASE_URL + "ws-update-quote-product_qty";
        public static final String DELETE_PRODUCT = BASE_URL + "ws-delete-quote-product";
        public static final String UPDATE_ADDRESS = BASE_URL + "ws-update-quote-address";
        public static final String UPDATE_CUSTOMER = BASE_URL + "ws-customer-update";
        public static final String ADD_HO_DETAIL = BASE_URL + "ws-add-ho-visits";
        public static final String SEND_PI = BASE_URL + "ws-send-pi";


        public static final String MARK_ATTENDANCE= BASE_URL + "ws-attendence";
        public static final String EXPLODED_VIEW= BASE_URL + "ws-exploded-type";
        public static final String EXPLODED_VIEW_SEND= BASE_URL + "ws-send-exploded-data";
        public static final String CNP_APPLICATION= BASE_URL + "ws-application-type";
        public static final String SEND_CNP_APPLICATION= BASE_URL + "ws-send-application-data";
        public static final String CNP_PROFILE= BASE_URL + "ws-profile-type";
        public static final String CNP_SUBPROFILE= BASE_URL + "ws-profile-sub-type";
        public static final String SEND_CNP_PROFILE= BASE_URL + "ws-send-profile-data";
        public static final String  LEAVE_APPLICATION= BASE_URL + "ws-leave-application";
        public static final String  HOLIDAY_LIST= BASE_URL + "ws-holiday-list";
        public static final String  CUSTOMER_BLANK_FILED_LIST= BASE_URL + "ws-cust-blank-field-list";
        public static final String  NOTIFICATION_LIST= BASE_URL + "ws-notification-list";
        public static final String  MY_PERFORMANCE= BASE_URL + "ws-user-performance";
        public static final String  QUOTATION_LIST_COUNT= BASE_URL + "ws-user-quote-list-count";
        public static final String  QUOTATION_PDF_LIST= BASE_URL + "ws-quote-list-pdf";
        public static final String  COLLECTION_DEPOSIT= BASE_URL + "ws-collection-deposit";

    }

    public class ServiceCode {

        public static final int LOGIN = 101;
        public static final int CHANGE_PASSWORD = 102;
        public static final int USER_INFO = 103;
        public static final int UPDATE_USER_INFO = 104;
        public static final int UPLOAD_PROFILE_PIC = 105;
        public static final int PRICE_LIST = 106;
        public static final int PRICE_SEARCH = 107;
        public static final int STOCK_LIST = 108;
        public static final int STOCK_DETAILS = 109;
        public static final int STOCK_SEARCH = 110;
        public static final int MY_CUSTOMER_LIST = 111;
        public static final int ADD_MY_CUSTOMER_LIST = 112;
        public static final int ADD_VISITS = 113;
        public static final int COLLECTION = 114;
        public static final int VIEW_COLLECTION = 115;
        public static final int BRANCH_LIST = 116;
        public static final int GET_PRODUCT = 117;
        public static final int ADD_QUOTATION = 118;
        public static final int VIEW_VISITS = 119;
        public static final int QUOTATION_LIST = 120;
        public static final int VIEW_QUOTATION = 121;
        public static final int CHANGE_STATUS = 122;
        public static final int SEND_MAIL = 123;
        public static final int GET_OUTSTANDING_AMOUNT = 124;
        public static final int GET_OUTSTANDING_RECEIPT = 124;
        public static final int POST_OUTSTANDING_MAIL = 125;
        public static final int POST_PRODUCT_TYPE = 126;
        public static final int UPDATE_DISCOUNT = 127;
        public static final int UPDATE_QUANTITY = 128;
        public static final int DELETE_PRODUCT = 129;
        public static final int UPDATE_ADDRESS = 130;
        public static final int UPDATE_CUSTOMER = 131;
        public static final int ADD_HO_DETAIL = 132;
        public static final int SEND_PI = 133;

        public static final int MARK_ATTENDANCE = 134;
        public  static final int EXPLODED_VIEW=135;
        public static  final int EXPLODED_VIEW_SEND=136;
        public static  final int CNP_APPLICATION=137;
        public  static final int SEND_CNP_APPLICATION=138;
        public static  final int CNP_PROFILE=139;
        public  static final int SEND_CNP_PROFILE=140;
        public  static final int CNP_SUBPROFILE=141;
        public  static final int LEAVE_APPLICATION=142;
        public  static final int HOLIDAY_LIST=143;
        public  static final int CUSTOMER_BLANK_FILED_LIST=144;
        public  static final int NOTIFICATION_LIST=145;
        public  static final int MY_PERFORMANCE=146;
        public  static final int QUOTATION_LIST_COUNT=147;
        public  static final int QUOTATION_PDF_LIST=148;
        public  static final int COLLECTION_DEPOSIT=149;

        public  static final int REMARK=150;


    }

}
