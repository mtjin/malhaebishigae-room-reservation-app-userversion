package com.mtjin.free_room.views.setting

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.databinding.FragmentSettingBinding
import com.mtjin.free_room.views.dialog.BottomDialogFragment
import com.mtjin.free_room.views.main.MainActivity
import javax.inject.Inject

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val settingViewModel: SettingViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SettingViewModel::class.java)
    }
    private val profileViewModel: SettingViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SettingViewModel::class.java)
    }

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = settingViewModel
        initView()
        initViewModelCallback()
    }

    private fun initView() {
        settingViewModel.initBusinessCode()
        binding.swAlarm.isChecked = settingViewModel.getSettingAlarm()
        binding.swAlarm.setOnCheckedChangeListener { p0, isChecked ->
            settingViewModel.setSettingAlarm(isChecked)
        }
    }

    private fun initViewModelCallback() {
        with(settingViewModel) {
            showLogoutDialog.observe(this@SettingFragment, Observer {
                BottomDialogFragment(getString(R.string.logout_question_text)) { yes ->
                    if (yes) {
                        FirebaseAuth.getInstance().signOut()
                        showToast(getString(R.string.logout_text))
                        findNavController().navigate(SettingFragmentDirections.actionBottomNav4ToLoginActivity())
                    }
                }.show(requireActivity().supportFragmentManager, "settingTag")
            })

            deleteCacheMsg.observe(this@SettingFragment, Observer {
                showPositiveSnackBar(getString(R.string.delete_cache_data_text))
            })

            showDeleteCacheDialog.observe(this@SettingFragment, Observer {
                BottomDialogFragment(getString(R.string.cache_delete_question_text)) { yes ->
                    if (yes) {
                        settingViewModel.deleteCache()
                    }
                }.show(requireActivity().supportFragmentManager, "settingTag")
            })

            businessCodeResponse.observe(this@SettingFragment, { state ->
                when (state) {
                    SettingViewModel.SaveBusinessCodeResponse.EMPTY_INPUT -> {
                        showToast(getString(R.string.business_code_input_msg))
                    }
                    SettingViewModel.SaveBusinessCodeResponse.SUCCESS -> {
                        showToast(getString(R.string.business_code_success_msg))
                    }
                    SettingViewModel.SaveBusinessCodeResponse.FAILURE -> {
                        showToast("오류가 발생했습니다.\n없는 비즈니스코드는 등록이 안됩니다.\ntmddjs210@naver.com에 등록문의부탁드립니다.")
                    }
                    else -> {
                    }
                }

            })
        }
    }
}
