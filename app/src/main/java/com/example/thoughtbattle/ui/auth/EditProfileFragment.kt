package com.example.thoughtbattle.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.thoughtbattle.R
import com.example.thoughtbattle.databinding.FragmentEditProfileBinding
import com.example.thoughtbattle.ui.MainViewModel
import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.RequiresApi
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.thoughtbattle.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseUser


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private val viewModel: MainViewModel by activityViewModels()
    val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        // Callback invoked after media selected or picker activity closed.
        if (uri != null) {
            Log.d("photo picker", "Selected URI: $uri")
            imageUri = uri
            binding.profileImage.setImageURI(uri)
        } else {
            Log.d("photo picker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        viewModel.loadCurrentUserProfile()
    }



    private fun setupObservers() {
        viewModel.observeUserProfile().observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.displayNameEditText.setText(FirebaseAuth.getInstance().currentUser!!.displayName, TextView.BufferType.EDITABLE)
                Glide.with(this)
                    .load(it.profileImageUrl)
                    .into(binding.profileImage)
            }
        }

        viewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.saveButton.isEnabled = !isLoading
        }

        viewModel.errorState.observe(viewLifecycleOwner) { error ->
            error?.takeIf { it.isNotEmpty() }?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.changePhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest.Builder()
                .setMediaType(PickVisualMedia.ImageOnly)
                .build())
        }


        binding.saveButton.setOnClickListener {
            if (validateForm()) {

                viewModel.updateProfile(
                    binding.displayNameEditText.text.toString(),
                    imageUri
                )

                viewModel.loadingState.observe(viewLifecycleOwner){
                    if(!it){
                        binding.progressBar.visibility = View.GONE
                        viewModel.setUserId(viewModel.currentAuthUser.value!!.id)
                        val args = Bundle()
                        args.putString("PROFILE_USER_ID", viewModel.currentAuthUser.value?.id)
                        findNavController().navigate(R.id.action_edit_profile_to_profile,args)
                    } else{
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }

            }
        }
    }

    private fun validateForm(): Boolean {
        return if (binding.displayNameEditText.text.isNullOrBlank()) {
            binding.displayNameInput.error = getString(R.string.display_name_required)
            false
        } else {
            binding.displayNameInput.error = null
            true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}