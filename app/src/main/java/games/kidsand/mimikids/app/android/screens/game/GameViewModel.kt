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

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import games.kidsand.mimikids.app.android.Event
import games.kidsand.mimikids.app.android.R
import games.kidsand.mimikids.app.android.data.model.GuessWord
import games.kidsand.mimikids.app.android.data.source.remote.GameApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

/**
 * ViewModel containing all the logic needed to run the game
 */
class GameViewModel : ViewModel() {

    // These are the three different types of buzzing in the game. Buzz pattern is the number of
    // milliseconds each interval of buzzing and non-buzzing takes.
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    companion object {
        // These represent different important times in the game, such as game length.

        // This is when the game is over
        private const val DONE = 0L

        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L

        // This is the number of milliseconds in a second
        private const val ONE_SECOND = 1000L

        // This is the total time of the game
        private const val COUNTDOWN_TIME = 60000L

    }

    private val timer: CountDownTimer

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    // The String version of the current time
    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    private val originalWords = MutableLiveData<List<String>>(emptyList())

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    // Event which triggers the end of the game
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    // Event that triggers the phone to buzz using different patterns, determined by BuzzType
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    init {
        _score.value = 0

        // Creates a timer which triggers the end of the game when it finishes
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventBuzz.value = BuzzType.GAME_OVER
                _eventGameFinish.value = true
            }
        }

        loadWords()
    }

    private fun loadWords() {
        val locale = Locale.getDefault().toString()
        GameApi.retrofitService.guess("animals", locale).enqueue(object : Callback<GuessWord> {
            override fun onResponse(call: Call<GuessWord>, response: Response<GuessWord>) {
                originalWords.value = response.body()?.words ?: emptyList()
                start()
            }

            override fun onFailure(call: Call<GuessWord>, error: Throwable) {
                Timber.e("Failure loading guess words: ${error.message}")
                showSnackbarMessage(R.string.loading_words_error)
            }
        })
    }

    private fun start() {
        resetList()
        nextWord()
        timer.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = originalWords.value!!.toMutableList()
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = (_score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    /** Methods for completed events **/

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}
