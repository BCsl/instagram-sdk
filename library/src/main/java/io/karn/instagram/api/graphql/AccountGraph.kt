package io.karn.instagram.api.graphql

import io.karn.instagram.common.json
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import khttp.get

internal object AccountGraph {

    fun followers(session: Session, primaryKey: String, resultSize: Int = 24, endCursor: String = "", withRelationshipStatus: Boolean = false, withReelStatus: Boolean = false) =
            relationships(session, Endpoints.FOLLOWERS_HASH, primaryKey, resultSize, endCursor, withRelationshipStatus, withReelStatus)

    fun following(session: Session, primaryKey: String, resultSize: Int = 24, endCursor: String = "", withRelationshipStatus: Boolean = false, withReelStatus: Boolean = false) =
            relationships(session, Endpoints.FOLLOWING_HASH, primaryKey, resultSize, endCursor, withRelationshipStatus, withReelStatus)

    private fun relationships(session: Session, hash: String, primaryKey: String, resultSize: Int = 24, endCursor: String = "", withRelationshipStatus: Boolean = false, withReelStatus: Boolean = false) =
            get(url = Endpoints.GRAPH_API_URL,
                    params = mapOf(
                            "query_hash" to hash,
                            "variables" to json {
                                "id" to primaryKey
                                "include_reel" to withReelStatus
                                "fetch_mutual" to withRelationshipStatus
                                "first" to resultSize
                                "after" to endCursor
                            }.toString()
                    ),
                    headers = Crypto.HEADERS,
                    cookies = session.cookieJar
            )

}
