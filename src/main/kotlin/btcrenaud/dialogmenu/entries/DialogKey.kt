package btcrenaud.dialogmenu.entries

/**
 * Default keys used by dialog button inputs. The string value of each enum
 * is used to retrieve the corresponding response from the dialog; every input
 * type also accepts a custom key so several inputs can coexist in one dialog.
 */
enum class DialogKey(val key: String) {
    VALUE("value"),
    INPUT("input"),
    CHOICE("choice"),
    BOOLEAN("boolean");

    override fun toString(): String = key
}

