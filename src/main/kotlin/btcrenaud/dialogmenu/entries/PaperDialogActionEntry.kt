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
import com.typewritermc.engine.paper.entry.triggerEntriesFor
import com.typewritermc.engine.paper.extensions.placeholderapi.parsePlaceholders
import com.typewritermc.engine.paper.utils.asMini
import com.typewritermc.engine.paper.utils.item.Item
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import io.papermc.paper.registry.data.dialog.input.TextDialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback

/** How the dialog window lays out its buttons. */
enum class DialogDisplayType {
    /** Grid of buttons, `columns` per row. */
    MULTI_ACTION,

    /** Vanilla yes/no window: first button confirms, the exit button declines. */
    CONFIRMATION,

    /** Single-button notice window using the first button (or the exit button). */
    NOTICE,
}

/** What the client does with the dialog after a button is clicked. */
enum class DialogCloseBehaviour(val paper: DialogBase.DialogAfterAction) {
    CLOSE(DialogBase.DialogAfterAction.CLOSE),
    NONE(DialogBase.DialogAfterAction.NONE),
    WAIT_FOR_RESPONSE(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE),
}

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
    @Help("Item showcased in the dialog body (empty for none).")
    val bodyItem: Var<Item> = ConstVar(Item.Empty),
    val buttons: List<DialogButtonConfig> = emptyList(),
    val exitButton: ExitButtonConfig = ExitButtonConfig(),
    @Placeholder
    @Help("Whether to display the exit button.")
    val showExitButton: Var<Boolean> = ConstVar(true),
    @Placeholder
    @Help("Number of columns to display.")
    val columns: Var<Int> = ConstVar(1),
    @Help("Window layout: button grid, yes/no confirmation, or single-button notice.")
    val dialogType: DialogDisplayType = DialogDisplayType.MULTI_ACTION,
    @Help("What happens client-side after clicking a button.")
    val afterAction: DialogCloseBehaviour = DialogCloseBehaviour.CLOSE,
) : ActionEntry {
    override fun ActionTrigger.execute() {
        DialogMenuPlaceholder.enter(player, name)
        val title = (displayName.get(player, context).takeIf { it.isNotBlank() } ?: name)
            .parsePlaceholders(player)
            .asMini()

        val bodies = mutableListOf<DialogBody>()
        val itemStack = bodyItem.get(player, context).build(player, context)
        if (!itemStack.type.isAir) {
            bodies.add(DialogBody.item(itemStack).build())
        }
        bodies.add(
            DialogBody.plainMessage(
                message.get(player, context).parsePlaceholders(player).asMini()
            )
        )

        val cols = columns.get(player, context).coerceAtLeast(1)

        val actions = mutableListOf<ActionButton>()
        val inputs = mutableListOf<DialogInput>()
        val booleanValues = mutableMapOf<String, Pair<String, String>>()
        val interactionContext = context

        buttons.forEach { button ->
            when (button) {
                is ActionButtonConfig -> {
                    val action = commandAction(button.command.get(player, context), button.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
                    val label = button.label.get(player, context).parsePlaceholders(player).asMini()
                    val built = ActionButton.builder(label).action(action).build()
                    actions.add(built)
                }
                is TriggerButtonConfig -> {
                    val refs = button.triggers
                    val action = DialogAction.customClick({ _, audience ->
                        val clicker = audience as? org.bukkit.entity.Player ?: return@customClick
                        refs.triggerEntriesFor(clicker, interactionContext)
                        DialogMenuPlaceholder.exit(clicker)
                    }, ClickCallback.Options.builder().uses(1).build())
                    val label = button.label.get(player, context).parsePlaceholders(player).asMini()
                    actions.add(ActionButton.builder(label).action(action).build())
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
                            button.key.get(player, context).ifBlank { DialogKey.INPUT.key },
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
                            button.key.get(player, context).ifBlank { DialogKey.CHOICE.key },
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

        val buildExit = dialogType == DialogDisplayType.CONFIRMATION || showExitButton.get(player, context)
        val exit = if (buildExit) {
            val cfg = exitButton
            val action = commandAction(cfg.command.get(player, context), cfg.asOp.get(player, context), booleanValues) { DialogMenuPlaceholder.exit(it) }
            ActionButton.builder(
                cfg.label.get(player, context).parsePlaceholders(player).asMini()
            ).action(action).build()
        } else null

        val dialog = Dialog.create { factory ->
            val baseBuilder = DialogBase.builder(title)
                .body(bodies)
                .canCloseWithEscape(true)
                .afterAction(afterAction.paper)

            if (inputs.isNotEmpty()) {
                baseBuilder.inputs(inputs)
            }

            val type = when (dialogType) {
                DialogDisplayType.MULTI_ACTION -> DialogType.multiAction(actions, exit, cols)
                DialogDisplayType.CONFIRMATION -> {
                    val yes = actions.firstOrNull() ?: exit ?: defaultButton()
                    val no = exit ?: defaultButton()
                    DialogType.confirmation(yes, no)
                }
                DialogDisplayType.NOTICE -> {
                    val ok = actions.firstOrNull() ?: exit
                    if (ok != null) DialogType.notice(ok) else DialogType.notice()
                }
            }

            factory.empty()
                .base(baseBuilder.build())
                .type(type)
        }

        player.showDialog(dialog)
    }

    private fun defaultButton(): ActionButton =
        ActionButton.builder(Component.translatable("gui.ok")).build()
}
