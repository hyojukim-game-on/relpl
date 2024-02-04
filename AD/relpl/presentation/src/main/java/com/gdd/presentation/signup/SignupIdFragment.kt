package com.gdd.presentation.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupIdBinding
import com.gdd.retrofit_adapter.RelplException
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SignupIdFragment_Genseong"
@AndroidEntryPoint
class SignupIdFragment : BaseFragment<FragmentSignupIdBinding>(
    FragmentSignupIdBinding::bind, R.layout.fragment_signup_id
) {
    private lateinit var signupActivity: SignupActivity
    private val activityViewModel: SignupViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
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
            val id = binding.etId.editText!!.text.toString().trim()
            activityViewModel.isDuplicatedId(id)
            binding.etId.isEnabled = false
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

        activityViewModel.idDupResult.observe(viewLifecycleOwner){result ->
            if (result.isSuccess){
                if (!result.getOrNull()!!){
                    showToast("사용 가능한 아이디입니다")
                    Log.d(TAG, "registerObserver: ${activityViewModel.id}")
                    signupActivity.moveToNextPage()
                }else{
                    showToast("중복된 아이디입니다")
                    binding.etId.isEnabled = true
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }
    }

}