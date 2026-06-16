package btcrenaud.dialogmenu.entries

import btcrenaud.dialogmenu.util.DialogMenuPlaceholder
import btcrenaud.dialogmenu.util.commandAction
import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.core.extension.annotations.MultiLine
import com.typewritermc.core.extension.annotations.Placeholder
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.extensions.placeholderapi.parsePlaceholders
import com.typewritermc.engine.paper.utils.asMini
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.input.TextDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component

@Entry("paper_dialog", "Display a customizable dialog", Colors.BLUE, "mdi:view-grid")
class PaperDialogActionEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    @Placeholder
    @Help("Custom display name for the dialog title.")
    val displayName: Var<String> = ConstVar(""),
    @Placeholder
    @MultiLine
    @Help("Lines of text displayed in the dialog body.")
    val message: Var<String> = ConstVar(""),
    val buttons: List<DialogButtonConfig> = emptyList(),
    val exitButton: ExitButtonConfig = ExitButtonConfig(),
    @Placeholder
    @Help("Whether to display the exit button.")
    val showExitButton: Var<Boolean> = ConstVar(true),
    @Placeholder
    @Help("Number of columns to display.")
    val columns: Var<Int> = ConstVar(1),
) : ActionEntry {
    override fun ActionTrigger.execute() {
        DialogMenuPlaceholder.enter(player, name)
        val title = (displayName.get(player, context).takeIf { it.isNotBlank() } ?: name)
            .parsePlaceholders(player)
            .asMini()
        val body = DialogBody.plainMessage(
            message.get(player, context).parsePlaceholders(player).asMini()
        )

        val cols = columns.get(player, context).coerceAtLeast(1)

        val actions = mutableListOf<ActionButton>()
        val inputs = mutableListOf<DialogInput>()
        val booleanValues = mutableMapOf<String, Pair<String, String>>()

        buttons.forEach { button ->
            when (button) {
                is ActionButtonConfig -> {
                    val action = commandAction(button.command.get(player, context), button.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
                    val label = button.label.get(player, context).parsePlaceholders(player).asMini()
                    val built = ActionButton.builder(label).action(action).build()
                    actions.add(built)
                }
                is TextInputButtonConfig -> {
                    val action = commandAction(button.command.get(player, context), button.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
                    val label = button.buttonLabel.get(player, context).parsePlaceholders(player).asMini()
                    val btn = ActionButton.builder(label).action(action).build()
                    actions.add(btn)

                    val multi = run {
                        val lines = button.maxLines.get(player, context)
                        val h = button.height.get(player, context)
                        if (lines > 0 || h > 0) {
                            TextDialogInput.MultilineOptions.create(lines.takeIf { it > 0 }, h.takeIf { it > 0 })
                        } else null
                    }

                    inputs.add(
                        DialogInput.text(
                            DialogKey.INPUT.key,
                            button.width.get(player, context),
                            button.label.get(player, context).parsePlaceholders(player).asMini(),
                            button.labelVisible.get(player, context),
                            button.initial.get(player, context),
                            button.maxLength.get(player, context),
                            multi
                        )
                    )
                }
                is BooleanButtonConfig -> {
                    val key = button.key.get(player, context)
                    val trueVal = button.onTrue.get(player, context).parsePlaceholders(player)
                    val falseVal = button.onFalse.get(player, context).parsePlaceholders(player)
                    booleanValues[key] = trueVal to falseVal

                    val action = commandAction(button.command.get(player, context), button.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
                    val label = button.buttonLabel.get(player, context).parsePlaceholders(player).asMini()
                    val btn = ActionButton.builder(label).action(action).build()
                    actions.add(btn)

                    inputs.add(
                        DialogInput.bool(
                            key,
                            button.label.get(player, context).parsePlaceholders(player).asMini(),
                            button.initial.get(player, context),
                            trueVal,
                            falseVal
                        )
                    )
                }
                is MultipleChoiceButtonConfig -> {
                    val action = commandAction(button.command.get(player, context), button.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
                    val label = button.buttonLabel.get(player, context).parsePlaceholders(player).asMini()
                    val btn = ActionButton.builder(label).action(action).build()
                    actions.add(btn)

                    val options = button.options.get(player, context).split(',').filter { it.isNotBlank() }.map { opt ->
                        val trimmed = opt.trim()
                        val initial = trimmed.endsWith("*")
                        val clean = trimmed.removeSuffix("*")
                        SingleOptionDialogInput.OptionEntry.create(clean, Component.text(clean), initial)
                    }
                    inputs.add(
                        DialogInput.singleOption(
                            DialogKey.CHOICE.key,
                            button.label.get(player, context).parsePlaceholders(player).asMini(),
                            options
                        ).build()
                    )
                }
                is NumberRangeButtonConfig -> {
                    val action = commandAction(button.command.get(player, context), button.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
                    val label = button.buttonLabel.get(player, context).parsePlaceholders(player).asMini()
                    val btn = ActionButton.builder(label).action(action).build()
                    actions.add(btn)

                    val start = button.start.get(player, context).toFloat()
                    val end = button.end.get(player, context).toFloat()
                    val builder = DialogInput.numberRange(
                        button.key.get(player, context),
                        button.label.get(player, context).parsePlaceholders(player).asMini(),
                        start,
                        end
                    ).labelFormat("options.generic_value")
                    val initial = button.initial.get(player, context)
                    if (initial != Int.MIN_VALUE) builder.initial(initial.toFloat())
                    val step = button.step.get(player, context)
                    if (step > 0) builder.step(step.toFloat())
                    inputs.add(builder.build())
                }
            }
        }

        val exit = if (showExitButton.get(player, context)) {
            val cfg = exitButton
            val action = commandAction(cfg.command.get(player, context), cfg.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
            ActionButton.builder(
                cfg.label.get(player, context).parsePlaceholders(player).asMini()
            ).action(action).build()
        } else null

        val dialog = Dialog.create { factory ->
            val baseBuilder = DialogBase.builder(title)
                .body(listOf(body))
                .canCloseWithEscape(true)

            if (inputs.isNotEmpty()) {
                baseBuilder.inputs(inputs)
            }

            factory.empty()
                .base(baseBuilder.build())
                .type(DialogType.multiAction(actions, exit, cols))
        }

        player.showDialog(dialog)
    }
}


