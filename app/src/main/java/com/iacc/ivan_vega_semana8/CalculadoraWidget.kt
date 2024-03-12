import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import com.iacc.ivan_vega_semana8.R

/**
 * Implementation of App Widget functionality.
 */
class CalculadoraWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.calculadora_widget)

            val textViewInputId = context.resources.getIdentifier("textViewInput", "id", context.packageName)
            views.setOnClickPendingIntent(textViewInputId, getPendingSelfIntent(context, "TEXTVIEW_CLICK", appWidgetId))

            val inputData = getSavedInputData(context)
            views.setTextViewText(textViewInputId, inputData)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPendingSelfIntent(context: Context, action: String, appWidgetId: Int): PendingIntent {
            val intent = Intent(context, CalculadoraWidget::class.java)
            intent.action = action
            intent.putExtra("APPWIDGET_ID", appWidgetId)
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        private fun getSavedInputData(context: Context): String {
            val sharedPreferences = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
            return sharedPreferences.getString("inputData", "") ?: ""
        }

        private fun saveInputData(context: Context, newData: String) {
            val sharedPreferences = context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("inputData", newData)
            editor.apply()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "TEXTVIEW_CLICK") {
            val appWidgetId = intent.getIntExtra("APPWIDGET_ID", 0)
            val clickedNumber = intent.getStringExtra("EXTRA")

            val inputData = getSavedInputData(context)

            val updatedInputData = inputData + clickedNumber

            saveInputData(context, updatedInputData)

            updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
        }
    }
}