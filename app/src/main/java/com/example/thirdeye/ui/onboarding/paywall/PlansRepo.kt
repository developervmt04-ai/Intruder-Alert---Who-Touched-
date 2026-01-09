package com.example.thirdeye.ui.onboarding.paywall

import com.example.thirdeye.data.localData.PlansData

class PlansRepo {


    suspend fun getPlans():List<PlansData>{

        return listOf(
            PlansData(
                id = "plan_week",
                duration = "1 week",
                description = "Rs 100 per week",
                price = "PKR 650"
            ),
            PlansData(
                id = "plan_month",
                duration = "1 month",
                description = "Rs 400 per month",
                price = "PKR 2400"
            ),
            PlansData(
                id = "plan_3_month",
                duration = "3 months",
                description = "Rs 1000 per 3 months",
                price = "PKR 3000"
            )
        )
    }

}