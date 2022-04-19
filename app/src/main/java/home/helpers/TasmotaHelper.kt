package home.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.example.smartsystem.R
import home.api.UnifiedAPI
import org.json.JSONArray
import org.json.JSONObject

class TasmotaHelper(private val c: Context, private val tasmota: UnifiedAPI)  {

    companion object {
        const val EMPTY_ARRAY: String = "[]"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(c)
    private val nullParent: ViewGroup? = null

    fun updateItem(callback: UnifiedAPI.CallbackInterface, index: Int) {
        val array = JSONArray(prefs.getString(tasmota.deviceId, EMPTY_ARRAY))
        val arrayItem = array.optJSONObject(index) ?: JSONObject()
        val view = LayoutInflater.from(c).inflate(R.layout.dialog_tasmota_add, nullParent, false)
        val titleTxt = view.findViewById<EditText>(R.id.title)
        val commandTxt = view.findViewById<EditText>(R.id.command)
        titleTxt.setText(arrayItem.optString("title"))
        commandTxt.setText(arrayItem.optString("command"))
        AlertDialog.Builder(c)
            .setTitle(R.string.tasmota_add_command)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val newTitle = titleTxt.text.toString()
                val newCommand = commandTxt.text.toString()
                array.remove(index)
                prefs.edit().putString(tasmota.deviceId, array.put(
                    JSONObject()
                        .put("title", if (newTitle == "") c.resources.getString(R.string.tasmota_add_command_dialog_title_empty) else newTitle)
                        .put("command", if (newCommand == "") c.resources.getString(R.string.tasmota_add_command_dialog_command_empty) else newCommand)
                ).toString()).apply()
                tasmota.loadList(callback)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    fun addToList(callback: UnifiedAPI.CallbackInterface, title: String = "", command: String = "") {
        val view = LayoutInflater.from(c).inflate(R.layout.dialog_tasmota_add, nullParent, false)
        val titleTxt = view.findViewById<EditText>(R.id.title)
        val commandTxt = view.findViewById<EditText>(R.id.command)
        titleTxt.setText(title)
        commandTxt.setText(command)
        AlertDialog.Builder(c)
                .setTitle(R.string.tasmota_add_command)
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val newTitle = titleTxt.text.toString()
                    val newCommand = commandTxt.text.toString()
                    prefs.edit().putString(tasmota.deviceId, JSONArray(
                            prefs.getString(tasmota.deviceId, EMPTY_ARRAY)).put(JSONObject()
                                    .put("title", if (newTitle == "") c.resources.getString(R.string.tasmota_add_command_dialog_title_empty) else newTitle)
                                    .put("command", if (newCommand == "") c.resources.getString(R.string.tasmota_add_command_dialog_command_empty) else newCommand)
                            ).toString()).apply()
                    tasmota.loadList(callback)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
    }

    fun removeFromList(callback: UnifiedAPI.CallbackInterface, index: Int) {
        val array = JSONArray(prefs.getString(tasmota.deviceId, EMPTY_ARRAY))
        array.remove(index)
        prefs.edit().putString(tasmota.deviceId, array.toString()).apply()
        tasmota.loadList(callback)
    }

    fun executeOnce(callback: UnifiedAPI.CallbackInterface) {
        val view = LayoutInflater.from(c).inflate(R.layout.dialog_tasmota_execute_once, nullParent, false)
        val command = view.findViewById<EditText>(R.id.command)
        AlertDialog.Builder(c)
                .setTitle(R.string.tasmota_execute_once)
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    tasmota.execute(command.text.toString(), callback)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
    }
}