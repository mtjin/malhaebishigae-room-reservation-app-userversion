package com.mtjin.free_room.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.mtjin.free_room.views.dialog.LottieDialogFragment
import com.mtjin.free_room.R
import com.pd.chocobar.ChocoBar
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : Fragment() {
    lateinit var binding: B
    protected lateinit var thisContext: Context
    protected val compositeDisposable = CompositeDisposable()
    private val loadingDialogFragment by lazy { LottieDialogFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        thisContext = inflater.context
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    abstract fun init()

    protected fun showToast(msg: String) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    protected fun showPositiveSnackBar(msg: String) {
        ChocoBar.builder().setView(activity?.findViewById(R.id.content))
            .setText(msg)
            .setBackgroundColor(Color.parseColor("#00B0FF"))
            .centerText()
            .setDuration(ChocoBar.LENGTH_SHORT)
            .good()
            .show()
    }

    protected fun showNegativeSnackBar(msg: String) {
        ChocoBar.builder().setView(activity?.findViewById(R.id.content))
            .setText(msg)
            .setBackgroundColor(Color.parseColor("#00B0FF"))
            .centerText()
            .setDuration(ChocoBar.LENGTH_SHORT)
            .bad()
            .show()
    }

    protected fun showInfoSnackBar(msg: String) {
        ChocoBar.builder().setView(activity?.findViewById(R.id.content))
            .setText(msg)
            .setBackgroundColor(Color.parseColor("#00B0FF"))
            .setTextColor(Color.parseColor("#FFFFFF"))
            .setIcon(R.drawable.ic_info_white_24dp)
            .centerText()
            .setDuration(ChocoBar.LENGTH_SHORT)
            .orange()
            .show()
    }

    fun showProgressDialog() {
        if (!loadingDialogFragment.isAdded) {
            activity?.supportFragmentManager?.let {
                loadingDialogFragment.show(
                    it,
                    "lottieLoading"
                )
            }
        }
    }

    fun hideProgressDialog() {
        if (loadingDialogFragment.isAdded) {
            loadingDialogFragment.dismissAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
    }

}