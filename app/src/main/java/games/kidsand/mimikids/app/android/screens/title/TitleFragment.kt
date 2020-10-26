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

package games.kidsand.mimikids.app.android.screens.title

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import games.kidsand.mimikids.app.android.R
import games.kidsand.mimikids.app.android.databinding.TitleFragmentBinding
import games.kidsand.mimikids.app.android.screens.title.TitleFragmentDirections.Companion.actionTitleToGame

/**
 * Fragment for the starting or title screen of the app
 */
class TitleFragment : Fragment() {

    private val titleViewModel: TitleViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = TitleFragmentBinding.inflate(inflater)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = titleViewModel
                observeUi()
            }.root

    private fun observeUi() {
        observeSelectCategory()
        observeNavigateToGame()
    }

    private fun observeSelectCategory() {
        titleViewModel.selectCategory.observe(viewLifecycleOwner) {
            it?.let {
                MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.select_category)
                        .setItems(titleViewModel.categories) { _, which ->
                            titleViewModel.onCategorySelected(which)
                        }
                        .show()
            }
        }
    }

    private fun observeNavigateToGame() {
        titleViewModel.navigateToGame.observe(viewLifecycleOwner) { category ->
            category?.let {
                findNavController().navigate(actionTitleToGame(category))
                titleViewModel.onGameNavigated()
            }
        }
    }
}
