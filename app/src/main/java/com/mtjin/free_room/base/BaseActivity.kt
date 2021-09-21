package com.mtjin.free_room.base

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mtjin.free_room.views.dialog.LottieDialogFragment
import com.mtjin.free_room.R
import com.pd.chocobar.ChocoBar
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : AppCompatActivity() {
    lateinit var binding: B
    protected val compositeDisposable = CompositeDisposable()
    private val loadingDialogFragment by lazy { LottieDialogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }

    protected fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showNegativeSnackBar(msg: String) {
        ChocoBar.builder().setView(findViewById(R.id.content))
            .setText(msg)
            .setBackgroundColor(Color.parseColor("#00B0FF"))
            .centerText()
            .setDuration(ChocoBar.LENGTH_SHORT)
            .bad()
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

