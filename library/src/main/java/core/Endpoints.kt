package core

internal object Endpoints {
    private const val API_URL = "https://i.instagram.com/api/v1"

    const val CSRF_TOKEN = "$API_URL/si/fetch_headers/?challenge_type=signup&guid=%s"
    const val LOGIN = "$API_URL/accounts/login/"
    const val LOGIN_APPROVAL = "$API_URL/accounts/two_factor_login"
    const val CHALLENGE_PATH = "$API_URL%s"
    const val LOGOUT = "$API_URL/accounts/logout/"
    const val ACCOUNT_INFO = "$API_URL/users/%s/info/"
    const val ACCOUNT_FEED = "$API_URL/feed/user/%s/?max_id=%s&min_timestamp=%s&rank_token=%s&ranked_content=true"
    const val FOLLOWERS = "$API_URL/friendships/%s/followers/?ig_sig_key_version=${Crypto.SIG_VERSION}&rank_token=%s&max_id=%s"
    const val FOLLOWING = "$API_URL/friendships/%s/following/?ig_sig_key_version=${Crypto.SIG_VERSION}&rank_token=%s&max_id=%s"

    const val SEARCH = "$API_URL/users/search/?ig_sig_key_version=${Crypto.SIG_VERSION}&rank_token=%s&is_typeahead=false&query=%s"

    const val STORIES = "$API_URL/feed/user/%s/story/"
}