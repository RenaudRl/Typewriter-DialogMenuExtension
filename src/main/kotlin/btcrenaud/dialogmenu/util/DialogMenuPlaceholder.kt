package btcrenaud.dialogmenu.util

import com.typewritermc.core.extension.Initializable
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.core.utils.Reloadable
import com.typewritermc.engine.paper.extensions.placeholderapi.PlaceholderHandler
import com.typewritermc.engine.paper.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Singleton
object DialogMenuPlaceholder : PlaceholderHandler, Listener, Reloadable, Initializable {
    private val active = ConcurrentHashMap<UUID, String>()

    override suspend fun initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    override suspend fun shutdown() {
        HandlerList.unregisterAll(this)
    }

    fun enter(player: Player, name: String) {
        active[player.uniqueId] = name
    }

    fun exit(player: Player) {
        active.remove(player.uniqueId)
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (!params.startsWith("inmenudialog")) return null

        val uuid = player?.uniqueId ?: return "0"

        val dialog = when {
            params.startsWith("inmenudialog_") -> params.substringAfter('_')
            params.startsWith("inmenudialog:") -> params.substringAfter(':')
            else -> ""
        }

        val isActive = if (dialog.isBlank()) {
            active.containsKey(uuid)
        } else {
            active[uuid] == dialog
        }

        return if (isActive) "1" else "0"
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        active.remove(event.player.uniqueId)
    }

    override suspend fun load() {
        // Nothing to load
    }

    override suspend fun unload() {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (active.remove(player.uniqueId) != null) {
                player.closeInventory()
            }
        }
    }
}
