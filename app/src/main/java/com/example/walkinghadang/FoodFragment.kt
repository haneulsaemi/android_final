package com.example.walkinghadang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walkinghadang.databinding.FragmentFoodBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private val apiKey = "jbGHJMq1TOHOzoWuOUcatgnyzGotAsC4yLEGOdHKw3GGF2RHmcgtdwINtemfO+7hzBtF/fysjVsDm1+GGUU5nQ=="

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
        // Inflate the layout for this fragment
        _binding = FragmentFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchFoods("")
        binding.btnFoodSearch.setOnClickListener {
            val keyword = binding.edtFoodKeyword.text.toString()
            Log.e("Food_Keyword", "keyword : ${keyword}")
            if (keyword.isBlank()) {
                Toast.makeText(requireContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                fetchFoods(keyword)
            }
        }
    }

    private fun fetchFoods(keyword: String) {
        FoodRetrofit.foodApiService.getFoods(
            serviceKey = apiKey,
            keyword = keyword,
            pageNo = 1,
            numOfRows = 100,
            type = "json"
        ).enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {

                if (response.isSuccessful) {
                    val items = response.body()?.body?.items ?: emptyList()
                    val filtered = if (keyword.isNotBlank()) {
                        items.filter {
                            it.FOOD_NM_KR?.contains(keyword, ignoreCase = true) == true
                        }
                    } else {
                        items
                    }
                    binding.foodRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.foodRecyclerView.adapter = FoodAdapter(filtered)
                    Toast.makeText(requireContext(), "전체 식품 ${filtered.size ?: 0}개 표시", Toast.LENGTH_SHORT).show()


                } else {
                    Toast.makeText(requireContext(), "API 응답 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                Log.e("API_ERROR", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
         * @return A new instance of fragment FoodFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FoodFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}