package edu.ap.padelpal.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import edu.ap.padelpal.R
import edu.ap.padelpal.databinding.FragmentProfileBinding
import jp.wasabeef.glide.transformations.BlurTransformation

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

// om een afbeelding als een cirkel in de ImageView weer te geven.
        Glide.with(this)
            .load(R.drawable.profile_img)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imageView)


// afbeelding wazig
        Glide.with(this)
            .load(R.drawable.padel_tournament)
            .transform(BlurTransformation(25))
            .into(binding.imgBlur)
        Glide.with(this)
            .load(R.drawable.padel_tournament)
            .transform(BlurTransformation(25))
            .into(binding.imgBlur2)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}