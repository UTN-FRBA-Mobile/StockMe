package com.stockme.order

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.stockme.R
import com.stockme.databinding.FragmentOrderPurchaseDetailBinding
import com.stockme.model.OrderPurchase
import com.stockme.model.ProductOrderPurchase
import com.stockme.model.Product
import kotlin.random.Random

//import com.stockme.orderPurchaseDetail.OrderPurchaseDetail

class OrderPurchaseDetailFragment : Fragment() {
    private var _binding: FragmentOrderPurchaseDetailBinding? = null
    private val binding get() = _binding!!
    var args = arguments
    private lateinit var orderPurchase: OrderPurchase
    private lateinit var orderPurchaseDetailAdapter: OrderPurchaseDetailAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOrderPurchaseDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mPrueba = args?.getSerializable("orderPurchase")
        orderPurchase = (args?.getSerializable("orderPurchase") as OrderPurchase ?)!!

        binding.fab.setOnClickListener {
            // navigateToDetail()
        }

    }

    override fun onStart() {
        super.onStart()

        binding.productList.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        /*val products : List<ProductOrderPurchase> = listOf(
            ProductOrderPurchase(Product(id = "0RXinwZOhmzRf2tJzgQU", code = "1111111", description = "Producto 1", price = "11.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),123),
            ProductOrderPurchase(Product(id = "NX5yZjFTqjqBSkrbg6yI", code = "2222222", description = "Producto 2", price = "12.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),32),
            ProductOrderPurchase(Product(id = "a6c48369-035f-4a33-91f4-e65507e6c1d0", code = "3333333", description = "Producto 3", price = "13.0", currentStock = Random.nextInt(1, 100), minStock = 1, maxStock = 100),42),
        )

        val orderPurchases : List<OrderPurchase> = listOf(
            OrderPurchase("1111111", "2021-01-01", "", "proveedor1", ArrayList<ProductOrderPurchase>(products)),
            OrderPurchase("2222222", "2021-01-01", "", "proveedor1",ArrayList<ProductOrderPurchase>(products)),
        )*/

        orderPurchaseDetailAdapter = OrderPurchaseDetailAdapter(orderPurchase)
        binding.productList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderPurchaseDetailAdapter
        }

        binding.productList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu)
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
        fun newInstance() = OrderPurchaseDetailFragment()
    }
}