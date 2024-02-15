package com.gdd.presentation.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.gdd.presentation.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlin.reflect.jvm.jvmName

abstract class BaseFragment<B : ViewBinding>(
    private val bind: (View) -> B,
    @LayoutRes layoutResId: Int
) : Fragment(layoutResId) {
    private var _binding: B? = null
    protected val binding get() = _binding!!
    protected lateinit var _activity: Context
    @SuppressLint("ResourceType")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        _activity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bind(super.onCreateView(inflater, container, savedInstanceState)!!)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        return binding.root
    }

    fun hideKeyboard(){
        val inputManager: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun showToast(message: String) {
        Toast.makeText(_activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(message: String){
        Snackbar.make(binding.root,message,Snackbar.LENGTH_SHORT).show()
    }

    /**
     * life cycle check
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LIFECYCLE", "${_binding?.javaClass} : CREATE")
    }

    override fun onStart() {
        super.onStart()
        lifecycleObserve.onStateChanged(viewLifecycleOwner,Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleObserve.onStateChanged(viewLifecycleOwner,Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        super.onPause()
        lifecycleObserve.onStateChanged(viewLifecycleOwner,Lifecycle.Event.ON_PAUSE)
    }

    override fun onStop() {
        super.onStop()
        lifecycleObserve.onStateChanged(viewLifecycleOwner,Lifecycle.Event.ON_STOP)
        if (_activity is MainActivity){
            (_activity as MainActivity).dismissLoadingView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LIFECYCLE", "${_binding?.javaClass} : Destory")
    }

    private val lifecycleObserve = LifecycleEventObserver { source, event ->
        Log.d("LIFECYCLE", "${_binding?.javaClass} : $event")
    }


}