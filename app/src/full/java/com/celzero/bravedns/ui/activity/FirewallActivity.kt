/*
Copyright 2020 RethinkDNS and its authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.rethinkdns.retrixed.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rethinkdns.retrixed.R
import com.rethinkdns.retrixed.databinding.ActivityFirewallBinding
import com.rethinkdns.retrixed.service.BraveVPNService
import com.rethinkdns.retrixed.service.PersistentState
import com.rethinkdns.retrixed.service.VpnController
import com.rethinkdns.retrixed.ui.fragment.FirewallSettingsFragment
import com.rethinkdns.retrixed.util.Themes.Companion.getCurrentTheme
import com.rethinkdns.retrixed.util.Utilities.isAtleastQ
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject

class FirewallActivity : AppCompatActivity(R.layout.activity_firewall) {
    private val b by viewBinding(ActivityFirewallBinding::bind)
    private val persistentState by inject<PersistentState>()

    enum class Tabs(val screen: Int) {
        UNIVERSAL(0);

        companion object {
            fun getCount(): Int {
                return entries.count()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getCurrentTheme(isDarkThemeOn(), persistentState.theme))
        super.onCreate(savedInstanceState)
        if (isAtleastQ()) {
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.isAppearanceLightNavigationBars = false
            window.isNavigationBarContrastEnforced = false
        }

        init()
    }

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
    }

    private fun init() {

        b.firewallActViewpager.adapter =
            object : FragmentStateAdapter(this) {
                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        Tabs.UNIVERSAL.screen -> FirewallSettingsFragment.newInstance()
                        else -> FirewallSettingsFragment.newInstance()
                    }
                }

                override fun getItemCount(): Int {
                    return Tabs.getCount()
                }
            }

        TabLayoutMediator(b.firewallActTabLayout, b.firewallActViewpager) { tab, position
                -> // Styling each tab here
                tab.text =
                    when (position) {
                        Tabs.UNIVERSAL.screen -> getString(R.string.firewall_act_universal_tab)
                        else -> getString(R.string.firewall_act_universal_tab)
                    }
            }
            .attach()

        observeAppState()
    }

    private fun observeAppState() {
        VpnController.connectionStatus.observe(this) {
            if (it == BraveVPNService.State.PAUSED) {
                startActivity(Intent().setClass(this, PauseActivity::class.java))
                finish()
            }
        }
    }
}
