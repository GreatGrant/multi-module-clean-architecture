package com.example.presentation.ui.item

import androidx.lifecycle.viewModelScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import mvi.Executor
import mvi.ViewModelStore
import org.kumnan.aos.apps.domain.interactor.GetItemListUseCase
import org.kumnan.aos.apps.domain.interactor.InsertItemUseCase
import org.kumnan.aos.apps.domain.model.Item
import javax.inject.Inject

@HiltViewModel
class ItemViewModel @Inject constructor(
    override val initialState: ItemState,
    private val getItemUseCase: GetItemListUseCase,
    private val insertItemUseCase: InsertItemUseCase,
) : ViewModelStore<ItemIntent, ItemState, ItemMessage>() {

    init {
    	viewModelScope.launch {
    	    accept(ItemIntent.ObserveItems)
        }
    }

    override fun Executor<ItemIntent, ItemMessage>.onIntent(intent: ItemIntent) {
        when (intent) {
            is ItemIntent.ObserveItems -> getItemUseCase()
                .onEach { dispatch(ItemMessage.Fetched(it)) }
                .launchIn(viewModelScope)

            is ItemIntent.InsertItem -> viewModelScope.launch {
                insertItemUseCase(intent.item)
            }
        }
    }

    override fun reduce(state: ItemState, message: ItemMessage): ItemState = when (message) {
        is ItemMessage.Fetched -> state.copy(items = message.data)
    }
}