package defdev.labs.droid.vulnabank

enum class FormField { NAME, ACCOUNT_NUMBER, AMOUNT }

data class TransactionForm(
    val name: String = "",
    val accountNumber: String = "",
    val amount: String = "",
    val comment: String = "",
) {
    val invalidField: FormField?
        get() = when {
            name.isEmpty() -> FormField.NAME
            accountNumber.length < 8 -> FormField.ACCOUNT_NUMBER
            amount.isEmpty() || amount.toInt() <= 0 -> FormField.AMOUNT
            else -> null
        }

    val isValid: Boolean
        get() = invalidField == null

    fun message(): String = "$name;$accountNumber;$amount;$comment"
}
