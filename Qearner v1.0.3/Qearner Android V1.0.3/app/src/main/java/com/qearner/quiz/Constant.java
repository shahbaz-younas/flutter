package com.qearner.quiz;

public class Constant {

    private static String DOMAIN_URL = "https://qearner.thewrteam.in/"; //domain or admin panel url

    public static String QUIZ_URL = DOMAIN_URL + "api-v2.php";  //api url
    /////// PARAMETERS  ///////
    public static String AUTHORIZATION = "Authorization";
    public static String JWT_KEY = "set_your_strong_jwt_secret_key";
    public static String accessKey = "access_key";
    public static String accessKeyValue = "6808";
    public static String name = "name";
    public static String email = "email";
    public static String mobile = "mobile";
    public static String type = "type";
    public static String limit = "limit";
    public static String fcmId = "fcm_id";
    public static String userId = "user_id";
    public static String PROFILE = "profile";
    public static String userSignUp = "user_signup";
    public static String Token = "token";
    public static String MY_RANK = "my_rank";
    public static String status = "status";

    public static String KEY_DEVICE_ID = "device_id";
    public static String HEADER_DEVICE_ID = "DeviceId";
    public static String getCategories = "get_categories";
    public static String getRandomQuestion = "get_random_questions";
    public static String GET_QUES_BY_CATE = "get_questions_by_category";
    public static String GET_QUES_BY_SUB_CATE = "get_questions_by_subcategory";
    public static String getDailyQuiz = "get_daily_quiz";
    public static String getSubCategory = "get_subcategory_by_maincategory";
    public static String categoryId = "main_id";
    public static String cate_id = "category_id";
    public static String subCategoryId = "subcategory";
    public static String reportQuestion = "report_question";
    public static String questionId = "question_id";
    public static String messageReport = "message";
    public static String FILE_PATH = "file_path";
    public static String SET_USER_COINS = "set_user_coin_score";
    public static String SET_PLAYED_STATUS = "set_played_status";
    public static String DailyStatus = "set_daily_status";
    public static String getQuestionForRobot = "get_random_questions_for_computer";
    public static String getSelfChallengeQuestions = "get_questions_for_self_challenge";
    public static String category = "category";

    public static String getPrivacy = "privacy_policy_settings";
    public static String getTerms = "get_terms_conditions_settings";
    public static String get_about_us = "get_about_us";
    public static String upload_profile_image = "upload_profile_image";
    public static String image = "image";
    public static String updateFcmId = "update_fcm_id";
    public static String updateProfile = "update_profile";
    public static String GET_MONTHLY_LB = "get_monthly_leaderboard";
    public static String setMonthlyLeaderboard = "set_monthly_leaderboard";
    public static String NO_OF_CATE = "no_of";
    public static String IS_PLAY = "is_played";
    public static String NO_OF_QUES = "no_of_que";
    public static String GET_USER_BY_ID = "get_user_by_id";
    public static String GET_GLOBAL_LB = "get_global_leaderboard";
    public static String GET_TODAY_LB = "get_datewise_leaderboard";
    public static String FROM = "from";
    public static String TO = "to";
    public static String LOGIN = "login";
    public static String TotalCoins = "TotalCoins";
    public static String get_questions_by_type = "get_questions_by_type";
    public static String GET_CATE_BY_LANG = "get_categories_by_language";

    public static String SET_USER_STATISTICS = "set_users_statistics";
    public static String GET_USER_STATISTICS = "get_users_statistics";
    public static String GET_SYSTEM_CONFIG = "get_system_configurations";
    public static String GET_COINS_LIST = "get_all_coin_list";
    public static String GET_INSTRUCTIONS = "get_instructions";
    public static String GET_LANGUAGES = "get_languages";
    public static String Get_IN_APPURCHASE="in_app_purchase_mode";
    public static String IN_APPPURCHASE="0";

    public static String ACCOUNT_REMOVE="account_remove";
    public static String GET_NOTIFICATIONS = "get_notifications";
    public static String LANGUAGE = "language";
    public static String LANGUAGE_ID = "language_id";
    public static String RATIO = "ratio";
    public static String CORRECT_ANSWERS = "correct_answers";
    public static String QUESTION_ANSWERED = "questions_answered";
    public static String AUTH_ID = "firebase_id";
    public static String QUE_TYPE = "question_type";
    public static String LIVE_CONTEST = "live_contest";
    public static String UPCOMING_CONTEST = "upcoming_contest";
    public static String PAST_CONTEST = "past_contest";

    public static String START_DATE = "start_date";
    public static String END_DATE = "end_date";
    public static String ENTRY = "entry";
    public static String DESCRIPTION = "description";
    public static String PARTICIPANTS = "participants";
    public static String DATE_CREATED = "date_created";
    public static String POINTS = "points";
    public static String TOP_WINNERS = "top_winner";
    public static String TOP_USERS = "top_users";
    public static String DATE = "date";
    public static String RANK = "user_rank";
    public static String SCORE = "score";
    public static String COINS = "coins";

