import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlist_maker.settings.domain.theme.interactor.ThemeInteractor

class MainViewModelFactory(private val themeInteractor: ThemeInteractor) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(themeInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}