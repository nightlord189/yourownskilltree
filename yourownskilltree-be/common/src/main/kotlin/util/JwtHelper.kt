package org.aburavov.yourownskilltree.backend.common.util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.Principal
import org.aburavov.yourownskilltree.backend.common.permissions.UserGroup

@OptIn(ExperimentalEncodingApi::class)
fun decodeJwt(token: String): Principal {
    if (token == "") {
        return Principal(
            userGroup = UserGroup.GUEST,
        )
    }

    // Разделяем JWT на части
    val parts = token.split(".")
    if (parts.size != 3) {
        throw IllegalArgumentException("Invalid JWT token format")
    }

    // Может потребоваться дополнить строку символами '=' для корректного декодирования Base64
    // Декодируем payload (вторую часть)
    val payload = parts[1]
    val padding = when (payload.length % 4) {
        0 -> ""
        2 -> "=="
        3 -> "="
        else -> throw IllegalArgumentException("Invalid payload length")
    }

    val jwtJson = Base64.decode(payload + padding).decodeToString()
    println("JWT JSON PAYLOAD: $jwtJson")

    val jsonObject = Json.parseToJsonElement(jwtJson).jsonObject

    val userId = jsonObject["user_id"]?.jsonPrimitive?.content ?: ""
    val userGroup = jsonObject["user_group"]?.jsonPrimitive?.content ?: "GUEST"

    return Principal(
        userId =userId,
        userGroup = UserGroup.valueOf(userGroup)
    )
}

@Serializable
private data class JwtHeader(
    val alg: String = "HS256",
    val typ: String = "JWT"
)

@Serializable
private data class JwtPayload(
    @SerialName("user_id")
    val userId: String,
    @SerialName("user_group")
    val userGroup: String
)

@OptIn(ExperimentalEncodingApi::class)
fun Principal.encodeToJwt(): String {
    val header = JwtHeader()
    val payload = JwtPayload(
        userId = userId,
        userGroup = userGroup.name
    )

    val encodedHeader = Base64.encode(Json.encodeToString(header).toByteArray())
    val encodedPayload = Base64.encode(Json.encodeToString(payload).toByteArray())

    // Фиксированная подпись для примера
    val signature = "test_signature"

    return "$encodedHeader.$encodedPayload.$signature"
}