    public static String ERROR = "error";
    public static String MESSAGE = "message";
    public static String DATA = "data";
    public static String ID = "id";
    public static String OFFSET = "offset";
    public static String LIMIT = "limit";
    public static String KEY_APP_LINK = "app_link";
    public static String KEY_LANGUAGE_MODE = "language_mode";
    public static String KEY_ANSWER_MODE = "answer_mode";
    public static String KEY_OPTION_E_MODE = "option_e_mode";
    public static String KEY_APP_VERSION = "app_version";

    public static String KEY_SHARE_TEXT = "shareapp_text";
    public static String CATEGORY_NAME = "category_name";
    public static String IMAGE = "image";
    public static String TITLE = "title";
    public static String DATE_SENT = "date_sent";
    public static String TYPE_ID = "type_id";
    public static String POSITION = "position";

    public static String MAIN_CATE_ID = "maincat_id";
    public static String KEY_SUB_CATE_NAME = "subcategory_name";
    public static String REFER_CODE = "refer_code";
    public static String FRIENDS_CODE = "friends_code";
    public static String STRONG_CATE = "strong_category";
    public static String WEAK_CATE = "weak_category";
    public static String RATIO_1 = "ratio1";
    public static String RATIO_2 = "ratio2";
    public static String QUESTION = "question";
    public static String OPTION_A = "optiona";
    public static String OPTION_B = "optionb";
    public static String OPTION_C = "optionc";
    public static String OPTION_D = "optiond";
    public static String OPTION_E = "optione";

    public static String NOTE = "note";
    public static String getValue = "1";
    public static String TypeTwo = "type_two";
    public static String GAME_ROOM_KEY = "match_id";
    public static String DE_ACTIVE = "0";
    public static String WINNER_ID = "winner_id";
    public static String GLOBAL_SCORE = "all_time_score";
    public static String GLOBAL_RANK = "all_time_rank";
    public static String KEY_MORE_APP = "more_apps";
    public static String TRUE_FALSE = "2";
    public static String DailyQuizText = "daily_quiz_mode";
    public static String KEY_FREE_COIN_STATUS = "free_coin_status";
    public static String KEY_DAILY_QUIZ_STATUS = "daily_quiz_status";
    public static String ContestText = "contest_mode";
    public static String ForceUpdateText = "force_update";
    public static String DailyPlayed = "0";
    public static String FreeCoins = "1";
    public static String MainTenance_Status="maintenance_status";
    public static String MainTenceStatus="0";
    public static String MainTenance_Message="maintenance_message";
    public static String MainTenanceMessage="0";
    //////////////////////////////////Payment Request///////////////////////////////////////
    public static String PaymentRequest = "payment_request";
    public static String PaymentAddress = "payment_address";
    public static String RequestType = "request_type";
    public static String RequestAmount = "request_amount";
    public static String PointsUsed = "points_used";
    public static String Remarks = "remarks";
    public static String CoinStatus = "coin_status";
    public static String AddPoint = "add_point";
    public static String PaypalAmount = "Paypal_Coin_Amount";
    public static String PaytmAmount = "Coin_Amount";

    public static String UserTracker = "user_tracker";
    public static String USER_AUTH = "UserAuthorization";
    /*-----------fireBase database column names for battle---------*/

    public static String STATUS = "status";
    public static String LEFT_BATTLE = "leftBattle";
    public static String IS_AVAIL = "isAvail";
    public static String cateId = "cateId";
    public static String LANG_ID = "langId";

    public static String DB_GAME_ROOM_NEW = "RandomBattleRoom";
    //public static String DB_GAME_ROOM_NEW = "game_room";
    public static String OPPONENT_ID = "opponentID";
    public static String MATCHING_ID = "matchingID";

    public static String USER_NAME = "name";
    public static String USER_ID = "userID";
    public static String NAME = "name";
    public static String QUESTIONS = "Questions";
    public static String RIGHT_ANS = "rightAns";
    public static String SEL_ANS = "userSelect";
    public static String TOTAL = "total";
    public static String DESTROY_GAME_KEY = "destroy_match";

    public static String Ads_Type="ads_type";
    public static String ADS_TYPE="0";
    public static String INAppAdsMode = "in_app_ads_mode";
    public static String IN_APP_MODE = "0";

    public static String fbRewardsAds = "fb_rewarded_video_ads";
    public static String FB_REWARDS_ADS = "0";
    public static String fbInterstitial = "fb_interstitial_id";
    public static String FB_INTERSTITIAL = "0";
    public static String fbBanner = "fb_banner_id";
    public static String FB_BANNER = "0";
    public static String fbNative = "fb_native_unit_id";
    public static String FB_NATIVE = "0";

    public static String AppID = "adAppId";
    public static String APP_ID = "0";
    public static String AdmobRewardsAds = "admob_Rewarded_Video_Ads";
    public static String ADMOB_REWARDS_ADS = "0";
    public static String AdmobInterstitial = "admob_interstitial_id";
    public static String ADMOB_INTERSTITIAL = "0";
    public static String AdmobBanner = "admob_banner_id";
    public static String ADMOB_BANNER = "0";
    public static String AdmobNative = "native_unit_id";
    public static String ADMOB_NATIVE = "0";
    public static String AdmobOpenAds = "admob_openads_id";
    public static String ADMOB_OPEN_ADS = "0";

