package com.example.thirdeye.ui.onboarding.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thirdeye.data.localData.PlansData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repo: PlansRepo
): ViewModel(){

    private val _plans= MutableStateFlow<List<PlansData>>(emptyList())
    val plans= _plans.asStateFlow()

    private val _selectedPlan=MutableStateFlow<PlansData?>(null)
    val selectedPlan=_selectedPlan.asStateFlow()

    fun loadPlans(){
        viewModelScope.launch {
            _plans.value=repo.getPlans()

        }
    }
    fun selectedPlan(plan: PlansData){
        _selectedPlan.value=plan
    }



}