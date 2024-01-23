package com.gdd.presentation.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupIdBinding


class SignupIdFragment : BaseFragment<FragmentSignupIdBinding>(
    FragmentSignupIdBinding::bind, R.layout.fragment_signup_id
) {
    private lateinit var signupActivity: SignupActivity
    private val activityViewModel: SignupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupActivity = _activity as SignupActivity

        initView()
        registerListener()
        registerObserver()
    }

    private fun initView(){
        binding.btnNext.isEnabled = false
    }

    private fun registerListener(){
        binding.btnNext.setOnClickListener {
            signupActivity.moveToNextPage()
        }

        binding.etId.editText!!.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                activityViewModel.isValidId(p0!!.toString())
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun registerObserver(){
        activityViewModel.idValidateResult.observe(viewLifecycleOwner){
            if (it){
                binding.btnNext.isEnabled = true
                binding.etId.helperText = resources.getString(R.string.signup_id_et_valid)
                binding.btnNext.isEnabled = true
            }else{
                binding.btnNext.isEnabled = false
                binding.etId.error = resources.getString(R.string.signup_all_et_err)
                binding.btnNext.isEnabled = false
            }
        }
    }

}