import org.aburavov.yourownskilltree.backend.common.permissions.UserGroup
import org.aburavov.yourownskilltree.backend.common.util.decodeJwt
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class JwtTest {
    @Test
    fun `should decode valid JWT token`() {
        // Create a test JWT token
        // Header: {"alg":"HS256","typ":"JWT"}
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        // Payload: {"user_id":"test123","user_group":"ADMIN"}
        val payload = "eyJ1c2VyX2lkIjoidGVzdDEyMyIsInVzZXJfZ3JvdXAiOiJBRE1JTiJ9"
        // Signature (can be any string for test purposes since we don't validate it)
        val signature = "test_signature"

        val token = "$header.$payload.$signature"

        val principal = decodeJwt(token)

        assertEquals("test123", principal.userId)
        assertEquals(UserGroup.ADMIN, principal.userGroup)
    }

    @Test
    fun `should handle missing fields with defaults`() {
        // Header: {"alg":"HS256","typ":"JWT"}
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        // Payload: {"some_other_field":"value"}
        val payload = "eyJzb21lX290aGVyX2ZpZWxkIjoidmFsdWUifQ"
        val signature = "test_signature"

        val token = "$header.$payload.$signature"

        val principal = decodeJwt(token)

        assertEquals("", principal.userId)
        assertEquals(UserGroup.GUEST, principal.userGroup)
    }

    @Test
    fun `should throw exception for invalid JWT format`() {
        val invalidToken = "invalid.token"

        assertThrows<IllegalArgumentException> {
            decodeJwt(invalidToken)
        }
    }

    @Test
    fun `should throw exception for invalid base64 padding`() {
        // Header: {"alg":"HS256","typ":"JWT"}
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        // Invalid base64 string with wrong length
        val payload = "abc"
        val signature = "test_signature"

        val token = "$header.$payload.$signature"

        assertThrows<IllegalArgumentException> {
            decodeJwt(token)
        }
    }
}