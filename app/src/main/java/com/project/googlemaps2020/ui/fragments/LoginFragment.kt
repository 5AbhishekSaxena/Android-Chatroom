package com.project.googlemaps2020.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.project.googlemaps2020.R
import com.project.googlemaps2020.databinding.FragmentLoginBinding
import com.project.googlemaps2020.utils.Resource
import com.project.googlemaps2020.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: AuthViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyUserIsLoggedIn()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        setupOnClickListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    progressBar(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToChatsFragment()
                    )
                }

                is Resource.Error -> {
                    progressBar(false)
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    progressBar(true)
                }
            }
        })
    }

    private fun progressBar(visible: Boolean) {
        binding.apply {
            loginProgressBar.isVisible = visible
            btnLogin.isVisible = !visible
        }
    }

    private fun setupOnClickListeners() {
        binding.apply {
            tvSignUp.setOnClickListener {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                )
            }

            btnLogin.setOnClickListener {
                val email = etLoginEmail.text.toString()
                val password = etLoginPassword.text.toString()
                viewModel.loginUser(email, password)
            }
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = auth.uid
        if (uid != null) {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToChatsFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}