import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseD(
    @SerialName("uid1")
    val uid1: Long,

    @SerialName("created_at")
    val createdAt: String, // Assuming the timestamp is represented as a string

    @SerialName("uid2")
    val uid2: Long?,

    @SerialName("when")
    val whenDate: String?, // Assuming "when" is used as a variable name, so backticks are needed

    @SerialName("name")
    val name: String?,

    @SerialName("currency")
    val currency: String?,

    @SerialName("paid_by")
    val paidBy: Long?,

    @SerialName("amount")
    val amount: Double?,

    @SerialName("issettled")
    val isSettled: Boolean?,

    @SerialName("expenseid")
    val expenseId: Long?
)