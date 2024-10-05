import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.settings.domain.theme.interactor.ThemeInteractor

class MainViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {

    private val _isDarkThemeLiveData = MutableLiveData<Boolean>()
    val isDarkThemeLiveData: LiveData<Boolean> = _isDarkThemeLiveData

    fun applyTheme() {
        val isDarkTheme = themeInteractor.isDarkTheme()
        _isDarkThemeLiveData.value = isDarkTheme
        setAppTheme(isDarkTheme)
    }

    private fun setAppTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}