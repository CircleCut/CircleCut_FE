import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseD(
    @SerialName("uid1")
    val uid1: Int,

    @SerialName("created_at")
    val createdAt: String?=null, // Assuming the timestamp is represented as a string

    @SerialName("uid2")
    val uid2: Int?,

    @SerialName("when")
    val whenDate: String?, // Assuming "when" is used as a variable name, so backticks are needed

    @SerialName("name")
    val name: String?,

    @SerialName("currency")
    val currency: String?,

    @SerialName("paidby")
    val paidby: Int?,

    @SerialName("amount")
    val amount: Double?,

    @SerialName("issettled")
    val isSettled: Boolean?=false,

    @SerialName("expenseid")
    val expenseId: Long?=null
)