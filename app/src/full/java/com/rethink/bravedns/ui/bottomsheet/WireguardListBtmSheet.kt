package com.rethinkdns.retrixed.ui.bottomsheet

import Logger
import Logger.LOG_TAG_UI
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rethinkdns.retrixed.R
import com.rethinkdns.retrixed.database.AppInfo
import com.rethinkdns.retrixed.database.CustomDomain
import com.rethinkdns.retrixed.database.CustomIp
import com.rethinkdns.retrixed.database.WgConfigFilesImmutable
import com.rethinkdns.retrixed.databinding.BottomSheetProxiesListBinding
import com.rethinkdns.retrixed.databinding.ListItemProxyCcWgBinding
import com.rethinkdns.retrixed.service.DomainRulesManager
import com.rethinkdns.retrixed.service.IpRulesManager
import com.rethinkdns.retrixed.service.PersistentState
import com.rethinkdns.retrixed.service.ProxyManager.ID_WG_BASE
import com.rethinkdns.retrixed.util.Themes.Companion.getBottomsheetCurrentTheme
import com.rethinkdns.retrixed.util.Utilities
import com.rethinkdns.retrixed.util.Utilities.isAtleastQ
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class WireguardListBtmSheet(val type: InputType, val obj: Any?, val confs: List<WgConfigFilesImmutable?>, val listener: WireguardDismissListener) :
    BottomSheetDialogFragment() {
    private var _binding: BottomSheetProxiesListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val b
        get() = _binding!!

    private val persistentState by inject<PersistentState>()

    private val cd: CustomDomain? = if (type == InputType.DOMAIN) obj as CustomDomain else null
    private val ci: CustomIp? = if (type == InputType.IP) obj as CustomIp else null
    private val ai: AppInfo? = if (type == InputType.APP) obj as AppInfo else null

    companion object {
        fun newInstance(input: InputType, obj: Any?, data: List<WgConfigFilesImmutable?>, listener: WireguardDismissListener): WireguardListBtmSheet {
            return WireguardListBtmSheet(input, obj, data, listener)
        }

        private const val TAG = "WglBtmSht"
    }

    interface WireguardDismissListener {
        fun onDismissWg(obj: Any?)
    }

    enum class InputType(val id: Int) {
        DOMAIN (0),
        IP (1),
        APP (2)
    }

    override fun getTheme(): Int =
        getBottomsheetCurrentTheme(isDarkThemeOn(), persistentState.theme)

    private fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProxiesListBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let { window ->
            if (isAtleastQ()) {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.isAppearanceLightNavigationBars = false
                window.isNavigationBarContrastEnforced = false
            }
        }
        Logger.v(LOG_TAG_UI, "$TAG: view created")
        init()
    }

    private fun init() {
        //b.title.text = getString(R.string.select_wireguard_proxy)
        when (type) {
            InputType.DOMAIN -> {
                b.ipDomainInfo.visibility = View.VISIBLE
                b.ipDomainInfo.text = cd?.domain
            }
            InputType.IP -> {
                b.ipDomainInfo.visibility = View.VISIBLE
                b.ipDomainInfo.text = ci?.ipAddress
            }
            InputType.APP -> {
                // initApp()
            }
        }

        val lst = confs.map { it }
        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RecyclerViewAdapter(lst) { conf ->
            Logger.v(LOG_TAG_UI, "$TAG: Item clicked: ${conf?.name ?: "None"}")
            when (type) {
                InputType.DOMAIN -> {
                    processDomain(conf)
                }
                InputType.IP -> {
                    processIp(conf)
                }
                InputType.APP -> {
                    // processApp(selectedItem)
                }
            }
        }

        b.recyclerView.adapter = adapter
    }

    private fun processDomain(conf: WgConfigFilesImmutable?) {
        io {
            if (cd == null) {
                Logger.w(LOG_TAG_UI, "$TAG: Custom domain is null")
                return@io
            }
            if (conf == null) {
                DomainRulesManager.setProxyId(cd, "")
                cd.proxyId = ""
            } else {
                val id = ID_WG_BASE + conf.id
                DomainRulesManager.setProxyId(cd, id)
                cd.proxyId = id
            }
            val name = conf?.name ?: getString(R.string.settings_app_list_default_app)
            Logger.v(LOG_TAG_UI, "$TAG: wg-endpoint set to $name for ${cd.domain}")
            uiCtx {
                Utilities.showToastUiCentered(
                    requireContext(),
                    getString(R.string.config_add_success_toast),
                    Toast.LENGTH_SHORT
                )
            }
        }
    }

    private fun processIp(conf: WgConfigFilesImmutable?) {
        io {
            if (ci == null) {
                Logger.w(LOG_TAG_UI, "$TAG: Custom IP is null")
                return@io
            }
            if (conf == null) {
                IpRulesManager.updateProxyId(ci, "")
                ci.proxyId = ""
            } else {
                val id = ID_WG_BASE + conf.id
                IpRulesManager.updateProxyId(ci, id)
                ci.proxyId = id
            }
            val name = conf?.name ?: getString(R.string.settings_app_list_default_app)
            Logger.v(LOG_TAG_UI, "$TAG: wg-endpoint set to $name for ${ci.ipAddress}")
            uiCtx {
                Utilities.showToastUiCentered(
                    requireContext(),
                    getString(R.string.config_add_success_toast),
                    Toast.LENGTH_SHORT
                )
            }
        }
    }

    inner class RecyclerViewAdapter(
        private val data: List<WgConfigFilesImmutable?>,
        private val onItemClicked: (WgConfigFilesImmutable?) -> Unit
    ) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                ListItemProxyCcWgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount(): Int = data.size

        inner class ViewHolder(private val bb: ListItemProxyCcWgBinding) :
            RecyclerView.ViewHolder(bb.root) {

            fun bind(conf: WgConfigFilesImmutable?) {
                if (conf == null) {
                    bb.proxyNameCc.text = getString(R.string.settings_app_list_default_app)
                    bb.proxyDescCc.text = getString(R.string.settings_app_list_default_app)
                    when (type) {
                        InputType.DOMAIN -> {
                            bb.proxyRadioCc.isChecked = cd?.proxyId?.isEmpty() == true
                        }

                        InputType.IP -> {
                            bb.proxyRadioCc.isChecked = ci?.proxyId?.isEmpty() == true
                        }

                        InputType.APP -> {
                            // bb.endpointCheck.isChecked = item == appInfo?.proxyId
                        }
                    }
                } else {
                    bb.proxyNameCc.text = conf.name
                    bb.proxyDescCc.text = if (conf.isActive) getString(R.string.lbl_active) else getString(R.string.lbl_inactive)

                    val id = ID_WG_BASE + conf.id
                    when (type) {
                        InputType.DOMAIN -> {
                            bb.proxyRadioCc.isChecked = id == cd?.proxyId
                        }

                        InputType.IP -> {
                            bb.proxyRadioCc.isChecked = id == ci?.proxyId
                        }

                        InputType.APP -> {
                            // bb.endpointCheck.isChecked = item == appInfo?.proxyId
                        }
                    }
                }
                bb.lipCcWgParent.setOnClickListener {
                    onItemClicked(conf)
                    notifyDataSetChanged()
                }

                bb.proxyRadioCc.setOnClickListener {
                    onItemClicked(conf)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun io(f: suspend () -> Unit) {
        (requireContext() as LifecycleOwner).lifecycleScope.launch(Dispatchers.IO) { f() }
    }

    private suspend fun uiCtx(f: suspend () -> Unit) {
        withContext(Dispatchers.Main) { f() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Logger.v(LOG_TAG_UI, "$TAG: Dismissed, input: ${type.name}")
        when (type) {
            InputType.DOMAIN -> {
                listener.onDismissWg(cd)
            }
            InputType.IP -> {
                listener.onDismissWg(ci)
            }
            InputType.APP -> {
                listener.onDismissWg(ai)
            }
        }
    }

}
