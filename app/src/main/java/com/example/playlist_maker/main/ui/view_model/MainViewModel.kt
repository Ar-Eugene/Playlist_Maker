import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlist_maker.common.ThemeInteractor

class MainViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {

    private val _isDarkThemeLiveData = MutableLiveData<Boolean>()
    val isDarkThemeLiveData: LiveData<Boolean> = _isDarkThemeLiveData

    init {
        _isDarkThemeLiveData.value = themeInteractor.isDarkTheme()
    }

    fun applyTheme() {
        themeInteractor.applyTheme()
    }

}