package btcrenaud.dialogmenu.util

import com.typewritermc.engine.paper.extensions.placeholderapi.parsePlaceholders
import com.typewritermc.engine.paper.logger
import com.typewritermc.engine.paper.plugin
import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.action.DialogAction
import net.kyori.adventure.text.event.ClickCallback
import org.bukkit.entity.Player
import kotlin.math.roundToInt

private val macroRegex = Regex("\\$\\(([^\\)]+)\\)")

/**
 * Builds a dialog action that resolves `$(key)` macros against the dialog
 * response and executes the resulting command as the clicking player.
 *
 * The command is dispatched on the player's scheduler so it is safe on both
 * Paper and Folia regardless of which thread delivers the dialog response.
 */
fun commandAction(
    command: String?,
    asOp: Boolean,
    booleanValues: Map<String, Pair<String, String>>,
    onDone: (Player) -> Unit,
): DialogAction {
    val trimmed = command?.trim().orEmpty()
    return DialogAction.customClick({ response: DialogResponseView, audience ->
        val player = audience as? Player ?: return@customClick
        val resolved = resolveMacros(trimmed, response, booleanValues)
        if (resolved.isBlank()) {
            onDone(player)
            return@customClick
        }
        player.scheduler.run(plugin, { _ ->
            val parsed = resolved.parsePlaceholders(player)
            val wasOp = player.isOp
            try {
                if (asOp && !wasOp) player.isOp = true
                player.performCommand(parsed)
            } catch (e: Exception) {
                logger.warning("Dialog command '$parsed' failed for ${player.name}: ${e.message}")
            } finally {
                if (asOp && !wasOp) player.isOp = false
            }
            onDone(player)
        }, null)
    }, ClickCallback.Options.builder().uses(1).build())
}

private fun resolveMacros(
    command: String,
    response: DialogResponseView,
    booleanValues: Map<String, Pair<String, String>>,
): String {
    if (command.isBlank()) return ""
    var resolved = command
    macroRegex.findAll(command).forEach { match ->
        val key = match.groupValues[1]
        val mapping = booleanValues[key]
        val value = response.getText(key)
            ?: if (mapping != null) {
                response.getBoolean(key)?.let { bool ->
                    if (bool) mapping.first else mapping.second
                } ?: response.getFloat(key)?.let { f ->
                    if (f.roundToInt() == 1) mapping.first else mapping.second
                }
            } else {
                response.getFloat(key)?.roundToInt()?.toString()
                    ?: response.getBoolean(key)?.toString()
            }
            ?: ""
        resolved = resolved.replace(match.value, value)
    }
    return resolved.trim()
}
