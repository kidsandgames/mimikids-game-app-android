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

package games.kidsand.mimikids.app.android.screens.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for the final screen showing the score
 */
class ScoreViewModel : ViewModel() {

    private val _eventPlayAgain = MutableLiveData(false)
    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    fun onPlayAgain() {
        _eventPlayAgain.value = true
    }

    fun onPlayAgainComplete() {
        _eventPlayAgain.value = false
    }

    fun initialize(finalScore: Int) {
        _score.value = finalScore
    }
}
