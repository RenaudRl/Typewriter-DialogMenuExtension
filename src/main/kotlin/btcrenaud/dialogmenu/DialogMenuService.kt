package btcrenaud.dialogmenu

import io.papermc.paper.dialog.Dialog
import org.bukkit.entity.Player

/**
 * Service to handle Paper Dialog UI.
 * This is used as a bridge to call the Paper Dialog API in a module that correctly resolves it.
 */
object DialogMenuService {

    /**
     * Shows a dialog to a player.
     * @param player The player to show the dialog to.
     * @param dialog The dialog to show.
     */
    fun showDialog(player: Player, dialog: Dialog) {
        player.showDialog(dialog)
    }
}