    public static String FREE_COIN_STATUS = "";
    public static String DAILY_QUIZ_STATUS = "";
    public static int MAX_MINUTES = 60; //max minutes for self challenge quiz
    public static int MAX_QUESTION_PER_BATTLE = 10; // max question per battle
    public static int RANDOM_QUE_LIMIT = 10;

    public static int RESET_SKIP_COINS = 2;
    public static int FIFTY_AUD_COINS = 4;
    public static int TOTAL_COINS;
    public static long LeftTime;

    public static String CATE_ID = "";
    public static String SUB_CAT_ID = "";
    public static String CATE_NAME = "";
    public static String SUB_CATE_NAME = "";

    public static String verificationCode;

    public static String LANGUAGE_MODE;
    public static String OPTION_E_MODE;
    public static String SHARE_APP_TEXT;


    public static String VERSION_CODE;
    public static String REQUIRED_VERSION;
    public static String DAILY_QUIZ_ON;
    public static String CONTEST_ON;
    public static String FORCE_UPDATE;


    public static String QUICK_ANSWER_ENABLE = "0";
    public static long TAKE_TIME = 0;
    public static long CHALLENGE_TIME = 0;

    public static int PROGRESS_TEXT_SIZE = 13; // progress text size
    public static int PROGRESS_STROKE_WIDTH = 7; // stroke width


    public static int PAGE_LIMIT = 50;
    public static final String PREF_TEXT_SIZE = "fontSizePref";

    public static final String D_LANG_ID = "-1";


    public static final String TEXT_SIZE_MAX = "30"; //maximum text size of play area question
    public static final String TEXT_SIZE_MIN = "18";//minimum default text for play area question


    /*-----------Tournament Api---------*/
    public static String GET_CONTEST = "get_contest";
    public static String GET_DATA_KEY = "1";
    public static String GET_LEADERBOARD = "get_contest_leaderboard";
    public static String CONTEST_ID = "contest_id";

    public static String CONTEST_UPDATE_SCORE = "contest_update_score";
    public static String QUESTION_ATTEND = "questions_attended";
    public static String GET_QUESTION_BY_CONTEST = "get_questions_by_contest";
    public static String TYPE = "type";


    /// you can increase or decrease time
    public static int CIRCULAR_MAX_PROGRESS = 25; // max here we set 25 second for each question, you can increase or decrease time here
    public static int TIME_PER_QUESTION = 25000;  //here we set 25 second to milliseconds
    public static int COUNT_DOWN_TIMER = 1000; //here we set 1 second
    public static int OPPONENT_SEARCH_TIME = 11000; // time for search opponent for battle
    public static int FOR_CORRECT_ANS = 4; // mark for correct answer
    public static int PENALTY = 2;// minus mark for incorrect
    public static boolean isPlayed;

    public static int PASSING_PER = 60;  //count quiz complete when user give >30 percent correct answer

    /////////////////////Battle Field Names//////////////////////////

    public static String noOfQuestion = "noOfQuestion";
    public static String FALSE = "false";
    public static String TRUE = "true";
    public static String QUIZ_TYPE = "quiz_type";
    public static String REGULAR = "regular";
    public static String BATTLE = "battle";
    public static String PRACTICE = "practice";
    public static String RANDOM_BATTLE_CATE_MODE = "battle_random_category_mode";
    public static String GROUP_BATTLE_CATE_MODE = "battle_group_category_mode";
    public static boolean isCateEnable;
    public static boolean isGroupCateEnable;
    public static String roomName = "roomName";
    public static String roomType = "room_type";
    public static String roomID = "roomID";
    public static String time = "time";
    public static String NUMERIC_STRING = "0123456789";
    /////////////////////Custom Coins//////////////////////////

    public static String TrueAnswer = "true_answer";
    public static String FOR_CORRECT_ANS_COIN = "0";
    public static String PenaltyWrongAnswer = "penalty_wrong_answer";
    public static String PENALTY_COIN = "0";
    public static String DailyEarnCoin = "daily_earn_coins";
    public static String DAILY_EARN_COIN;
    public static String BattleQuizEntry = "battle_quiz_entry_coin";
    public static String BATTLE_QUIZ_ENTRY_COINS = "0";
    public static String BattleWinner = "battle_winner_coin";
    public static String BATTLE_WINNER_COINS = "0";
    public static String REFER_COIN = "refer_coin";
    public static String REFER_COIN_VALUE;
    public static String EARN_COIN = "earn_coin";
    public static String EARN_COIN_VALUE;
    public static String REWARD_COIN = "reward_coin";
    public static String REWARD_COIN_VALUE;
    public static String APP_LINK = "http://play.google.com/store/apps/details?id=";
    public static String MORE_APP_URL = "https://play.google.com/store/apps/developer?id=";


    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * NUMERIC_STRING.length());
            builder.append(NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

}