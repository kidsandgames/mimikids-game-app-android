package games.kidsand.mimikids.app.android

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import games.kidsand.mimikids.app.android.screens.game.GameViewModel
import games.kidsand.mimikids.app.android.screens.score.ScoreViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel()
            isAssignableFrom(ScoreViewModel::class.java) ->
                ScoreViewModel()
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}