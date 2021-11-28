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
import com.stockme.databinding.FragmentOrderPurchaseListBinding
import com.stockme.model.OrderPurchase

//import com.stockme.orderPurchaseDetail.OrderPurchaseDetail

class OrderPurchaseListFragment : Fragment() {
    private var _binding: FragmentOrderPurchaseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderPurchaseAdapter: OrderPurchaseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOrderPurchaseListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

  /*  private fun navigateToDetail(orderPurchaseId: String? = null) {
        val intent = Intent(context, OrderPurchaseDetailActivity::class.java)
        if (orderPurchase != null) intent.putExtra(OrderPurchaseDetailActivity.ORDER_PURCHASE_ID, orderPurchaseId)
        startActivity(intent)
    }*/

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
        binding.list.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        val orderPurchases : List<OrderPurchase> = listOf(
            OrderPurchase("1111111", "2021-01-01", "", "proveedor1"),
            OrderPurchase("2222222", "2021-01-01", "", "proveedor1"),
        )

        orderPurchaseAdapter = OrderPurchaseAdapter(orderPurchases)
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderPurchaseAdapter
        }

        binding.list.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu)
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                orderPurchaseAdapter.filter.filter(newText)
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
            Snackbar.make(binding.root, "OrderPurchaseo #" + result.contents, Snackbar.LENGTH_LONG).show()
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
        fun newInstance() = OrderPurchaseListFragment()
    }
}