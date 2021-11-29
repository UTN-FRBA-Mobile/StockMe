package com.stockme.home.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stockme.R
import com.stockme.databinding.FragmentSalesBinding
import com.stockme.model.Product
import com.stockme.product.ProductAdapter
import com.stockme.product.RecyclerItemClickListener

class SalesFragment : Fragment() {

    private lateinit var viewModel: SalesViewModel
    private var _binding: FragmentSalesBinding? = null

    private val binding get() = _binding!!

    private lateinit var productAdapter: SalesProductAdapter

    private val products = ArrayList<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(SalesViewModel::class.java)

        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onStart() {
        super.onStart()
        binding.productList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchNotFound.root.visibility = View.GONE

        viewModel.fetchProducts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.productList.addOnItemTouchListener(
                RecyclerItemClickListener(
                        context,
                        binding.productList,
                        object : RecyclerItemClickListener.OnItemClickListener {
                            override fun onItemClick(view: View?, position: Int) {
//                                val product = productAdapter.productListFiltered[position]
//                                navigateToDetail(product.id)
                            }

                            override fun onLongItemClick(view: View?, position: Int) { }
                        })
        )

        setupObserver()
    }


    private fun setupObserver() {
        viewModel.fetchProductsLiveData.observe(viewLifecycleOwner) {
            binding.productList.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE

            if (it != null) {
                products.clear()
                products.addAll(it)
                productAdapter = SalesProductAdapter(products)
                binding.productList.apply {
                    adapter = productAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                    itemAnimator = DefaultItemAnimator()
                }
            } else {
                Snackbar.make(binding.root, R.string.product_detail_error, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}