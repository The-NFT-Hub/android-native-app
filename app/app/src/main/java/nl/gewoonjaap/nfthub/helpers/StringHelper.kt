package nl.gewoonjaap.nfthub.helpers

class StringHelper {
    companion object {
        fun ellipsize(input: String?, maxLength: Int): String? {
            return if (input == null || input.length <= maxLength) {
                input
            } else input.substring(0, maxLength - 3) + "..."
        }
    }
}