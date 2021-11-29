package com.stockme.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.stockme.R
import com.stockme.databinding.FragmentOrderPurchaseDetailBinding
import com.stockme.model.ProductOrderPurchase
import com.stockme.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.random.Random

//import com.stockme.orderPurchaseDetail.OrderPurchaseDetail

class ProductOrderPurchaseListFragment : Fragment() {
    private var _binding: FragmentOrderPurchaseDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var productOrderPurchaseAdapter: ProductOrderPurchaseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOrderPurchaseDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            // navigateToDetail()
        }
    /*    binding.orderPurchaseList.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                binding.orderPurchaseList,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val orderPurchase = orderPurchaseAdapter.orderPurchaseAdapterListFiltered[position]
                        navigateToDetail(orderPurchase.id)
                        // Snackbar.make(binding.root, "Click " + product.description, Snackbar.LENGTH_LONG).show()
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        // Snackbar.make(binding.root, "Long Click", Snackbar.LENGTH_LONG).show()
                    }
                })
        )*/
    }

    override fun onStart() {
        super.onStart()
        binding.productList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        val productsOrderPurchase : List<ProductOrderPurchase> = listOf(
            ProductOrderPurchase(Product(id = "0RXinwZOhmzRf2tJzgQU", code = "1111111", description = "Producto 1", price = "11.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),123),
            ProductOrderPurchase(Product(id = "NX5yZjFTqjqBSkrbg6yI", code = "2222222", description = "Producto 2", price = "12.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),32),
            ProductOrderPurchase(Product(id = "a6c48369-035f-4a33-91f4-e65507e6c1d0", code = "3333333", description = "Producto 3", price = "13.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),42),
        )

        productOrderPurchaseAdapter = ProductOrderPurchaseAdapter(productsOrderPurchase)
        binding.productList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productOrderPurchaseAdapter
        }

        binding.productList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu)
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                productOrderPurchaseAdapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean = false
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_scan -> {
                initScanner()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initScanner() {
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        // Resultado del scanner
        if (result.contents == null) {
            Snackbar.make(binding.root, "Cancelado", Snackbar.LENGTH_LONG).show()
        }
        else {
            Snackbar.make(binding.root, "OrderPurchase #" + result.contents, Snackbar.LENGTH_LONG).show()
           /* val orderPurchase = orderPurchaseAdapter.orderPurchases().find { it.code == result.contents }
            if (orderPurchase != null) navigateToDetail(orderPurchase.id)
            else Snackbar.make(binding.root, "Order de Compra #" + result.contents + " no encontrada", Snackbar.LENGTH_LONG).show()
       */ }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProductOrderPurchaseListFragment()
    }
}