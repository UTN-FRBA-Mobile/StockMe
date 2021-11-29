package com.stockme.order

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.stockme.R
import com.stockme.model.ProductOrderPurchase

class ProductOrderPurchaseAdapter (private val orderPurchaseDetail: List<ProductOrderPurchase>):
    RecyclerView.Adapter<ProductOrderPurchaseAdapter.ViewHolder>(), Filterable {

    private var productOrderPurchaseListFiltered: ArrayList<ProductOrderPurchase> = ArrayList(orderPurchaseDetail)

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_order_purchase_detail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductOrderPurchaseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductOrderPurchaseAdapter.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_order_purchase_detail -> {
                holder.itemView.findViewById<TextView>(R.id.descriptionText).text = productOrderPurchaseListFiltered[position].product.description
                holder.itemView.findViewById<TextView>(R.id.codeText).text = ("#" + productOrderPurchaseListFiltered[position].product.code)
                holder.itemView.findViewById<TextView>(R.id.stockOrdered).text    = (productOrderPurchaseListFiltered[position].stockOrdered.toString()    + " UN")
                if (productOrderPurchaseListFiltered[position].product.image != null) {
                    Picasso.get().load(Uri.parse(productOrderPurchaseListFiltered[position].product.image)).into(holder.itemView.findViewById<ImageView>(R.id.image))
                }
            }
            else -> {}
        }
    }
    override fun getItemCount(): Int = productOrderPurchaseListFiltered.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                var OrderPurchaseList = orderPurchaseDetail
                if (charSearch.isNotEmpty()) OrderPurchaseList = OrderPurchaseList.filter { productOrderPurchase ->
                    productOrderPurchase.product.code.lowercase().contains(charSearch.lowercase()) || productOrderPurchase.product.description.lowercase().contains(charSearch.lowercase())
                }
                productOrderPurchaseListFiltered.clear()
                productOrderPurchaseListFiltered.addAll(OrderPurchaseList)
                return FilterResults().apply { values = productOrderPurchaseListFiltered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productOrderPurchaseListFiltered = results?.values as ArrayList<ProductOrderPurchase>
                notifyDataSetChanged()
            }
        }
    }
}
