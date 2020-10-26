package games.kidsand.mimikids.app.android.screens.title

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import games.kidsand.mimikids.app.android.data.model.GuessCategory

class TitleViewModel : ViewModel() {

    private val _categories = MutableLiveData<Array<GuessCategory>>().apply {
        value = arrayOf(
                GuessCategory("Animais", "animals"),
                GuessCategory("Emoções", "emotions"),
                GuessCategory("Objetos", "objects")
        )
    }
    val categories: Array<String>? = _categories.value?.map { guessCategory ->
        guessCategory.title
    }?.toTypedArray()

    private val _selectCategory = MutableLiveData<Boolean?>()
    val selectCategory: LiveData<Boolean?> = _selectCategory

    private val _navigateToGame = MutableLiveData<String?>()
    val navigateToGame: LiveData<String?> = _navigateToGame

    fun onPlayClicked() {
        _selectCategory.value = true
    }

    fun onCategorySelected(categoryIndex: Int) {
        _selectCategory.value = null
        _navigateToGame.value = _categories.value?.get(categoryIndex)?.name
    }

    fun onGameNavigated() {
        _navigateToGame.value = null
    }
}