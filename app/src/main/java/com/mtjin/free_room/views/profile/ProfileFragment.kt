package com.mtjin.free_room.views.profile

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.databinding.FragmentProfileBinding
import com.mtjin.free_room.utils.uuid
import com.mtjin.free_room.views.main.MainActivity
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
    }

    override fun init() {
        (activity as MainActivity).mainComponent.inject(this)
        binding.vm = viewModel
        initQrCode()
        initViewModelCallback()
        requestProfile()
    }

    private fun requestProfile() {
        viewModel.requestProfile()
    }

    private fun initQrCode() {
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix =
            multiFormatWriter.encode(uuid, BarcodeFormat.QR_CODE, 270, 270)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        binding.ivQrCode.setImageBitmap(bitmap)
        viewModel.qrCodeBitmap = bitmap
    }

    private fun initViewModelCallback() {
        with(viewModel) {
            networkState.observe(this@ProfileFragment, Observer {
                showInfoSnackBar(getString(R.string.network_err_then_load_local_text))
            })

            reviseState.observe(this@ProfileFragment, Observer { state ->
                if (state) {
                    binding.tvId.isFocusableInTouchMode = true
                    binding.tvPw.isFocusableInTouchMode = true
                } else {
                    binding.tvId.clearFocus()
                    binding.tvPw.clearFocus()
                    binding.tvId.isFocusableInTouchMode = false
                    binding.tvPw.isFocusableInTouchMode = false
                }
            })

            idPwEmptyMsg.observe(this@ProfileFragment, Observer {
                showNegativeSnackBar(getString(R.string.enter_id_pw_text))
            })

            reviseFailMsg.observe(this@ProfileFragment, Observer {
                showNegativeSnackBar(getString(R.string.id_duplicate_or_error_text))
            })

            reviseSuccessMsg.observe(this@ProfileFragment, Observer {
                showPositiveSnackBar(getString(R.string.profile_update_success_text))
            })

            isLottieLoading.observe(this@ProfileFragment, Observer { loadingState ->
                when (loadingState) {
                    true -> showProgressDialog()
                    false -> hideProgressDialog()
                }
            })

            saveQrCodeResultMsg.observe(this@ProfileFragment, Observer { result ->
                if (result) showPositiveSnackBar(getString(R.string.qr_code_save_success_text))
                else showNegativeSnackBar(getString(R.string.qr_code_save_fail_text))
            })

            permissionResultMsg.observe(this@ProfileFragment, Observer { result ->
                if (!result) showToast(getString(R.string.image_download_permission_denied_text))
            })

            idPwOverLengthMsg.observe(this@ProfileFragment, Observer {
                showNegativeSnackBar(getString(R.string.id_pw_length_max_text))
            })
        }
    }
}