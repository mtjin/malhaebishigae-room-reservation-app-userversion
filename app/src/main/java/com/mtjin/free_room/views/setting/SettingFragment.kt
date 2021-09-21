package com.mtjin.free_room.views.setting

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.databinding.FragmentSettingBinding
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.views.dialog.BottomDialogFragment
import com.mtjin.free_room.views.main.MainActivity
import javax.inject.Inject

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SettingViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SettingViewModel::class.java)
    }

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = viewModel
        initView()
        initViewModelCallback()
    }

    private fun initView() {
        viewModel.initBusinessCode()
        binding.swAlarm.isChecked = viewModel.getSettingAlarm()
        binding.swAlarm.setOnCheckedChangeListener { p0, isChecked ->
            viewModel.setSettingAlarm(isChecked)
        }
    }

    private fun initViewModelCallback() {
        with(viewModel) {
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
                        viewModel.deleteCache()
                    }
                }.show(requireActivity().supportFragmentManager, "settingTag")
            })

            businessCodeResponse.observe(this@SettingFragment, { state ->
                when (state) {
                    1 -> { // 빈 코드인 경우
                        showToast(getString(R.string.business_code_input_msg))
                    }
                    2 -> { // 수정 완료
                        showToast(getString(R.string.business_code_success_msg))
                    }
                }

            })
        }
    }
}
