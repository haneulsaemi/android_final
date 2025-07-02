package com.example.walkinghadang

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walkinghadang.databinding.FragmentJsonBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JsonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JsonFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    val TAG = "25android"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentJsonBinding.inflate(inflater, container, false)
        val gilNm = arguments?.getString("gilNm")
        val lvCd = arguments?.getString("lvCd")
        val reqTm = arguments?.getString("reqTm")

        val call: Call<JsonResponse> = RetrofitConnection.jsonSeoulApiService.getJsonList(
            "67764568767373613836584a447979",
            "json",
            "viewGil",
            1,
            100
        )

        call?.enqueue(object:Callback<JsonResponse>{
            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                if(response.isSuccessful){
                    var rows = response.body()?.viewGil?.row ?: mutableListOf()
                    val filteredRows = rows.filter {
                        when {
                            !gilNm.isNullOrEmpty() -> it.GIL_NM.contains(gilNm, ignoreCase = true)
                            !lvCd.isNullOrEmpty() -> it.LV_CD.contains(lvCd, ignoreCase = true)
                            !reqTm.isNullOrEmpty() -> it.REQ_TM.contains(reqTm, ignoreCase = true)
                            else -> true
                        }
                    }.toMutableList()
                    Log.d(TAG,"${response.body()}")
                    binding.jsonRecyclerView.layoutManager = LinearLayoutManager(activity)
                    binding.jsonRecyclerView.adapter = JsonAdapter(filteredRows.toMutableList())
                    binding.jsonRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                }else{
                    Log.d(TAG, "${response}")
                }
            }
            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
//                Log.e(TAG, "Network Error", t)
                Log.d(TAG, "${t.printStackTrace()}")

            }
        })

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment JsonFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            JsonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}