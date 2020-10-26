/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package games.kidsand.mimikids.app.android.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import games.kidsand.mimikids.app.android.databinding.GameFragmentBinding
import games.kidsand.mimikids.app.android.screens.game.GameFragmentDirections.Companion.actionGameToScore
import games.kidsand.mimikids.app.android.util.getViewModelFactory
import games.kidsand.mimikids.app.android.util.setupSnackbar

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private val arguments: GameFragmentArgs by navArgs()

    private val gameViewModel by viewModels<GameViewModel> { getViewModelFactory() }

    private lateinit var viewBinding: GameFragmentBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate view and obtain an instance of the binding class
        viewBinding = GameFragmentBinding.inflate(inflater)

        // Set the viewmodel for databinding - this allows the bound layout access to all of the
        // data in the VieWModel
        viewBinding.gameViewModel = gameViewModel

        // Specify the current activity as the lifecycle owner of the binding. This is used so that
        // the binding can observe LiveData updates
        viewBinding.lifecycleOwner = viewLifecycleOwner

        observeUi()

        return viewBinding.root
    }

    private fun observeUi() {
        // Sets up event listening to navigate the player when the game is finished
        gameViewModel.eventGameFinish.observe(viewLifecycleOwner, { isFinished ->
            if (isFinished) {
                val currentScore = gameViewModel.score.value ?: 0
                findNavController(this).navigate(actionGameToScore(
                        currentScore,
                        arguments.category
                ))
                gameViewModel.onGameFinishComplete()
            }
        })

        // Buzzes when triggered with different buzz events
        gameViewModel.eventBuzz.observe(viewLifecycleOwner, { buzzType ->
            if (buzzType != GameViewModel.BuzzType.NO_BUZZ) {
                buzz(buzzType.pattern)
                gameViewModel.onBuzzComplete()
            }
        })
    }

    /**
     * Given a pattern, this method makes sure the device buzzes
     */
    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()
        buzzer?.let {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.setupSnackbar(viewLifecycleOwner, gameViewModel.snackbarText, Snackbar.LENGTH_LONG)
        gameViewModel.start(arguments.category)
    }
}
