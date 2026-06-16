package btcrenaud.dialogmenu.entries

/**
 * Common keys usable for dialog button inputs. The string value of each enum
 * is used to retrieve the corresponding response from the dialog.
 */
enum class DialogKey(val key: String) {
    VALUE("value"),
    INPUT("input"),
    RESULT("result"),
    CHOICE("choice"),
    OPTION("option"),
    SELECTION("selection"),
    TOGGLE("toggle"),
    BOOLEAN("boolean"),
    NUMBER("number"),
    RANGE("range"),
    TEXT("text"),
    AMOUNT("amount"),
    COUNT("count"),
    LEVEL("level"),
    NAME("name");

    override fun toString(): String = key
}

