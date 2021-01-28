package rs.mmitic.rafcloud.data.model

typealias LoggedInUser = UserDTO

class UserDTO(
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val role: UserRole
)

enum class UserRole(val value: String) {
    USER("USER"), ADMIN("ADMIN")
}

val UserDTO.displayName: String
    get() = when {
        firstName != null && lastName != null -> "$firstName $lastName"
        firstName != null -> firstName
        lastName != null -> lastName
        else -> username
    }