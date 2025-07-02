package com.example.walkinghadang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.walkinghadang.databinding.FragmentGilBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GilFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentGilBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGilBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadJsonFragment("", "GIL_NM")

        binding.btnSearch.setOnClickListener {
            val keyword = binding.edtLoc.text.toString()
            if (keyword.isBlank()) {
                Toast.makeText(requireContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedId = binding.rGroup.checkedRadioButtonId
            val searchType = when (selectedId) {
                R.id.rbGil -> "GIL_NM"
                R.id.rbLvl -> "LV_CD"
                R.id.rbTime -> "REQ_TM"
                else -> "GIL_NM"
            }

            loadJsonFragment(keyword, searchType)
        }
    }

    private fun loadJsonFragment(keyword: String, searchType: String) {
        val jsonFragment = JsonFragment()
        val bundle = Bundle()

        when (searchType) {
            "GIL_NM" -> bundle.putString("gilNm", keyword)
            "LV_CD" -> bundle.putString("lvCd", keyword)
            "REQ_TM" -> bundle.putString("reqTm", keyword)
        }

        jsonFragment.arguments = bundle

        childFragmentManager.beginTransaction()
            .replace(R.id.activity_content, jsonFragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}