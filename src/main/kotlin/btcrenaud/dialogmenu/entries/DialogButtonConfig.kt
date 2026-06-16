package btcrenaud.dialogmenu.entries

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.core.extension.annotations.Placeholder
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var

sealed interface DialogButtonConfig

/** A simple action button executing a command. */
@AlgebraicTypeInfo("action_button", Colors.BLUE, "fa6-solid:arrow-right")
data class ActionButtonConfig(
    @Placeholder
    @Help("Text displayed on the button.")
    val label: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Command executed when the button is clicked.")
    val command: Var<String> = ConstVar(""),
    @Help("Whether to execute the command as OP.")
    val asOp: Var<Boolean> = ConstVar(false),
) : DialogButtonConfig

/** Exit button closing the dialog. */
@AlgebraicTypeInfo("exit_button", Colors.RED, "fa6-solid:door-open")
data class ExitButtonConfig(
    @Placeholder
    @Help("Text displayed on the exit button.")
    val label: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Command executed when the exit button is clicked.")
    val command: Var<String> = ConstVar(""),
    @Help("Whether to execute the command as OP.")
    val asOp: Var<Boolean> = ConstVar(false),
)

/** Text input paired with a confirmation button. */
@AlgebraicTypeInfo("text_input_button", Colors.GREEN, "fa6-solid:keyboard")
data class TextInputButtonConfig(
    @Placeholder
    @Help("Label displayed above the text input.")
    val label: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Command template executed when the confirmation button is clicked. Use $(input) to insert the value.")
    val command: Var<String> = ConstVar(""),
    @Help("Whether to execute the command as OP.")
    val asOp: Var<Boolean> = ConstVar(false),
    @Placeholder
    @Help("Label for the confirmation button.")
    val buttonLabel: Var<String> = ConstVar("OK"),
    val width: Var<Int> = ConstVar(200),
    val labelVisible: Var<Boolean> = ConstVar(true),
    val initial: Var<String> = ConstVar(""),
    val maxLength: Var<Int> = ConstVar(256),
    val maxLines: Var<Int> = ConstVar(0),
    val height: Var<Int> = ConstVar(0),
) : DialogButtonConfig

/** Boolean toggle input. */
@AlgebraicTypeInfo("boolean_button", Colors.PURPLE, "fa6-solid:toggle-on")
data class BooleanButtonConfig(
    @Placeholder
    @Help("Label displayed next to the toggle.")
    val label: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Key used to insert the value in the command. Default: 'boolean'.")
    val key: Var<String> = ConstVar(DialogKey.BOOLEAN.key),
    val initial: Var<Boolean> = ConstVar(false),
    @Placeholder
    @Help("Value inserted in the command when the toggle is true.")
    val onTrue: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Value inserted in the command when the toggle is false.")
    val onFalse: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Command template executed when the confirmation button is clicked. Use $(<key>) to insert the value.")
    val command: Var<String> = ConstVar(""),
    @Help("Whether to execute the command as OP.")
    val asOp: Var<Boolean> = ConstVar(false),
    @Placeholder
    @Help("Label for the confirmation button.")
    val buttonLabel: Var<String> = ConstVar("OK"),
) : DialogButtonConfig

/** Multiple choice input. */
@AlgebraicTypeInfo("multiple_choice_button", Colors.YELLOW, "fa6-solid:list")
data class MultipleChoiceButtonConfig(
    @Placeholder
    @Help("Label displayed above the options.")
    val label: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Comma separated options. Append * to mark the initial option.")
    val options: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Command template executed when the confirmation button is clicked. Use $(choice) to insert the selected option.")
    val command: Var<String> = ConstVar(""),
    @Help("Whether to execute the command as OP.")
    val asOp: Var<Boolean> = ConstVar(false),
    @Placeholder
    @Help("Label for the confirmation button.")
    val buttonLabel: Var<String> = ConstVar("OK"),
) : DialogButtonConfig

/** Number range slider input. */
@AlgebraicTypeInfo("number_range_button", Colors.ORANGE, "fa6-solid:sliders")
data class NumberRangeButtonConfig(
    @Placeholder
    @Help("Label displayed above the slider.")
    val label: Var<String> = ConstVar(""),
    @Placeholder
    @Help("Key used to insert the value in the command. Default: 'value'.")
    val key: Var<String> = ConstVar(DialogKey.VALUE.key),
    val start: Var<Int> = ConstVar(0),
    val end: Var<Int> = ConstVar(0),
    val initial: Var<Int> = ConstVar(Int.MIN_VALUE),
    val step: Var<Int> = ConstVar(1),
    @Placeholder
    @Help("Command template executed when the confirmation button is clicked. Use $(<key>) to insert the value.")
    val command: Var<String> = ConstVar(""),
    @Help("Whether to execute the command as OP.")
    val asOp: Var<Boolean> = ConstVar(false),
    @Placeholder
    @Help("Label for the confirmation button.")
    val buttonLabel: Var<String> = ConstVar("OK"),
) : DialogButtonConfig


