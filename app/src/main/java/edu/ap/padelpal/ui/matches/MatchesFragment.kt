package edu.ap.padelpal.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.ap.padelpal.databinding.FragmentMatchesBinding

class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(MatchesViewModel::class.java)

        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        val root: View = binding.root






        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}